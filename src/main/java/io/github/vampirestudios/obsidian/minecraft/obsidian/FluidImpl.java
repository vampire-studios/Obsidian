package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.quiltmc.qsl.fluid.api.QuiltFluidBlock;
import org.quiltmc.qsl.fluid.impl.QuiltFluid;

import java.util.List;

public abstract class FluidImpl extends QuiltFluid {

	/**
	 * The level property of the fluid currently being built. {@link #createFluidStateDefinition} runs
	 * from the {@link Fluid} constructor, before this subclass can assign any field, so the property has
	 * to be handed over out-of-band — the same hand-off {@link BlockImpl} uses for its block properties.
	 */
	private static final ThreadLocal<IntegerProperty> CONSTRUCTING_LEVEL =
			ThreadLocal.withInitial(() -> IntegerProperty.create("level", 0, 8));

	private final io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid fluid;
	private final IntegerProperty level;

	// Assigned by register(), which is the only thing allowed to build a fluid pair. Vanilla calls
	// getSource()/getFlowing()/getBucket() constantly at runtime, so none of them may register anything.
	private QuiltFluidBlock fluidBlock;
	private Still source;
	private Flowing flowing;
	private Item bucket;

	protected FluidImpl(io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid fluid) {
		this.fluid = fluid;
		this.level = CONSTRUCTING_LEVEL.get();
	}

	/**
	 * Builds and registers one fluid: its still and flowing forms, its block, and its bucket.
	 *
	 * <p>The still form is the one everything else points at, so it has to exist before the block and
	 * the bucket are built, and both forms have to be wired to each other before either is registered —
	 * the registry freeze walks the fluid's states as soon as it is added.
	 *
	 * @return the registered still fluid
	 */
	public static Still register(io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid fluid) {
		Identifier flowingId = Utils.appendToPath(fluid.id, "_flowing");
		Identifier bucketId = Utils.appendToPath(fluid.id, "_bucket");

		CONSTRUCTING_LEVEL.set(IntegerProperty.create("level", 0, fluid.maxFluidLevel));
		Still still;
		Flowing flowing;
		try {
			still = new Still(fluid);
			flowing = new Flowing(fluid);
		} finally {
			CONSTRUCTING_LEVEL.remove();
		}

		QuiltFluidBlock block = new QuiltFluidBlock(still, BlockBehaviour.Properties.ofLegacyCopy(Blocks.WATER)
				.setId(ResourceKey.create(Registries.BLOCK, fluid.id)));
		Item bucket = new BucketItem(still, new Item.Properties()
				.stacksTo(1)
				.setId(ResourceKey.create(Registries.ITEM, bucketId)));

		for (FluidImpl form : List.of(still, flowing)) {
			form.fluidBlock = block;
			form.source = still;
			form.flowing = flowing;
			form.bucket = bucket;
		}

		Registry.register(BuiltInRegistries.FLUID, fluid.id, still);
		Registry.register(BuiltInRegistries.FLUID, flowingId, flowing);
		Registry.register(BuiltInRegistries.BLOCK, fluid.id, block);
		Registry.register(BuiltInRegistries.ITEM, bucketId, bucket);

		return still;
	}

	/** The level property this fluid's states were built with. */
	public IntegerProperty getLevelProperty() {
		return this.level;
	}

	@Override
	public Fluid getFlowing() {
		return this.flowing;
	}

	@Override
	public Fluid getSource() {
		return this.source;
	}

	@Override
	public Item getBucket() {
		return this.bucket;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return this.fluidBlock.defaultBlockState().setValue(BlockStateProperties.LEVEL, getLegacyLevel(state));
	}

	public io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid getFluid() {
		return this.fluid;
	}

	@Override
	public int getColor(FluidState state, Level world, BlockPos pos) {
		return Integer.parseInt(this.fluid.fluidColor);
	}

	@Override
	public int getFogColor(FluidState state, Entity affected) {
		return Integer.parseInt(this.fluid.fluidFogColor);
	}

	@Override
	public boolean canExtinguish(FluidState state, Entity entity) {
		return this.fluid.canExtinguish;
	}

	@Override
	public boolean canIgnite(FluidState state, Entity affected) {
		return this.fluid.canIgnite;
	}

	@Override
	public float getPushStrength(FluidState state, Entity affected) {
		return this.fluid.pushStrengthChangesWhenWarm
				? affected.level().dimensionType().attributes().contains(EnvironmentAttributes.FAST_LAVA) ? this.fluid.pushStrengthUltrawarm : this.fluid.pushStrength
				: this.fluid.pushStrength;
	}

	@Override
	public float getHorizontalViscosity(FluidState state, Entity affected) {
		return this.fluid.horizontalViscosity;
	}

	@Override
	protected int getSlopeFindDistance(LevelReader worldView) {
		return this.fluid.flowSpeedChangesWhenWarm
				? worldView.dimensionType().attributes().contains(EnvironmentAttributes.FAST_LAVA) ? this.fluid.flowSpeedUltrawarm : this.fluid.flowSpeed
				: this.fluid.flowSpeed;
	}

	@Override
	protected boolean canConvertToSource(ServerLevel serverLevel) {
		return this.fluid.canBeInfinite;
	}

	@Override
	protected int getDropOff(LevelReader worldView) {
		return this.fluid.levelDecreasePerBlockChangesWhenWarm
				? worldView.dimensionType().attributes().contains(EnvironmentAttributes.FAST_LAVA) ? this.fluid.levelDecreasePerBlockUltrawarm : this.fluid.levelDecreasePerBlock
				: this.fluid.levelDecreasePerBlock;
	}

	@Override
	public int getTickDelay(LevelReader worldView) {
		return this.fluid.tickRateChangesWhenWarm
				? worldView.dimensionType().attributes().contains(EnvironmentAttributes.FAST_LAVA) ? this.fluid.tickRateUltrawarm : this.fluid.tickRate
				: this.fluid.tickRate;
	}

	@Override
	public float getVerticalViscosity(FluidState state, Entity affected) {
		return this.fluid.verticalViscosity;
	}

	@Override
	public boolean bobberFloats(FluidState state, FishingHook affected) {
		return this.fluid.fishingBobberFloats;
	}

	@Override
	public boolean canFish(FluidState state, FishingHook affected) {
		return this.fluid.canFish;
	}

	@Override
	public float getDefaultDensity(Level world, BlockPos blockpos) {
		return this.fluid.density;
	}

	@Override
	public float getDefaultTemperature(Level world, BlockPos blockpos) {
		return this.fluid.temperature;
	}

	@Override
	public float getFallDamageReduction(Entity entity) {
		if (this.fluid.fallDamageReductionType != null) {
			return switch (this.fluid.fallDamageReductionType) {
				case NONE -> NO_FALL_DAMAGE_REDUCTION;
				case QUARTER -> QUARTER_FALL_DAMAGE_REDUCTION;
				case HALF -> HALF_FALL_DAMAGE_REDUCTION;
				case THREE_QUARTER -> THREE_QUARTER_FALL_DAMAGE_REDUCTION;
				case FULL -> FULL_FALL_DAMAGE_REDUCTION;
			};
		} else {
			return this.fluid.fallDamageReduction;
		}
	}

	@Nullable
	@Override
	public SoundEvent getSplashSound(Entity splashing, Vec3 splashPos, RandomSource random) {
		return BuiltInRegistries.SOUND_EVENT.getValue(this.fluid.splashSound);
	}

	@Nullable
	@Override
	public SoundEvent getHighSpeedSplashSound(Entity splashing, Vec3 splashPos, RandomSource random) {
		return BuiltInRegistries.SOUND_EVENT.getValue(this.fluid.highSpeedSplashSound);
	}

	@Nullable
	@Override
	public ParticleOptions getSplashParticle(Entity splashing, Vec3 splashPos, RandomSource random) {
		return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.getValue(this.fluid.splashParticle);
	}

	@Nullable
	@Override
	public ParticleOptions getBubbleParticle(Entity splashing, Vec3 splashPos, RandomSource random) {
		return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.getValue(this.fluid.bubbleParticle);
	}

	@Override
	public ResourceKey<LootTable> getFishingLootTable() {
		return this.fluid.fishingLootTable;
	}

	@Override
	public boolean canBoatSwimOn() {
		return super.canBoatSwimOn();
	}

	@Nullable
	@Override
	protected ParticleOptions getDripParticle() {
		return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.getValue(this.fluid.particleType);
	}

	@Override
	public boolean allowSprintSwimming(FluidState state, Entity affected) {
		return this.fluid.allowSprintSwimming;
	}

	public static class Flowing extends FluidImpl {

		public Flowing(io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid fluid) {
			super(fluid);
		}

		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			// Runs from the superclass constructor, so the field is not assigned yet — read the hand-off.
			builder.add(CONSTRUCTING_LEVEL.get());
		}

		@Override
		public int getAmount(FluidState fluidState) {
			return fluidState.getValue(this.getLevelProperty());
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Still extends FluidImpl {

		public Still(io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid fluid) {
			super(fluid);
		}

		@Override
		public int getAmount(FluidState fluidState) {
			return this.getFluid().maxFluidLevel;
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

	}

}

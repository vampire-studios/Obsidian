package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.fluid.api.QuiltFluidBlock;
import org.quiltmc.qsl.fluid.impl.QuiltFluid;

public abstract class FluidImpl extends QuiltFluid {
	public static IntegerProperty LEVEL;

	private final QuiltFluidBlock fluidBlock;
	private final io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid fluid;

	public FluidImpl(io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid fluid) {
		LEVEL = IntegerProperty.create("level", 0, fluid.maxFluidLevel);
		this.fluid = fluid;
		this.fluidBlock = Registry.register(BuiltInRegistries.BLOCK, fluid.name.id, new QuiltFluidBlock(this, FabricBlockSettings.copyOf(Blocks.WATER)));
	}

	@Override
	public Fluid getFlowing() {
		return Registry.register(BuiltInRegistries.FLUID, Utils.appendToPath(this.fluid.name.id, "_flowing"), new Flowing(this.fluid));
	}

	@Override
	public Fluid getSource() {
		return Registry.register(BuiltInRegistries.FLUID, this.fluid.name.id, new Still(this.fluid));
	}

	@Override
	public Item getBucket() {
		return Registry.register(BuiltInRegistries.ITEM, Utils.appendToPath(this.fluid.name.id, "_bucket"), new BucketItem(this, new Item.Properties()
				.stacksTo(1)));
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
				? affected.level.dimensionType().ultraWarm() ? this.fluid.pushStrengthUltrawarm : this.fluid.pushStrength
				: this.fluid.pushStrength;
	}

	@Override
	public float getHorizontalViscosity(FluidState state, Entity affected) {
		return this.fluid.horizontalViscosity;
	}

	@Override
	protected int getSlopeFindDistance(LevelReader worldView) {
		return this.fluid.flowSpeedChangesWhenWarm
				? worldView.dimensionType().ultraWarm() ? this.fluid.flowSpeedUltrawarm : this.fluid.flowSpeed
				: this.fluid.flowSpeed;
	}

	@Override
	protected boolean canConvertToSource(Level world) {
		return this.fluid.canBeInfinite;
	}

	@Override
	protected int getDropOff(LevelReader worldView) {
		return this.fluid.levelDecreasePerBlockChangesWhenWarm
				? worldView.dimensionType().ultraWarm() ? this.fluid.levelDecreasePerBlockUltrawarm : this.fluid.levelDecreasePerBlock
				: this.fluid.levelDecreasePerBlock;
	}

	@Override
	public int getTickDelay(LevelReader worldView) {
		return this.fluid.tickRateChangesWhenWarm
				? worldView.dimensionType().ultraWarm() ? this.fluid.tickRateUltrawarm : this.fluid.tickRate
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
		return BuiltInRegistries.SOUND_EVENT.get(this.fluid.splashSound);
	}

	@Nullable
	@Override
	public SoundEvent getHighSpeedSplashSound(Entity splashing, Vec3 splashPos, RandomSource random) {
		return BuiltInRegistries.SOUND_EVENT.get(this.fluid.highSpeedSplashSound);
	}

	@Nullable
	@Override
	public ParticleOptions getSplashParticle(Entity splashing, Vec3 splashPos, RandomSource random) {
		return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(this.fluid.splashParticle);
	}

	@Nullable
	@Override
	public ParticleOptions getBubbleParticle(Entity splashing, Vec3 splashPos, RandomSource random) {
		return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(this.fluid.bubbleParticle);
	}

	@Override
	public ResourceLocation getFishingLootTable() {
		return this.fluid.fishingLootTable;
	}

	@Override
	public boolean canBoatSwimOn() {
		return super.canBoatSwimOn();
	}

	@Nullable
	@Override
	protected ParticleOptions getDripParticle() {
		return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(this.fluid.particleType);
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
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState fluidState) {
			return fluidState.getValue(LEVEL);
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

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.fluid.api.QuiltFluidBlock;
import org.quiltmc.qsl.fluid.impl.QuiltFluid;

public abstract class FluidImpl extends QuiltFluid {
	public static IntProperty LEVEL;

	private final QuiltFluidBlock fluidBlock;
	private final io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid fluid;

	public FluidImpl(io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid fluid) {
		LEVEL = IntProperty.of("level", 0, fluid.maxFluidLevel);
		this.fluid = fluid;
		this.fluidBlock = Registry.register(Registries.BLOCK, fluid.name.id, new QuiltFluidBlock(this, FabricBlockSettings.copyOf(Blocks.WATER)));
	}

	@Override
	public Fluid getFlowing() {
		return Registry.register(Registries.FLUID, Utils.appendToPath(this.fluid.name.id, "_flowing"), new Flowing(this.fluid));
	}

	@Override
	public Fluid getStill() {
		return Registry.register(Registries.FLUID, this.fluid.name.id, new Still(this.fluid));
	}

	@Override
	public Item getBucketItem() {
		return Registry.register(Registries.ITEM, Utils.appendToPath(this.fluid.name.id, "_bucket"), new BucketItem(this, new Item.Settings()
				.maxCount(1)));
	}

	@Override
	protected BlockState toBlockState(FluidState state) {
		return this.fluidBlock.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(state));
	}

	public io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid getFluid() {
		return this.fluid;
	}

	@Override
	public int getColor(FluidState state, World world, BlockPos pos) {
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
				? affected.world.getDimension().ultrawarm() ? this.fluid.pushStrengthUltrawarm : this.fluid.pushStrength
				: this.fluid.pushStrength;
	}

	@Override
	public float getHorizontalViscosity(FluidState state, Entity affected) {
		return this.fluid.horizontalViscosity;
	}

	@Override
	protected int getFlowSpeed(WorldView worldView) {
		return this.fluid.flowSpeedChangesWhenWarm
				? worldView.getDimension().ultrawarm() ? this.fluid.flowSpeedUltrawarm : this.fluid.flowSpeed
				: this.fluid.flowSpeed;
	}

	@Override
	protected boolean isInfinite(World world) {
		return this.fluid.canBeInfinite;
	}

	@Override
	protected int getLevelDecreasePerBlock(WorldView worldView) {
		return this.fluid.levelDecreasePerBlockChangesWhenWarm
				? worldView.getDimension().ultrawarm() ? this.fluid.levelDecreasePerBlockUltrawarm : this.fluid.levelDecreasePerBlock
				: this.fluid.levelDecreasePerBlock;
	}

	@Override
	public int getTickRate(WorldView worldView) {
		return this.fluid.tickRateChangesWhenWarm
				? worldView.getDimension().ultrawarm() ? this.fluid.tickRateUltrawarm : this.fluid.tickRate
				: this.fluid.tickRate;
	}

	@Override
	public float getVerticalViscosity(FluidState state, Entity affected) {
		return this.fluid.verticalViscosity;
	}

	@Override
	public boolean bobberFloats(FluidState state, FishingBobberEntity affected) {
		return this.fluid.fishingBobberFloats;
	}

	@Override
	public boolean canFish(FluidState state, FishingBobberEntity affected) {
		return this.fluid.canFish;
	}

	@Override
	public float getDefaultDensity(World world, BlockPos blockpos) {
		return this.fluid.density;
	}

	@Override
	public float getDefaultTemperature(World world, BlockPos blockpos) {
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
	public SoundEvent getSplashSound(Entity splashing, Vec3d splashPos, Random random) {
		return Registries.SOUND_EVENT.get(this.fluid.splashSound);
	}

	@Nullable
	@Override
	public SoundEvent getHighSpeedSplashSound(Entity splashing, Vec3d splashPos, Random random) {
		return Registries.SOUND_EVENT.get(this.fluid.highSpeedSplashSound);
	}

	@Nullable
	@Override
	public ParticleEffect getSplashParticle(Entity splashing, Vec3d splashPos, Random random) {
		return (DefaultParticleType) Registries.PARTICLE_TYPE.get(this.fluid.splashParticle);
	}

	@Nullable
	@Override
	public ParticleEffect getBubbleParticle(Entity splashing, Vec3d splashPos, Random random) {
		return (DefaultParticleType) Registries.PARTICLE_TYPE.get(this.fluid.bubbleParticle);
	}

	@Override
	public Identifier getFishingLootTable() {
		return this.fluid.fishingLootTable;
	}

	@Override
	public boolean canBoatSwimOn() {
		return super.canBoatSwimOn();
	}

	@Nullable
	@Override
	protected ParticleEffect getParticle() {
		return (DefaultParticleType) Registries.PARTICLE_TYPE.get(this.fluid.particleType);
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
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getLevel(FluidState fluidState) {
			return fluidState.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState state) {
			return false;
		}
	}

	public static class Still extends FluidImpl {

		public Still(io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid fluid) {
			super(fluid);
		}

		@Override
		public int getLevel(FluidState fluidState) {
			return this.getFluid().maxFluidLevel;
		}

		@Override
		public boolean isStill(FluidState state) {
			return true;
		}

	}

}

package io.github.vampirestudios.obsidian.api.obsidian.fluid;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.loot.LootTables;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Range;
import org.quiltmc.qsl.fluid.api.QuiltFlowableFluidExtensions;

public class Fluid {

	public ParentFluid parent;

	public NameInformation name;
	public String fluidColor;
	public String fluidFogColor;
	public boolean allowSprintSwimming;
	public boolean canExtinguish;
	public boolean canIgnite;
	@Range(from = 1, to = 15) public int maxFluidLevel;
	public float pushStrength;
	public float pushStrengthUltrawarm;
	public boolean pushStrengthChangesWhenWarm;
	public float fallDamageReduction;
	public FallDamageReduction fallDamageReductionType;
	public boolean fishingBobberFloats;
	public boolean canFish;
	public float horizontalViscosity;
	public float verticalViscosity;
	public float density;
	public float temperature;
	public boolean canBeInfinite;
	public int flowSpeed;
	public int flowSpeedUltrawarm;
	public boolean flowSpeedChangesWhenWarm;
	public int levelDecreasePerBlock;
	public int levelDecreasePerBlockUltrawarm;
	public boolean levelDecreasePerBlockChangesWhenWarm;
	public int tickRate;
	public int tickRateUltrawarm;
	public boolean tickRateChangesWhenWarm;
	public boolean randomTicking;
	public float blastResistance;
	public boolean boatFloats;

	public Identifier splashSound;
	public Identifier highSpeedSplashSound;

	public Identifier particleType;
	public Identifier splashParticle;
	public Identifier bubbleParticle;

	public Identifier fishingLootTable;

	public Fluid(ParentFluid parent, NameInformation name, int fluidColor, int fluidFogColor, boolean allowSprintSwimming, boolean canExtinguish, boolean canIgnite, @Range(from = 1, to = 15) int maxFluidLevel, float pushStrength, float pushStrengthUltrawarm, boolean pushStrengthChangesWhenWarm, float fallDamageReduction, FallDamageReduction fallDamageReductionType, boolean fishingBobberFloats, boolean canFish, float horizontalViscosity, float verticalViscosity, float density, float temperature, boolean canBeInfinite, int flowSpeed, int flowSpeedUltrawarm, boolean flowSpeedChangesWhenWarm, int levelDecreasePerBlock, int levelDecreasePerBlockUltrawarm, boolean levelDecreasePerBlockChangesWhenWarm, int tickRate, int tickRateUltrawarm, boolean tickRateChangesWhenWarm, boolean randomTicking, float blastResistance, boolean boatFloats, Identifier splashSound, Identifier highSpeedSplashSound, Identifier particleType, Identifier splashParticle, Identifier bubbleParticle, Identifier fishingLootTable) {
		this.parent = parent;
		this.name = name;
		this.fluidColor = Integer.toString(fluidColor);
		this.fluidFogColor = Integer.toString(fluidFogColor);
		this.allowSprintSwimming = allowSprintSwimming;
		this.canExtinguish = canExtinguish;
		this.canIgnite = canIgnite;
		this.maxFluidLevel = maxFluidLevel;
		this.pushStrength = pushStrength;
		this.pushStrengthUltrawarm = pushStrengthUltrawarm;
		this.pushStrengthChangesWhenWarm = pushStrengthChangesWhenWarm;
		this.fallDamageReduction = fallDamageReduction;
		this.fallDamageReductionType = fallDamageReductionType;
		this.fishingBobberFloats = fishingBobberFloats;
		this.canFish = canFish;
		this.horizontalViscosity = horizontalViscosity;
		this.verticalViscosity = verticalViscosity;
		this.density = density;
		this.temperature = temperature;
		this.canBeInfinite = canBeInfinite;
		this.flowSpeed = flowSpeed;
		this.flowSpeedUltrawarm = flowSpeedUltrawarm;
		this.flowSpeedChangesWhenWarm = flowSpeedChangesWhenWarm;
		this.levelDecreasePerBlock = levelDecreasePerBlock;
		this.levelDecreasePerBlockUltrawarm = levelDecreasePerBlockUltrawarm;
		this.levelDecreasePerBlockChangesWhenWarm = levelDecreasePerBlockChangesWhenWarm;
		this.tickRate = tickRate;
		this.tickRateUltrawarm = tickRateUltrawarm;
		this.tickRateChangesWhenWarm = tickRateChangesWhenWarm;
		this.randomTicking = randomTicking;
		this.blastResistance = blastResistance;
		this.boatFloats = boatFloats;
		this.splashSound = splashSound;
		this.highSpeedSplashSound = highSpeedSplashSound;
		this.particleType = particleType;
		this.splashParticle = splashParticle;
		this.bubbleParticle = bubbleParticle;
		this.fishingLootTable = fishingLootTable;
	}

	public Fluid waterLike() {
		return new Fluid(
				ParentFluid.WATER,
				name,
				QuiltFlowableFluidExtensions.WATER_FOG_COLOR,
				QuiltFlowableFluidExtensions.WATER_FOG_COLOR,
				true,
				true,
				false,
				8,
				QuiltFlowableFluidExtensions.WATER_PUSH_STRENGTH,
				0F,
				false,
				QuiltFlowableFluidExtensions.FULL_FALL_DAMAGE_REDUCTION,
				FallDamageReduction.FULL,
				true,
				true,
				QuiltFlowableFluidExtensions.WATER_VISCOSITY,
				QuiltFlowableFluidExtensions.WATER_VISCOSITY,
				QuiltFlowableFluidExtensions.WATER_DENSITY,
				QuiltFlowableFluidExtensions.WATER_TEMPERATURE,
				true,
				4,
				0,
				false,
				1,
				1,
				false,
				5,
				5,
				false,
				false,
				100.0F,
				true,
				Registry.SOUND_EVENT.getId(SoundEvents.ENTITY_PLAYER_SPLASH),
				Registry.SOUND_EVENT.getId(SoundEvents.ENTITY_PLAYER_SPLASH_HIGH_SPEED),
				Registry.PARTICLE_TYPE.getId(ParticleTypes.DRIPPING_WATER),
				Registry.PARTICLE_TYPE.getId(ParticleTypes.WATER_SPLASH),
				Registry.PARTICLE_TYPE.getId(ParticleTypes.BUBBLE),
				LootTables.FISHING_GAMEPLAY
		);
	}

	public Fluid lavaLike() {
		return new Fluid(
				ParentFluid.LAVA,
				name,
				QuiltFlowableFluidExtensions.LAVA_FOG_COLOR,
				QuiltFlowableFluidExtensions.LAVA_FOG_COLOR,
				false,
				false,
				true,
				8,
				QuiltFlowableFluidExtensions.LAVA_PUSH_STRENGTH_OVERWORLD,
				QuiltFlowableFluidExtensions.LAVA_PUSH_STRENGTH_ULTRAWARM,
				true,
				QuiltFlowableFluidExtensions.HALF_FALL_DAMAGE_REDUCTION,
				FallDamageReduction.HALF,
				false,
				false,
				QuiltFlowableFluidExtensions.LAVA_VISCOSITY,
				QuiltFlowableFluidExtensions.WATER_VISCOSITY,
				QuiltFlowableFluidExtensions.LAVA_DENSITY,
				QuiltFlowableFluidExtensions.LAVA_TEMPERATURE,
				true,
				2,
				4,
				true,
				1,
				2,
				true,
				30,
				10,
				true,
				true,
				100.0F,
				false,
				null,
				null,
				Registry.PARTICLE_TYPE.getId(ParticleTypes.DRIPPING_LAVA),
				null,
				null,
				LootTables.EMPTY
		);
	}

	public enum FallDamageReduction {
		NONE,
		QUARTER,
		HALF,
		THREE_QUARTER,
		FULL
	}

	public enum ParentFluid {
		WATER,
		LAVA,
		NONE
	}

}

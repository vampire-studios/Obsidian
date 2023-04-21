/*
 * Copyright 2022 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.fluid.api;

import javax.annotation.Nullable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;

public interface QuiltFlowableFluidExtensions {

	float WATER_VISCOSITY = 0.8f;
	float LAVA_VISCOSITY = 0.5f;
	float WATER_PUSH_STRENGTH = 0.014f;
	float LAVA_PUSH_STRENGTH_OVERWORLD = 7 / 3000f;
	float LAVA_PUSH_STRENGTH_ULTRAWARM = 0.007f;
	float WATER_DENSITY = 1000f;
	float LAVA_DENSITY = 3100f;
	float WATER_TEMPERATURE = 300f;
	float LAVA_TEMPERATURE = 1500f;
	float WATER_FOG_START = -8.0f;
	float NO_FALL_DAMAGE_REDUCTION = 0f;
	float QUARTER_FALL_DAMAGE_REDUCTION = 0.15f;
	float HALF_FALL_DAMAGE_REDUCTION = 0.5f;
	float THREE_QUARTER_FALL_DAMAGE_REDUCTION = 0.45f;
	float FULL_FALL_DAMAGE_REDUCTION = 1f;

	int WATER_FOG_COLOR = -1;
	int LAVA_FOG_COLOR = 0x991900;

	ResourceLocation WATER_FISHING_LOOT_TABLE = BuiltInLootTables.FISHING;

	/**
	 * The color of this fluid.
	 */
	default int getColor(FluidState state, Level world, BlockPos pos) {
		return world.getBiome(pos).value().getWaterColor();
	}

	/**
	 * 0.8F is the default for water
	 * 0.5F is the default for lava
	 *
	 * @see QuiltFlowableFluidExtensions#WATER_VISCOSITY
	 * @see QuiltFlowableFluidExtensions#LAVA_VISCOSITY
	 */
	default float getHorizontalViscosity(FluidState state, Entity affected) {
		return WATER_VISCOSITY;
	}

	/**
	 * Default for water and lava is 0.8F
	 *
	 * @see QuiltFlowableFluidExtensions#WATER_VISCOSITY
	 */
	default float getVerticalViscosity(FluidState state, Entity affected) {
		if(state.is(FluidTags.LAVA)) return LAVA_VISCOSITY;
		return WATER_VISCOSITY;
	}

	/**
	 * 0.014F is the default for water
	 * 7 / 3000 is the default for lava in the overworld
	 * 0.007F is the default for lava in the nether
	 *
	 * @see QuiltFlowableFluidExtensions#WATER_PUSH_STRENGTH
	 * @see QuiltFlowableFluidExtensions#LAVA_PUSH_STRENGTH_OVERWORLD
	 * @see QuiltFlowableFluidExtensions#LAVA_PUSH_STRENGTH_ULTRAWARM
	 */
	default float getPushStrength(FluidState state, Entity affected) {
		if(state.is(FluidTags.LAVA)) {
			return affected.level.dimensionType().ultraWarm() ? LAVA_PUSH_STRENGTH_ULTRAWARM : LAVA_PUSH_STRENGTH_OVERWORLD;
		}
		return WATER_PUSH_STRENGTH;
	}

	/**
	 * Toggles weather or not a player can sprint swim in your fluid
	 */
	default boolean allowSprintSwimming(FluidState state, Entity affected) {
		return true;
	}

	/**
	 * @return an updated horizontalViscosity, specifically regarding potion effects
	 */
	default float modifyEntityHorizontalViscosity(LivingEntity affected, float horizontalViscosity) {
		if (affected.hasEffect(MobEffects.DOLPHINS_GRACE)) {
			horizontalViscosity = 0.96f;
		}
		return horizontalViscosity;
	}

	default boolean enableDoubleTapSpacebarSwimming(FluidState state, Entity affected) {
		return true;
	}

	default boolean bobberFloats(FluidState state, FishingHook affected) {
		return true;
	}

	default boolean canFish(FluidState state, FishingHook affected) {
		return true;
	}

	default boolean canExtinguish(FluidState state, Entity affected) {
		return true;
	}

	default boolean canIgnite(FluidState state, Entity affected) {
		return !canExtinguish(state, affected);
	}

	default int getNextAirSubmerged(int air, LivingEntity entity, RandomSource random) {
		int i = EnchantmentHelper.getRespiration(entity);
		return i > 0 && random.nextInt(i + 1) > 0 ? air : air - 1;
	}

	/**
	 * Density in kilograms per cubic meter
	 * 1000 is the default for water
	 * 3100 is the default for lava
	 *
	 * @see QuiltFlowableFluidExtensions#WATER_DENSITY
	 * @see QuiltFlowableFluidExtensions#LAVA_DENSITY
	 */
	default float getDefaultDensity(Level world, BlockPos blockpos) {
		if(world.getFluidState(blockpos).is(FluidTags.LAVA)) return LAVA_DENSITY;
		return WATER_DENSITY;
	}

	/**
	 * Temperature in Kelvin
	 * 300 is the default for water
	 * 1500 is the default for lava
	 *
	 * @see QuiltFlowableFluidExtensions#WATER_TEMPERATURE
	 * @see QuiltFlowableFluidExtensions#LAVA_TEMPERATURE
	 */
	default float getDefaultTemperature(Level world, BlockPos blockpos) {
		if(world.getFluidState(blockpos).is(FluidTags.LAVA)) return LAVA_TEMPERATURE;
		return WATER_TEMPERATURE;
	}

	/**
	 * 0 waters default equals complete fall damage reduction
	 * 0.5 lavas default equals half fall damage reduction
	 * 1 is no fall damage reduction whatsoever
	 *
	 * @see QuiltFlowableFluidExtensions#FULL_FALL_DAMAGE_REDUCTION
	 * @see QuiltFlowableFluidExtensions#HALF_FALL_DAMAGE_REDUCTION
	 * @see QuiltFlowableFluidExtensions#NO_FALL_DAMAGE_REDUCTION
	 */
	default float getFallDamageReduction(Entity entity) {
		BlockPos entityPos = entity.blockPosition();

		if(entity.level.getFluidState(entityPos).is(FluidTags.LAVA)) return HALF_FALL_DAMAGE_REDUCTION;

		return FULL_FALL_DAMAGE_REDUCTION;
	}

	/**
	 * Water fog color is special cased to be -1. Any other
	 * value returned will be treated as a normal color.
	 */
	default int getFogColor(FluidState state, Entity affected) {
		if(state.is(FluidTags.LAVA)) return LAVA_FOG_COLOR;
		return WATER_FOG_COLOR;
	}

	/**
	 * @see QuiltFlowableFluidExtensions#WATER_FOG_START
	 */
	default float getFogStart(FluidState state, Entity affected, float viewDistance) {
		return WATER_FOG_START;
	}

	/**
	 * Only reason default impl is complicated is because
	 * water has a fade-in effect. Feel free to disregard
	 * it and simply return a value.
	 */
	default float getFogEnd(FluidState state, Entity affected, float viewDistance) {
		float distance = 192.0F;
		if (affected instanceof LocalPlayer player) {
			distance *= Math.max(0.25F, player.getWaterVision());
			/*Biome biome = player.world.getBiome(player.getBlockPos());
			if (biome.getCategory() == Biome.Category.SWAMP) {
				distance *= 0.85F;
			}*/
		}
		return distance * 0.5f;
	}

	@Nullable
	default SoundEvent getSplashSound(Entity splashing, Vec3 splashPos, RandomSource random) {
		return SoundEvents.PLAYER_SPLASH;
	}

	/**
	 *	This method is used, when the sound emitted from falling into the fluid is greated than 0.25f.
	 *	Logic found in FlowableFluidExtensions.onSplash
	 */
	@Nullable
	default SoundEvent getHighSpeedSplashSound(Entity splashing, Vec3 splashPos, RandomSource random) {
		return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
	}

	@Nullable
	default ParticleOptions getSplashParticle(Entity splashing, Vec3 splashPos, RandomSource random) {
		return ParticleTypes.SPLASH;
	}

	@Nullable
	default ParticleOptions getBubbleParticle(Entity splashing, Vec3 splashPos, RandomSource random) {
		return ParticleTypes.BUBBLE;
	}

	@Nullable
	default GameEvent getSplashGameEvent(Entity splashing, Vec3 splashPos, RandomSource random) {
		return GameEvent.SPLASH;
	}

	default ResourceLocation getFishingLootTable() {
		return WATER_FISHING_LOOT_TABLE;
	}

	default boolean canBoatSwimOn() {
		return true;
	}

	// Overriding of any methods below this comment is generally unnecessary,
	// and only made available to cover as many cases as possible.
	default void spawnSplashParticles(Entity splashing, Vec3 splashPos, RandomSource random) {
		for (int i = 0; i < 1.0f + splashing.getDimensions(splashing.getPose()).width * 20.0f; ++i) {
			double xOffset = (random.nextDouble() * 2.0 - 1.0) * (double) splashing.getDimensions(splashing.getPose()).width;
			double zOffset = (random.nextDouble() * 2.0 - 1.0) * (double) splashing.getDimensions(splashing.getPose()).width;
			int yFloor = Mth.floor(splashing.getY());
			ParticleOptions particle = getSplashParticle(splashing, splashPos, random);
			if (particle != null) {
				splashing.level.addParticle(
						particle,
						splashing.getX() + xOffset,
						yFloor + 1.0f,
						splashing.getZ() + zOffset,
						splashPos.x,
						splashPos.y,
						splashPos.z
				);
			}
		}
	}

	default void spawnBubbleParticles(Entity splashing, Vec3 splashPos, RandomSource random) {
		for (int i = 0; i < 1.0f + splashing.getDimensions(splashing.getPose()).width * 20.0f; ++i) {
			double xOffset = (random.nextDouble() * 2.0 - 1.0) * (double) splashing.getDimensions(splashing.getPose()).width;
			double zOffset = (random.nextDouble() * 2.0 - 1.0) * (double) splashing.getDimensions(splashing.getPose()).width;
			int yFloor = Mth.floor(splashing.getY());
			ParticleOptions particle = getBubbleParticle(splashing, splashPos, random);
			if (particle != null) {
				splashing.level.addParticle(
						particle,
						splashing.getX() + xOffset,
						yFloor + 1.0f,
						splashing.getZ() + zOffset,
						splashPos.x,
						splashPos.y - random.nextDouble() * 0.2,
						splashPos.z
				);
			}

		}
	}

	default void onSplash(Level world, Vec3 pos, Entity splashing, RandomSource random) {
		Entity passenger = splashing.isVehicle() && splashing.getControllingPassenger() != null ? splashing.getControllingPassenger() : splashing;
		float volumeMultiplier = passenger == splashing ? 0.2f : 0.9f;
		Vec3 velocity = passenger.getDeltaMovement();
		double volume = Math.min(
				1.0f,
				Math.sqrt(velocity.x * velocity.x * 0.2 + velocity.y * velocity.y + velocity.z * velocity.z * 0.2D) * volumeMultiplier
		);

		SoundEvent sound = volume < 0.25f ? getSplashSound(splashing, pos, random) : getHighSpeedSplashSound(splashing, pos, random);
		if (sound != null) {
			splashing.playSound(
					sound,
					(float) volume,
					1.0f + (random.nextFloat() - random.nextFloat()) * 0.4f
			);
		}

		spawnBubbleParticles(splashing, pos, random);
		spawnSplashParticles(splashing, pos, random);

		GameEvent splash = getSplashGameEvent(splashing, pos, random);
		if (splash != null) {
			splashing.gameEvent(splash);
		}
	}

	/**
	 * Here you can modify viscosity and speed based on enchantments.
	 *
	 * @return a Helper class which contains the calculated horizontalViscosity and speed. The class contains two fields, which are both floats.
	 */
	default FluidEnchantmentHelper customEnchantmentEffects(Vec3 movementInput, LivingEntity entity, float horizontalViscosity, float speed) {
		float depthStriderLevel = EnchantmentHelper.getDepthStrider(entity);
		if (depthStriderLevel > 3.0f) {
			depthStriderLevel = 3.0f;
		}

		if (!entity.isOnGround()) {
			depthStriderLevel *= 0.5f;
		}

		if (depthStriderLevel > 0.0f) {
			horizontalViscosity += (0.546f - horizontalViscosity) * depthStriderLevel / 3.0f;
			speed += (entity.getSpeed() - speed) * depthStriderLevel / 3.0f;
		}

		return new FluidEnchantmentHelper(horizontalViscosity, speed);
	}

	default void doDrownEffects(FluidState state, LivingEntity drowning, RandomSource random) {
		boolean isPlayer = drowning instanceof Player;
		boolean invincible = isPlayer && ((Player) drowning).getAbilities().invulnerable;
		if (!drowning.canBreatheUnderwater() && !MobEffectUtil.hasWaterBreathing(drowning) && !invincible) {
			drowning.setAirSupply(getNextAirSubmerged(drowning.getAirSupply(), drowning, random));
			// if out of air
			if (drowning.getAirSupply() == -20) {
				drowning.setAirSupply(0);
				Vec3 vec3d = drowning.getDeltaMovement();

				for (int i = 0; i < 8; ++i) {
					double f = random.nextDouble() - random.nextDouble();
					double g = random.nextDouble() - random.nextDouble();
					double h = random.nextDouble() - random.nextDouble();
					ParticleOptions particle = getBubbleParticle(drowning, drowning.position(), random);
					if (particle != null) {
						drowning.level.addParticle(
								particle,
								drowning.getX() + f,
								drowning.getY() + g,
								drowning.getZ() + h,
								vec3d.x, vec3d.y, vec3d.z);
					}
				}

				drowning.hurt(drowning.damageSources().drown(), 2.0F);
			}
		}

		// dismount vehicles that can't swim (horses)
		if (!drowning.level.isClientSide && drowning.isPassenger() && drowning.getVehicle() != null && !drowning.getVehicle().dismountsUnderwater()) {
			drowning.stopRiding();
		}
	}

}
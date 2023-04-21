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

package org.quiltmc.qsl.fluid.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;
import org.quiltmc.qsl.fluid.api.FluidEnchantmentHelper;
import org.quiltmc.qsl.fluid.api.QuiltFlowableFluidExtensions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LavaFluid.class)
public abstract class LavaFluidMixin extends FlowingFluid implements QuiltFlowableFluidExtensions {

	@Override
	public float getDefaultTemperature(Level world, BlockPos blockPos) {
		return LAVA_TEMPERATURE;
	}

	@Override
	public float getDefaultDensity(Level world, BlockPos blockPos) {
		return LAVA_DENSITY;
	}

	@Override
	public float getHorizontalViscosity(FluidState state, Entity effected) {
		return LAVA_VISCOSITY;
	}

	@Override
	public float getVerticalViscosity(FluidState state, Entity effected) {
		return WATER_VISCOSITY;
	}

	@Override
	public float getPushStrength(FluidState state, Entity effected) {
		return effected.level.dimensionType().ultraWarm() ? LAVA_PUSH_STRENGTH_ULTRAWARM : LAVA_PUSH_STRENGTH_OVERWORLD;
	}

	@Override
	public float getFallDamageReduction(Entity entity) {
		return HALF_FALL_DAMAGE_REDUCTION;
	}

	@Override
	public float getFogStart(FluidState state, Entity affected, float viewDistance) {
		if (affected.isSpectator()) {
			return -8.0f;
		} else if (affected instanceof LivingEntity living && living.hasEffect(MobEffects.FIRE_RESISTANCE)) {
			return 0.0f;
		} else {
			return 0.25f;
		}
	}

	@Override
	public float getFogEnd(FluidState state, Entity affected, float viewDistance) {
		if (affected.isSpectator()) {
			return viewDistance / 2;
		} else if (affected instanceof LivingEntity living && living.hasEffect(MobEffects.FIRE_RESISTANCE)) {
			return 3.0f;
		} else {
			return 1.0f;
		}
	}

	@Override
	public boolean canExtinguish(FluidState state, Entity effected) {
		return false;
	}

	@Override
	public int getColor(FluidState state, Level world, BlockPos pos) {
		return LAVA_FOG_COLOR;
	}

	@Override
	public boolean allowSprintSwimming(FluidState state, Entity affected) {
		return false;
	}

	@Override
	public float modifyEntityHorizontalViscosity(LivingEntity affected, float horizontalViscosity) {
		return horizontalViscosity;
	}

	@Override
	public boolean enableDoubleTapSpacebarSwimming(FluidState state, Entity affected) {
		return true;
	}

	@Override
	public void doDrownEffects(FluidState state, LivingEntity drowning, RandomSource random) {}

	@Override
	public int getFogColor(FluidState state, Entity affected) {
		return LAVA_FOG_COLOR;
	}

	@Override
	public SoundEvent getSplashSound(Entity splashing, Vec3 splashPos, RandomSource random) {
		return null;
	}

	@Override
	public SoundEvent getHighSpeedSplashSound(Entity splashing, Vec3 splashPos, RandomSource random) {
		return null;
	}

	@Override
	public ParticleOptions getSplashParticle(Entity splashing, Vec3 splashPos, RandomSource random) {
		return null;
	}

	@Override
	public ParticleOptions getBubbleParticle(Entity splashing, Vec3 splashPos, RandomSource random) {
		return null;
	}

	@Override
	public GameEvent getSplashGameEvent(Entity splashing, Vec3 splashPos, RandomSource random) {
		return null;
	}

	@Override
	public void spawnSplashParticles(Entity splashing, Vec3 splashPos, RandomSource random) {}

	@Override
	public void spawnBubbleParticles(Entity splashing, Vec3 splashPos, RandomSource random) {}

	@Override
	public void onSplash(Level world, Vec3 pos, Entity splashing, RandomSource random) {}

	@Override
	public FluidEnchantmentHelper customEnchantmentEffects(Vec3 movementInput, LivingEntity entity, float horizontalViscosity, float speed) {
		return new FluidEnchantmentHelper(horizontalViscosity, speed);
	}

	@Override
	public boolean canBoatSwimOn() {
		return false;
	}

	@Override
	public boolean bobberFloats(FluidState state, FishingHook affected) {
		return false;
	}

	@Override
	public boolean canFish(FluidState state, FishingHook affected) {
		return false;
	}

	@Override
	public ResourceLocation getFishingLootTable() {
		return BuiltInLootTables.EMPTY;
	}
}
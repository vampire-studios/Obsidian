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

import net.minecraft.core.registries.Registries;
import net.minecraft.entity.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.quiltmc.qsl.fluid.api.FluidEnchantmentHelper;
import org.quiltmc.qsl.fluid.api.QuiltFlowableFluidExtensions;
import org.quiltmc.qsl.fluid.impl.CustomFluidInteracting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements CustomFluidInteracting {

	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Shadow
	public abstract boolean hasStatusEffect(MobEffect effect);

	@Shadow
	protected abstract boolean shouldSwimInFluids();

	@Shadow
	public abstract boolean isClimbing();

	@Shadow
	public abstract Vec3 applyFluidMovingSpeed(double d, boolean bl, Vec3 vec3d);

	@Shadow
	public abstract void updateLimbs(boolean flutter);

	@Shadow
	public abstract boolean canWalkOnFluid(FluidState fluidState);

	@Shadow
	protected abstract void defineSynchedData();

	@Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isFallFlying()Z"))
	private boolean redirectFallFlyingToAddCase(LivingEntity instance, Vec3 movementInput) {
		FluidState fluidState = level.getFluidState(blockPosition());
		if (this.quilt$isInCustomFluid() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState)) {
			double fallSpeed = 0.08;
			boolean falling = this.getDeltaMovement().y <= 0.0;
			if (falling && this.hasStatusEffect(MobEffects.SLOW_FALLING)) {
				fallSpeed = 0.01;
				this.resetFallDistance();
			}

			double y = this.getY();
			float horizVisc = 0.8f;
			float vertVisc = 0.8f;
			float speed = 0.02F;

			if ((fluidState.getType() instanceof QuiltFlowableFluidExtensions fluid)) {
				horizVisc = this.isSprinting() ? 0.9f : fluid.getHorizontalViscosity(fluidState, this);
				vertVisc = fluid.getVerticalViscosity(fluidState, this);

				FluidEnchantmentHelper helper = fluid.customEnchantmentEffects(movementInput, ((LivingEntity) (Object) this), horizVisc, speed);
				horizVisc = helper.getHorizontalViscosity();
				speed = helper.getSpeed();

				horizVisc = fluid.modifyEntityHorizontalViscosity(((LivingEntity) (Object) this), horizVisc);
			}
			//
			this.moveRelative(speed, movementInput);
			this.move(MoverType.SELF, this.getDeltaMovement());
			Vec3 vec3d = this.getDeltaMovement();
			if (this.horizontalCollision && this.isClimbing()) {
				vec3d = new Vec3(vec3d.x, 0.2, vec3d.z);
			}

			this.setDeltaMovement(vec3d.multiply(horizVisc, vertVisc, horizVisc));
			Vec3 vec3d2 = this.applyFluidMovingSpeed(fallSpeed, falling, this.getDeltaMovement());
			this.setDeltaMovement(vec3d2);
			if (this.horizontalCollision && this.isFree(vec3d2.x, vec3d2.y + 0.6 - this.getY() + y, vec3d2.z)) {
				this.setDeltaMovement(vec3d2.x, 0.3, vec3d2.z);
			}
			return false;
		}
		return instance.isFallFlying();
	}

	@Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getVelocityAffectingPos()Lnet/minecraft/util/math/BlockPos;"), cancellable = true)
	private void cancelIfCustomFluid(Vec3 movementInput, CallbackInfo ci) {
		if (this.quilt$isInCustomFluid() && this.shouldSwimInFluids() && !this.canWalkOnFluid(level.getFluidState(blockPosition()))) {
			this.updateLimbs(this instanceof FlyingAnimal);
			ci.cancel();
		}
	}

	@Redirect(method = "tickMovement",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFluidHeight(Lnet/minecraft/registry/tag/TagKey;)D", ordinal = 1))
	private double redirectGetFluidHeight(LivingEntity instance, TagKey<Fluid> tag) {
		if (quilt$isInCustomFluid()) {
			return getFluidHeight(TagKey.create(Registries.FLUID,this.level.getFluidState(instance.blockPosition()).getType().builtInRegistryHolder().key().registry()));
		}
		return getFluidHeight(FluidTags.WATER);
	}

	@Redirect(method = "tickMovement",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isTouchingWater()Z"))
	private boolean redirectTouchingWaterToCheckIfSwim(LivingEntity instance) {
		if (quilt$isInCustomFluid()) {
			FluidState fluidState = this.level.getFluidState(blockPosition());
			if (fluidState.getType() instanceof QuiltFlowableFluidExtensions fluid) {
				return fluid.enableDoubleTapSpacebarSwimming(fluidState, instance);
			}
		}
		return isInWater();
	}

	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;swimUpward(Lnet/minecraft/registry/tag/TagKey;)V"))
	private void redirectSwimUpward(LivingEntity instance, TagKey<Fluid> fluid) {
		//Remove need for TagKey here
		this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.04F, 0.0));
	}

	@Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getAir()I", ordinal = 2))
	private int baseTick(LivingEntity instance) {
		if (quilt$isSubmergedInCustomFluid()) {
			FluidState fluidState = this.level.getFluidState(blockPosition());
			if (fluidState.getType() instanceof QuiltFlowableFluidExtensions fluid) {
				fluid.doDrownEffects(fluidState, instance, random);
				return getMaxAirSupply();
			}
		}

		return getAirSupply();
	}

}
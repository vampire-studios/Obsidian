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
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.quiltmc.qsl.fluid.api.QuiltFlowableFluidExtensions;
import org.quiltmc.qsl.fluid.impl.CustomFluidInteracting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;


@Mixin(Entity.class)
public abstract class EntityMixin implements CustomFluidInteracting {
	@Shadow
	public float fallDistance;
	@Shadow
	private Level level;
	@Shadow
	protected boolean firstTick;
	@Shadow
	@Final
	protected RandomSource random;
	protected boolean quilt$inCustomFluid;
	protected boolean quilt$submergedInCustomFluid;
	protected Fluid quilt$submergedCustomFluid;
	@Shadow
	private BlockPos blockPosition;

	@Shadow
	public abstract boolean equals(Object o);

	@Shadow
	@Nullable
	public abstract Entity getVehicle();

	@Shadow
	public abstract boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tag, double d);

	@Shadow
	public abstract void clearFire();

	@Shadow
	public abstract double getEyeY();

	@Shadow
	public abstract double getX();

	@Shadow
	public abstract double getZ();

	@Shadow
	public abstract BlockPos blockPosition();

	@Shadow
	public abstract boolean isSprinting();

	@Shadow
	public abstract boolean isSwimming();

	@Shadow
	public abstract void setSwimming(boolean swimming);

	@Shadow
	public abstract boolean isPassenger();

	@Shadow
	public abstract double getY();

	@Shadow
	public abstract void lavaHurt();

	@Inject(method = "baseTick", at = @At("TAIL"))
	public void baseTick(CallbackInfo ci) {
		this.checkCustomFluidState();
		this.updateSubmergedInCustomFluidState();
	}

	@Override
	public boolean quilt$isInCustomFluid() {
		return this.quilt$inCustomFluid;
	}

	@Override
	public boolean quilt$isSubmergedInCustomFluid() {
		return this.quilt$submergedInCustomFluid && this.quilt$isInCustomFluid();
	}

	void checkCustomFluidState() {
		FluidState fluidState = this.level.getFluidState(this.blockPosition());
		if (fluidState.getType() instanceof QuiltFlowableFluidExtensions fluid &&
				!(getVehicle() instanceof Boat)) {
			updateFluidHeightAndDoFluidPushing(TagKey.create(Registries.FLUID, fluidState.holder().unwrapKey().get().registry()), fluid.getPushStrength(fluidState, (Entity) (Object) this));
			if (!quilt$inCustomFluid && !firstTick) {
				customSplashEffects();
			}
			fallDistance = fluid.getFallDamageReduction(((Entity) (Object) this));
			quilt$inCustomFluid = true;

			if (fluid.canExtinguish(fluidState, (Entity) (Object) this)) {
				clearFire();
			}
			if (fluid.canIgnite(fluidState, (Entity) (Object) this)) {
				lavaHurt();
			}
			return;
		}
		this.quilt$inCustomFluid = false;
	}

	public boolean quilt$isSubmergedInCustomFluid(Fluid fluid) {
		return this.quilt$submergedCustomFluid == fluid;
	}

	private void updateSubmergedInCustomFluidState() {
		this.quilt$submergedCustomFluid = null;
		double d = this.getEyeY() - 0.1111111119389534D;
		Entity entity = this.getVehicle();
		if (entity instanceof Boat boatEntity) {
			if (!boatEntity.isUnderWater() && boatEntity.getBoundingBox().maxY >= d && boatEntity.getBoundingBox().minY <= d) {
				return;
			}
		}

		BlockPos blockPos = BlockPos.containing(this.getX(), d, this.getZ());
		FluidState fluidState = this.level.getFluidState(blockPos);


		double e = (float) blockPos.getY() + fluidState.getHeight(this.level, blockPos);
		if (e > d) {
			this.quilt$submergedCustomFluid = fluidState.getType();
		}

	}

	@Inject(method = "updateSwimming", at = @At("TAIL"))
	public void updateSwimming(CallbackInfo ci) {
		boolean canSwimIn = false;
		if (this.quilt$isInCustomFluid()) {
			FluidState fluidState = this.level.getFluidState(this.blockPosition());
			if (fluidState.getType() instanceof QuiltFlowableFluidExtensions fluid) {
				canSwimIn = fluid.allowSprintSwimming(fluidState, (Entity) (Object) this);
			}
			if (this.isSwimming()) {
				this.setSwimming(this.isSprinting() && canSwimIn && this.quilt$isInCustomFluid() && !this.isPassenger());
			} else {
				this.setSwimming(this.isSprinting() && this.quilt$isSubmergedInCustomFluid() && canSwimIn && !this.isPassenger() &&
						this.level.getFluidState(this.blockPosition).getType() instanceof QuiltFlowableFluidExtensions);
			}

		}
	}

	private void customSplashEffects() {
		FluidState fluidState = this.level.getFluidState(this.blockPosition);
		if (fluidState.getType() instanceof QuiltFlowableFluidExtensions fluid) {
			//Execute the onSplash event
			fluid.onSplash(this.level, new Vec3(this.getX(), this.getY(), this.getZ()), (Entity) (Object) this, this.random);
		}
	}
}
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
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import org.quiltmc.qsl.fluid.api.QuiltFlowableFluidExtensions;
import org.quiltmc.qsl.fluid.impl.CustomFluidInteracting;
import org.quiltmc.qsl.fluid.impl.QuiltFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Boat.class)
public abstract class BoatEntityMixin extends Entity implements CustomFluidInteracting {

	protected BoatEntityMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}

	@Redirect(method = "getWaterLevelAbove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
	public boolean getFluidLevelAbove(FluidState instance, TagKey<Fluid> tag) {
		if(!(instance.getType() instanceof QuiltFlowableFluidExtensions)) return true;
		QuiltFluid fluid = (QuiltFluid) instance.getType();
		return fluid.canBoatSwimOn();
	}

	@Redirect(method = "checkInWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
	public boolean checkBoatInFluid(FluidState instance, TagKey<Fluid> tag) {
		return instance.getType() instanceof QuiltFlowableFluidExtensions;
	}

	@Inject(method = "isUnderwater", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void getUnderwaterLocation(CallbackInfoReturnable<Boat.Status> cir, AABB box, double d, int i, int j, int k, int l, int m, int n, boolean bl, BlockPos.MutableBlockPos mutable, int o, int p, int q, FluidState fluidState) {
		if (fluidState.getType() instanceof QuiltFlowableFluidExtensions && d < (mutable.getY() + fluidState.getHeight(this.level, mutable))) {
			if (!fluidState.isSource()) {
				cir.setReturnValue(Boat.Status.UNDER_FLOWING_WATER);
				cir.cancel();
			}
		}
	}

	@Inject(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z"))
	public void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo ci) {
		if (!(this.level.getFluidState(this.blockPosition().below()).getType() instanceof QuiltFlowableFluidExtensions) && heightDifference < 0.0) {
			this.fallDistance -= (float) heightDifference;
		}
	}
}
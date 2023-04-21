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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootTable;
import org.quiltmc.qsl.fluid.impl.CustomFluidInteracting;
import org.quiltmc.qsl.fluid.impl.FishingBobberEntityExtensions;
import org.quiltmc.qsl.fluid.impl.QuiltFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FishingHook.class)
public abstract class FishingBobberEntityMixin implements CustomFluidInteracting, FishingBobberEntityExtensions {

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 0))
	public boolean tick(FluidState instance, TagKey<Fluid> tag) {
		QuiltFluid fluid = (QuiltFluid) instance.getType();
		return instance.is(this.quilt$canFishingBobberSwimOn()) && fluid.bobberFloats(instance,(FishingHook) (Object)this);
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 1))
	public boolean tick2(FluidState instance, TagKey<Fluid> tag) {
		return instance.is(this.quilt$canFishingBobberSwimOn());
	}

	@Inject(method = "tickFishingLogic", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;up()Lnet/minecraft/util/math/BlockPos;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void canFish(BlockPos pos, CallbackInfo ci, ServerLevel serverWorld, int i) {
		FluidState state = serverWorld.getBlockState(pos).getFluidState();
		QuiltFluid fluid = (QuiltFluid) state.getType();
		if (!state.is(this.quilt$canFishingBobberCatchIn()) || !fluid.canFish(state, (FishingHook) (Object)this)) {
			ci.cancel();
		}
	}

	@Redirect(method = "tickFishingLogic", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
	public boolean spawnParticles(BlockState instance, Block block) {
		return instance.getFluidState().is(quilt$canFishingBobberCatchIn());
	}

	@Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootManager;getLootTable(Lnet/minecraft/util/Identifier;)Lnet/minecraft/loot/LootTable;"))
	public LootTable changeLootTable(LootDataManager instance, ResourceLocation id) {
		return ((FishingHook) (Object) this).level.getServer().getLootData().getLootTable(this.quilt$getFishingLootTable());
	}
}
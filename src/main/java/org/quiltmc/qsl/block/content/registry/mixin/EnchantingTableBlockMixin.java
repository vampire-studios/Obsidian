/*
 * Copyright 2022 The Quilt Project
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

package org.quiltmc.qsl.block.content.registry.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentTableBlock.class)
public class EnchantingTableBlockMixin {
	@Inject(method = "isValidBookShelf", at = @At("HEAD"), cancellable = true)
	private static void quilt$hasEnchantmentPower(Level level, BlockPos pos, BlockPos offset, CallbackInfoReturnable<Boolean> cir) {
		var blockPos = pos.offset(offset);
		var state = level.getBlockState(blockPos);
		var power = BlockContentRegistries.ENCHANTING_BOOSTERS.get(state.getBlock())
				.map(booster -> booster.getEnchantingBoost(level, state, blockPos)).orElse(0f);
		var hasPower = power >= 0.0f && level.getBlockState(pos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2)).is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);

		if (hasPower) {
			cir.setReturnValue(true);
		}
	}

	@Redirect(method = "animateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
	private int quilt$changeParticleChance(RandomSource instance, int bound) {
		return 0;
	}

	// Make particles spawn rate depend on the block's enchanting booster
	@Redirect(
			method = "animateTick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/EnchantmentTableBlock;isValidBookShelf(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Z"
			)
	)
	private boolean quilt$changeParticleChance(
			Level world, BlockPos pos, BlockPos offset, BlockState ignoredState, Level ignoredWorld, BlockPos ignoredPos, RandomSource random
	) {
		if (!world.getBlockState(pos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2)).is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)) {
			return false;
		}

		var blockPos = pos.offset(offset);
		var blockState = world.getBlockState(blockPos);
		var block = blockState.getBlock();
		var booster = BlockContentRegistries.ENCHANTING_BOOSTERS.getNullable(block);

		if (booster != null) {
			var power = booster.getEnchantingBoost(world, blockState, blockPos);
			return random.nextFloat() * 16f <= power;
		}

		return false;
	}
}
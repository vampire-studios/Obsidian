/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.fabric.mixin.entity.event;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin extends LivingEntityMixin {

	@Redirect(method = "trySleep", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"))
	private Comparable<?> redirectSleepDirection(BlockState state, Property<?> property, BlockPos pos) {
		Direction initial = state.getBlock() instanceof BedBlock ? (Direction) state.get(property) : null;
		return EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.invoker().modifySleepDirection((LivingEntity) (Object) this, pos, initial);
	}

	@Inject(method = "trySleep", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;", shift = At.Shift.BY, by = 3), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void onTrySleepDirectionCheck(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> info, @Nullable Direction sleepingDirection) {
		// This checks the result from the event call above.
		if (sleepingDirection == null) {
			info.setReturnValue(Either.left(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_HERE));
		}
	}

	@Redirect(method = "trySleep", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSpawnPoint(Lnet/minecraft/util/registry/RegistryKey;Lnet/minecraft/util/math/BlockPos;FZZ)V"))
	private void onSetSpawnPoint(ServerPlayerEntity player, RegistryKey<World> dimension, BlockPos pos, float angle, boolean spawnPointSet, boolean sendMessage) {
		if (EntitySleepEvents.ALLOW_SETTING_SPAWN.invoker().allowSettingSpawn(player, pos)) {
			player.setSpawnPoint(dimension, pos, angle, spawnPointSet, sendMessage);
		}
	}

	@Redirect(method = "trySleep", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", remap = false))
	private boolean hasNoMonstersNearby(List<HostileEntity> monsters, BlockPos pos) {
		boolean vanillaResult = monsters.isEmpty();
		ActionResult result = EntitySleepEvents.ALLOW_NEARBY_MONSTERS.invoker().allowNearbyMonsters((PlayerEntity) (Object) this, pos, vanillaResult);
		return result != ActionResult.PASS ? result.isAccepted() : vanillaResult;
	}

	@Redirect(method = "trySleep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isDay()Z"))
	private boolean redirectDaySleepCheck(World world, BlockPos pos) {
		boolean day = world.isDay();
		ActionResult result = EntitySleepEvents.ALLOW_SLEEP_TIME.invoker().allowSleepTime((PlayerEntity) (Object) this, pos, !day);

		if (result != ActionResult.PASS) {
			return !result.isAccepted(); // true from the event = night-like conditions, so we have to invert
		}

		return day;
	}
}
package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.events.PlayerTickCallback;
import io.github.vampirestudios.obsidian.minecraft.obsidian.SeatEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerEntityMixin {
	@Inject(method = "tick", at = @At("HEAD"))
	private void hookPlayerTick(CallbackInfo ci) {
		PlayerTickCallback.EVENT.invoker().onPlayerTick((Player) (Object) this);
	}

	/** A lying seat borrows vanilla's sleeping model; the seat decides separately whether it counts as rest. */
	@Inject(method = "getDesiredPose", at = @At("HEAD"), cancellable = true)
	private void obsidian$seatPose(CallbackInfoReturnable<Pose> cir) {
		Player player = (Player) (Object) this;
		if (player.getVehicle() instanceof SeatEntity seat && seat.isLying()) {
			cir.setReturnValue(Pose.SLEEPING);
		}
	}
}

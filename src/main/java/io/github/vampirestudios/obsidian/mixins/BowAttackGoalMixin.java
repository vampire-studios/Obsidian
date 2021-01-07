package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.BowInterface;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.BowItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BowAttackGoal.class)
public class BowAttackGoalMixin {
	@Shadow
	@Final
	private HostileEntity actor;

	@Inject(method = "isHoldingBow()Z", at = @At("HEAD"), cancellable = true)
	private void ob_isHoldingCustomBow(CallbackInfoReturnable<Boolean> callbackInfo) {
		boolean holdingBow = actor.isHolding((item) -> item instanceof BowItem);
		if (holdingBow) {
			callbackInfo.setReturnValue(true);
		}
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
	private float ob_redirectPullProgress(int useTicks) {
		if (actor.getActiveItem().getItem() instanceof BowItem) {
			return ((BowInterface) actor.getActiveItem().getItem()).getCustomPullProgress(useTicks);
		}

		return BowItem.getPullProgress(useTicks);
	}
}
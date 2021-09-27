package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.BowInterface;
import io.github.vampirestudios.obsidian.api.CrossbowInterface;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {
	// Because the uses of this method are hardcoded, checking each hand for the Fabric interfaces of the items is needed.
	// Note: this does not cancel for the vanilla items unless they are holding a custom implementation of the items
	@Inject(method = "getHandPossiblyHolding", at = @At(value = "HEAD"), cancellable = true)
	private static void ob_getHandPossiblyHolding(LivingEntity entity, Item item, CallbackInfoReturnable<Hand> cir) {
		if (item == Items.CROSSBOW) {
			boolean inMainHand = entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof CrossbowInterface;
			boolean inOffHand = entity.getStackInHand(Hand.OFF_HAND).getItem() instanceof CrossbowInterface;

			if (inMainHand || inOffHand) {
				cir.setReturnValue(inMainHand ? Hand.MAIN_HAND : Hand.OFF_HAND);
			}
		} else if (item == Items.BOW) {
			boolean inMainHand = entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof BowInterface;
			boolean inOffHand = entity.getStackInHand(Hand.OFF_HAND).getItem() instanceof BowInterface;

			if (inMainHand || inOffHand) {
				cir.setReturnValue(inMainHand ? Hand.MAIN_HAND : Hand.OFF_HAND);
			}
		}
	}
}
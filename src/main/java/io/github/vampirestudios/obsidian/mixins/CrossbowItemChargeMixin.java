package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.minecraft.obsidian.CrossbowItemImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets an addon crossbow set its own charge time.
 *
 * <p>Vanilla hardcodes 1.25 seconds here, and this one static method feeds the charging sounds, the
 * model's {@code crossbow/pull} property and the shot — so this is the single place that can change
 * a crossbow's wind-up without the three drifting apart. Quick Charge is still applied on top, the
 * same way vanilla does it.</p>
 */
@Mixin(CrossbowItem.class)
public class CrossbowItemChargeMixin {

	@Inject(method = "getChargeDuration", at = @At("HEAD"), cancellable = true)
	private static void obsidian$chargeDuration(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
		if (!(stack.getItem() instanceof CrossbowItemImpl crossbow)) return;

		float seconds = crossbow.obsidian$chargeTicks() / 20.0F;
		cir.setReturnValue(Mth.floor(EnchantmentHelper.modifyCrossbowChargingTime(stack, entity, seconds) * 20.0F));
	}
}

package net.fabricmc.fabric.api.item.v1.elytra;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

/**
 * An interface to implement on all custom elytras.
 */
public interface FabricElytraExtensions {
	/**
	 * Returns if the elytra is usable based on its stack and user.
	 *
	 * @param stack the stack for the elytra item
	 * @param user  the user of the elytra
	 * @return {@code true} if the elytra is usable
	 */
	default boolean isUsable(ItemStack stack, LivingEntity user) {
		return ElytraItem.isFlyEnabled(stack);
	}
}
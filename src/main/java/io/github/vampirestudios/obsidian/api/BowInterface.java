package io.github.vampirestudios.obsidian.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;

/**
 * An interface to implement for all custom bows in fabric. <br>
 * Note: This is meant to be used on a BowItem class, the functionality will not work otherwise.
 *
 * @see SimpleBowItem
 */
public interface BowInterface {
	/**
	 * In this method you can modify the behavior of arrows shot from your custom bow. Applies all of the vanilla arrow modifiers first.
	 *
	 * @param bowStack                   The ItemStack for the Bow Item
	 * @param arrowStack                 The ItemStack for the arrows
	 * @param user                       The user of the bow
	 * @param remainingUseTicks          The ticks remaining on the bow usage
	 * @param persistentProjectileEntity The arrow entity to be spawned
	 */
	void modifyShotProjectile(ItemStack bowStack, ItemStack arrowStack, LivingEntity user, int remainingUseTicks, PersistentProjectileEntity persistentProjectileEntity);

	/**
	 * Gets the pull progress of the bow between 0 and 1.
	 *
	 * @param useTicks The number of ticks the bow has been pulled.
	 * @return The progress of the pull from 0.0f to 1.0f.
	 */
	default float getCustomPullProgress(int useTicks) {
		return BowItem.getPullProgress(useTicks);
	}
}
package io.github.vampirestudios.obsidian.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;

/**
 * This is the default implementation for {@link BowInterface}, allowing for the easy creation of new bows with no new modded functionality.
 */
public class SimpleBowItem extends BowItem implements BowInterface {
	public SimpleBowItem(Settings settings) {
		super(settings);
	}

	@Override
	public void modifyShotProjectile(ItemStack bowStack, ItemStack arrowStack, LivingEntity user, int remainingUseTicks, PersistentProjectileEntity persistentProjectileEntity) {
	}
}
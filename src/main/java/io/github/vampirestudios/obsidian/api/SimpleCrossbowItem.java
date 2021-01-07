package io.github.vampirestudios.obsidian.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;

/**
 * This is the default implementation for FabricCrossbow, allowing for the easy creation of new bows with no new modded functionality.
 */
public class SimpleCrossbowItem extends CrossbowItem implements CrossbowInterface {
	public SimpleCrossbowItem(Settings settings) {
		super(settings);
	}

	@Override
	public void modifyShotProjectile(ItemStack crossbowStack, LivingEntity entity, ItemStack projectileStack, PersistentProjectileEntity persistentProjectileEntity) {
	}

	@Override
	public float getProjectileSpeed(ItemStack stack, LivingEntity entity) {
		return getSpeed(stack);
	}
}
package io.github.vampirestudios.obsidian;

import net.minecraft.world.item.ItemStack;

public interface CosmeticSlotExt {
	ItemStack getHeadCosmetics();

	void setHeadCosmetics(ItemStack itemStack);

	ItemStack getChestCosmetics();

	void setChestCosmetics(ItemStack itemStack);

	ItemStack getLeggingsCosmetics();

	void setLeggingsCosmetics(ItemStack itemStack);

	ItemStack getBootsCosmetics();

	void setBootsCosmetics(ItemStack itemStack);
}
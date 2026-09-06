package io.github.vampirestudios.obsidian;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface CosmeticInterface {
	void obsidian$addHolder(LivingEntity livingEntity, Item item, ItemStack itemStack, String slot);

	void obsidian$destroyHolder(String slot);

	float obsidian$bodyYaw();
}
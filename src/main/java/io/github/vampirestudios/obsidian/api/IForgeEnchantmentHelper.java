package io.github.vampirestudios.obsidian.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public interface IForgeEnchantmentHelper {
	// Helpers for accessing Item data
	private EnchantmentHelper self() {
		return (EnchantmentHelper) this;
	}

	static int getTagEnchantmentLevel(Enchantment enchantment, ItemStack stack) {
		if (!stack.isEmpty()) {
			ResourceLocation identifier = EnchantmentHelper.getEnchantmentId(enchantment);
			ListTag nbtList = stack.getEnchantmentTags();

			for (int i = 0; i < nbtList.size(); ++i) {
				CompoundTag nbtCompound = nbtList.getCompound(i);
				ResourceLocation identifier2 = EnchantmentHelper.getEnchantmentId(nbtCompound);
				if (identifier2 != null && identifier2.equals(identifier)) {
					return EnchantmentHelper.getEnchantmentLevel(nbtCompound);
				}
			}

		}
		return 0;
	}
}

package io.github.vampirestudios.obsidian.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

public interface IForgeEnchantmentHelper {
	// Helpers for accessing Item data
	private EnchantmentHelper self() {
		return (EnchantmentHelper) this;
	}

	static int getTagEnchantmentLevel(Enchantment enchantment, ItemStack stack) {
		if (!stack.isEmpty()) {
			Identifier identifier = EnchantmentHelper.getEnchantmentId(enchantment);
			NbtList nbtList = stack.getEnchantments();

			for (int i = 0; i < nbtList.size(); ++i) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				Identifier identifier2 = EnchantmentHelper.getIdFromNbt(nbtCompound);
				if (identifier2 != null && identifier2.equals(identifier)) {
					return EnchantmentHelper.getLevelFromNbt(nbtCompound);
				}
			}

		}
		return 0;
	}
}

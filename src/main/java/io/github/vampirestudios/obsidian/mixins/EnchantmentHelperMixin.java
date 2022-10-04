package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.IForgeEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin implements IForgeEnchantmentHelper {

	/**
	 * @author Olivia
	 * @reason idk
	 */
	@Overwrite
	public static int getLevel(Enchantment enchantment, ItemStack stack) {
		return stack.getEnchantmentLevel(enchantment);
	}

}

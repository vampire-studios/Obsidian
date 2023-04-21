package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.IForgeEnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin implements IForgeEnchantmentHelper {

	/**
	 * @author Olivia
	 */
	@Overwrite
	public static int getLevel(Enchantment enchantment, ItemStack stack) {
		return stack.getEnchantmentLevel(enchantment);
	}

}

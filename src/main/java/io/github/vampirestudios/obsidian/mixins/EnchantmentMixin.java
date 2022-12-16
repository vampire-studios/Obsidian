package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.IForgeEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Enchantment.class)
public class EnchantmentMixin implements IForgeEnchantment {

	@Shadow @Final public EnchantmentTarget type;

	/**
	 * @author Olivia
	 */
	@Overwrite
	public boolean isAcceptableItem(ItemStack stack) {
		return this.canApplyAtEnchantingTable(stack);
	}

}

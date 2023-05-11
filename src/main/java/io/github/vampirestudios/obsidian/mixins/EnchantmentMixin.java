package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.IForgeEnchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Enchantment.class)
public class EnchantmentMixin implements IForgeEnchantment {

	@Shadow @Final public EnchantmentCategory category;

	/**
	 * @author Olivia
	 * @reason idk
	 */
	@Overwrite
	public boolean canEnchant(ItemStack stack) {
		return this.canApplyAtEnchantingTable(stack);
	}

}

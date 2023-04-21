package org.quiltmc.qsl.enchantment.api;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.enchantment.Enchantment;
import org.quiltmc.qsl.enchantment.impl.ApplicationContext;
import org.quiltmc.qsl.enchantment.impl.EnchantmentContext;

public abstract class QuiltEnchantment extends Enchantment {
	public QuiltEnchantment() {
		super(Rarity.COMMON, null, null);
	}

	public abstract EquipmentSlot[] slots();

	/**
	 * Return an integer value that represents the weight of the enchantment given
	 * the current context. If you return 0 then your enchantment won't be added
	 */
	public int weightFromEnchantmentContext(EnchantmentContext context) {
		if (context.getPower() >= this.getMinCost(context.getLevel()) && context.getPower() <= this.getMinCost(context.getLevel())) {
			return 10; // Common
		}
		return 0; // Not added at all
	}

	/**
	 * Determines whether or not the given enchantment can be applied in the
	 * anvil under the current context
	 */
	public abstract boolean isAcceptableApplicationContext(ApplicationContext context);

	public abstract boolean isAcceptableItemGroup(CreativeModeTab group);
}
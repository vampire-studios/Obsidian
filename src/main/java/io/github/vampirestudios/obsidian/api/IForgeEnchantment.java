package io.github.vampirestudios.obsidian.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.item.ItemStack;

public interface IForgeEnchantment {
    private Enchantment self()
    {
        return (Enchantment) this;
    }

    /**
    * Is this enchantment allowed to be enchanted on books via Enchantment Table
    * @return false to disable the vanilla feature
    */
    default boolean isAllowedOnBooks() {
        return true;
    }

    /**
     * ItemStack aware version of {@link Enchantment#getAttackDamage(int, EntityGroup)}
     * @param level the level of the enchantment
     * @param mobType the mob type being attacked
     * @param enchantedItem the item used for the attack
     * @return the damage bonus
     */
    default float getDamageBonus(int level, EntityGroup mobType, ItemStack enchantedItem) {
        return self().getAttackDamage(level, mobType);
    }

    /**
     * This applies specifically to applying at the enchanting table. The other method {@link Enchantment#isAcceptableItem(ItemStack)}
     * applies for <i>all possible</i> enchantments.
     */
    default boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.canApplyAtEnchantingTable(self());
    }

}
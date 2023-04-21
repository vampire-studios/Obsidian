package io.github.vampirestudios.obsidian.api;

import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

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
     * ItemStack aware version of {@link Enchantment#getDamageBonus(int, MobType)}
     * @param level the level of the enchantment
     * @param mobType the mob type being attacked
     * @param enchantedItem the item used for the attack
     * @return the damage bonus
     */
    default float getDamageBonus(int level, MobType mobType, ItemStack enchantedItem) {
        return self().getDamageBonus(level, mobType);
    }

    /**
     * This applies specifically to applying at the enchanting table. The other method {@link Enchantment#canEnchant(ItemStack)}
     * applies for <i>all possible</i> enchantments.
     */
    default boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.canApplyAtEnchantingTable(self());
    }

}
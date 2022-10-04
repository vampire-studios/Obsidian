package io.github.vampirestudios.obsidian.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.quiltmc.qsl.base.api.util.InjectedInterface;

@InjectedInterface(Enchantment.class)
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

    /**
     * Determines what creative tabs this enchantment's variant of an enchanted book or similar item should appear in.
     * @param book The item being added to the creative tab
     * @param tab The creative tab that items are being added to
     * @return whether the given Item's variant for this enchantment should appear in the respective creative tab
     */
    default boolean allowedInCreativeTab(Item book, ItemGroup tab) {
        if (!self().isAllowedOnBooks()) {
            return false;
        } else if (tab == ItemGroup.SEARCH) {
            return self().type != null;
        } else {
            return tab.containsEnchantments(self().type);
        }
    }
}
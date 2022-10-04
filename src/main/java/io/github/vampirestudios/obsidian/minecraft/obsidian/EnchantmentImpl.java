package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.enchantments.AttackDamage;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.ProtectionAmount;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EnchantmentImpl extends Enchantment {
    private final io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment enchantment;

    public EnchantmentImpl(io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment enchantment) {
        super(enchantment.getRarity(), enchantment.getEnchantmentTarget(), enchantment.getEquipmentSlots());
        this.enchantment = enchantment;
    }

    @Override
    public int getMinLevel() {
        return enchantment.minimumLevel;
    }

    @Override
    public int getMinPower(int level) {
        return enchantment.getMinimumPower(level);
    }

    @Override
    public int getMaxLevel() {
        return enchantment.maximumLevel;
    }

    @Override
    public int getMaxPower(int level) {
        return enchantment.getMaximumPower(level);
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        if (enchantment.protectionAmounts != null) {
            for (ProtectionAmount protectionAmount : enchantment.protectionAmounts) {
                if (protectionAmount.level == level && protectionAmount.getDamageSource() == source) {
                    return protectionAmount.protection_amount;
                }
            }
        }
        return 0;
    }

    @Override
    public float getAttackDamage(int level, EntityGroup group) {
        if (enchantment.attackDamages != null) {
            for (AttackDamage attackDamage : enchantment.attackDamages) {
                if (attackDamage.level == level && attackDamage.getEntityGroup() == group) {
                    return attackDamage.attack_damage;
                }
            }
        }
        return 0.0F;
    }

    @Override
    public boolean isTreasure() {
        return enchantment.treasure;
    }

    @Override
    public boolean isCursed() {
        return enchantment.curse;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return enchantment.allowOnBooks;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return enchantment.tradeable;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return enchantment.discoverable;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack);
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        if (enchantment.blacklistedEnchantments != null) {
            Identifier enchantmentId = Registry.ENCHANTMENT.getId(other);
            for (Identifier identifier : enchantment.blacklistedEnchantments) {
                if(identifier.equals(enchantmentId)) return false;
            }
        }
        return true;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        if (enchantment.acceptedItems != null) {
            Identifier itemId = Registry.ITEM.getId(stack.getItem());
            for (Identifier identifier : enchantment.acceptedItems) {
                if(identifier.equals(itemId)) return true;
            }
            return false;
        }
        return true;
    }
}

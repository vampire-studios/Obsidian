package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.enchantments.AttackDamage;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.ProtectionAmount;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

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
    public int getMinCost(int level) {
        return enchantment.getMinimumPower(level);
    }

    @Override
    public int getMaxLevel() {
        return enchantment.maximumLevel;
    }

    @Override
    public int getMaxCost(int level) {
        return enchantment.getMaximumPower(level);
    }

    @Override
    public int getDamageProtection(int level, DamageSource source) {
        if (enchantment.protectionAmounts != null) {
            for (ProtectionAmount protectionAmount : enchantment.protectionAmounts) {
                if (protectionAmount.level == level && protectionAmount.getDamageSource(source.getDirectEntity()) == source) {
                    return protectionAmount.protection_amount;
                }
            }
        }
        return 0;
    }

    @Override
    public float getDamageBonus(int level, MobType group) {
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
    public boolean isTreasureOnly() {
        return enchantment.treasure;
    }

    @Override
    public boolean isCurse() {
        return enchantment.curse;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return enchantment.allowOnBooks;
    }

    @Override
    public boolean isTradeable() {
        return enchantment.tradeable;
    }

    @Override
    public boolean isDiscoverable() {
        return enchantment.discoverable;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack);
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        if (enchantment.blacklistedEnchantments != null) {
            ResourceLocation enchantmentId = BuiltInRegistries.ENCHANTMENT.getKey(other);
            for (ResourceLocation identifier : enchantment.blacklistedEnchantments) {
                if(identifier.equals(enchantmentId)) return false;
            }
        }
        return true;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        if (enchantment.acceptedItems != null) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            for (ResourceLocation identifier : enchantment.acceptedItems) {
                if(identifier.equals(itemId)) return true;
            }
            return false;
        }
        return true;
    }
}

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.enchantments.AttackDamage;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.ProtectionAmount;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.damage.DamageSource;

public class EnchantmentImpl extends Enchantment {
    private final io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment enchantment;

    public EnchantmentImpl(io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment enchantment) {
        super(enchantment.getRarity(), enchantment.getEnchantmentTarget().get().getVanilla(), enchantment.getEquipmentSlots());
        this.enchantment = enchantment;
    }

    @Override
    public int getMinLevel() {
        return enchantment.minimum_level;
    }

    @Override
    public int getMinPower(int level) {
        return enchantment.minimum_power;
    }

    @Override
    public int getMaxLevel() {
        return enchantment.maximum_level;
    }

    @Override
    public int getMaxPower(int level) {
        return enchantment.maximum_power;
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        for (ProtectionAmount protectionAmount : enchantment.protection_amounts) {
            if (protectionAmount.level == level && protectionAmount.getDamageSource() == source) {
                return protectionAmount.protection_amount;
            }
        }
        return 1;
    }

    @Override
    public float getAttackDamage(int level, EntityGroup group) {
        for (AttackDamage attackDamage : enchantment.attack_damages) {
            if (attackDamage.level == level && attackDamage.getEntityGroup() == group) {
                return attackDamage.attack_damage;
            }
        }
        return 1.0F;
    }

    @Override
    public boolean isTreasure() {
        return enchantment.treasure;
    }

    @Override
    public boolean isCursed() {
        return enchantment.cursed;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other);
    }

    @Override
    public String getTranslationKey() {
        return super.getTranslationKey();
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return enchantment.available_for_enchanted_book_offer;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return enchantment.available_for_random_selection;
    }
}

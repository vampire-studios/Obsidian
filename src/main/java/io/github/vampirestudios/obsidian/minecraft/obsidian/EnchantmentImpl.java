package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.enchantments.AttackDamage;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.ProtectionAmount;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
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
        return enchantment.minimum_level;
    }

    @Override
    public int getMinPower(int level) {
        return enchantment.getMinimumPower(level);
    }

    @Override
    public int getMaxLevel() {
        return enchantment.maximum_level;
    }

    @Override
    public int getMaxPower(int level) {
        return enchantment.getMaximumPower(level);
    }

    @Override
    public int getProtectionAmount(int level, DamageSource source) {
        if (enchantment.protection_amounts != null) {
            for (ProtectionAmount protectionAmount : enchantment.protection_amounts) {
                if (protectionAmount.level == level && protectionAmount.getDamageSource() == source) {
                    return protectionAmount.protection_amount;
                }
            }
        }
        return 0;
    }

    @Override
    public float getAttackDamage(int level, EntityGroup group) {
        if (enchantment.attack_damages != null) {
            for (AttackDamage attackDamage : enchantment.attack_damages) {
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
        return enchantment.cursed;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        if (enchantment.accepted_enchantments != null) {
            for (Identifier identifier : enchantment.accepted_enchantments) {
                return identifier.equals(Registry.ENCHANTMENT.getId(other));
            }
        }
        return true;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        if (enchantment.accepted_items != null) {
            for (Identifier identifier : enchantment.accepted_items) {
                return identifier.equals(Registry.ITEM.getId(stack.getItem()));
            }
        }
        return true;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        super.onTargetDamaged(user, target, level);
    }

    @Override
    public void onUserDamaged(LivingEntity user, Entity attacker, int level) {
        super.onUserDamaged(user, attacker, level);
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

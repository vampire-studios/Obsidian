package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;

public class Enchantment {

    public NameInformation name;
    public String[] slots;
    public Identifier[] accepted_items;
    public Identifier[] accepted_enchantments;
    public String target;
    public String rarity;
    public int minimum_level = 1;
    public int maximum_level = 2;
    public int minimum_power;
    public int maximum_power;
    public ProtectionAmount[] protection_amounts;
    public AttackDamage[] attack_damages;
    public boolean treasure = false;
    public boolean cursed = false;
    public boolean available_for_enchanted_book_offer = true;
    public boolean available_for_random_selection = true;

    public net.minecraft.enchantment.Enchantment.Rarity getRarity() {
        return net.minecraft.enchantment.Enchantment.Rarity.valueOf(rarity);
    }

    public int getMinimumPower(int level) {
        if (minimum_power != 0) {
            return minimum_power;
        } else {
            return 1 + level * 10;
        }
    }

    public int getMaximumPower(int level) {
        if (maximum_power != 0) {
            return maximum_power;
        } else {
            return this.getMinimumPower(level) + 5;
        }
    }

    public EnchantmentTarget getEnchantmentTarget() {
        return EnchantmentTarget.valueOf(target);
    }

    public EquipmentSlot[] getEquipmentSlots() {
        EquipmentSlot[] slots = new EquipmentSlot[10];
        for (int i = 0; i < this.slots.length; i++) {
            slots[i] = EquipmentSlot.byName(this.slots[i]);
        }
        return slots;
    }

}

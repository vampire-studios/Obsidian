package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;

public class Enchantment {

    public String name;
    public Identifier id;
    public String[] slots;
    public String target;
    public String rarity;
    public int minimum_level = 1;
    public int maximum_level = 2;
    public int minimum_power = 1;
    public int maximum_power = 2;
    public ProtectionAmount[] protection_amounts = new ProtectionAmount[0];
    public AttackDamage[] attack_damages = new AttackDamage[0];
    public boolean treasure = false;
    public boolean cursed = false;
    public boolean available_for_enchanted_book_offer;
    public boolean available_for_random_selection;

    public net.minecraft.enchantment.Enchantment.Rarity getRarity() {
        return net.minecraft.enchantment.Enchantment.Rarity.valueOf(rarity);
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

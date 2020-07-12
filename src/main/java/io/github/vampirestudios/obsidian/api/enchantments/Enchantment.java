package io.github.vampirestudios.obsidian.api.enchantments;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;

public class Enchantment {

    public String name;
    public Identifier id;
    public String[] slots;
    public String target;
    public String rarity;

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

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public record CustomArmorMaterial(io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial material) implements ArmorMaterial {

    @Override
    public int getDurability(EquipmentSlot slot) {
        return this.material.getDurability(slot);
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return this.material.protection_amount;
    }

    @Override
    public int getEnchantability() {
        return this.material.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return Registry.SOUND_EVENT.get(this.material.equipSound);
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Registry.ITEM.get(this.material.repair_item));
    }

    @Override
    public String getName() {
        return this.material.name.getPath();
    }

    @Override
    public float getToughness() {
        return this.material.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.material.knockback_resistance;
    }

}
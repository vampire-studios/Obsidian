package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public record CustomArmorMaterial(io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial material) implements ArmorMaterial {

    @Override
    public int getDurabilityForType(ArmorItem.Type slot) {
        return this.material.getDurability(slot.getSlot());
    }

    @Override
    public int getDefenseForType(ArmorItem.Type slot) {
        return this.material.protection_amount;
    }

    @Override
    public int getEnchantmentValue() {
        return this.material.enchantability;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        return /*BuiltInRegistries.SOUND_EVENT.get(this.material.equipSound)*/SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(BuiltInRegistries.ITEM.get(this.material.repair_item));
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
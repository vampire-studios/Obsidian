package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class CustomArmorMaterial implements ArmorMaterial {

   private final io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial material;

   public CustomArmorMaterial(io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial material) {
      this.material = material;
   }

   @Override
   public int getDurability(EquipmentSlot slot) {
      return this.material.maxDamageFactor;
   }

   @Override
   public int getProtectionAmount(EquipmentSlot slot) {
      return this.material.protection_amount;
   }

   public int getEnchantability() {
      return this.material.enchantability;
   }

   @Override
   public SoundEvent getEquipSound() {
      return Registry.SOUND_EVENT.get(this.material.sound_event);
   }

   @Override
   public Ingredient getRepairIngredient() {
      return Ingredient.ofItems(Registry.ITEM.get(this.material.repair_item));
   }

   public String getName() {
      return this.material.name;
   }

   public float getToughness() {
      return this.material.toughness;
   }

   public float getKnockbackResistance() {
      return this.material.knockback_resistance;
   }

}
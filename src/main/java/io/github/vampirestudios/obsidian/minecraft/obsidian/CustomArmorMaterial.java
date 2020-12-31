package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

public class CustomArmorMaterial implements ArmorMaterial {

   private static final int[] MAX_DAMAGE_ARRAY = new int[] { 13, 15, 16, 11 };
   private final String name;
   private final int maxDamageFactor;
   private final int[] damageReductionAmountArray;
   private final int enchantability;
   private final SoundEvent soundEvent;
   private final float toughness;
   private final float knockbackResistance;
   private final Ingredient repairMaterial;

   public CustomArmorMaterial(String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability,
                              SoundEvent soundEvent, float toughness, float knockbackResistance, Ingredient repairMaterial) {
      this.name = name;
      this.maxDamageFactor = maxDamageFactor;
      this.damageReductionAmountArray = damageReductionAmountArray;
      this.enchantability = enchantability;
      this.soundEvent = soundEvent;
      this.toughness = toughness;
      this.knockbackResistance = knockbackResistance;
      this.repairMaterial = repairMaterial;
   }

   @Override
   public int getDurability(EquipmentSlot slot) {
      return MAX_DAMAGE_ARRAY[slot.getEntitySlotId()] * this.maxDamageFactor;
   }

   @Override
   public int getProtectionAmount(EquipmentSlot slot) {
      return this.damageReductionAmountArray[slot.getEntitySlotId()];
   }

   public int getEnchantability() {
      return this.enchantability;
   }

   @Override
   public SoundEvent getEquipSound() {
      return this.soundEvent;
   }

   @Override
   public Ingredient getRepairIngredient() {
      return this.repairMaterial;
   }

   public String getName() {
      return this.name;
   }

   public float getToughness() {
      return this.toughness;
   }

   public float getKnockbackResistance() {
      return this.knockbackResistance;
   }

}
package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class CustomToolMaterial implements Tier {

    public io.github.vampirestudios.obsidian.api.obsidian.item.ToolMaterial toolMaterial;

    public CustomToolMaterial(io.github.vampirestudios.obsidian.api.obsidian.item.ToolMaterial toolMaterial) {
        this.toolMaterial = toolMaterial;
    }

    @Override
    public int getUses() {
        return toolMaterial.durability;
    }

    @Override
    public float getSpeed() {
        return toolMaterial.miningSpeed;
    }

    @Override
    public float getAttackDamageBonus() {
        return toolMaterial.attackDamage;
    }

    @Override
    public int getLevel() {
        return toolMaterial.miningLevel;
    }

    @Override
    public int getEnchantmentValue() {
        return toolMaterial.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(BuiltInRegistries.ITEM.get(toolMaterial.repairItem));
    }

}
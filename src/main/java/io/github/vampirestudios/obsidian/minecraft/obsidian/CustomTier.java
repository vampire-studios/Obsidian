package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class CustomTier implements Tier {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Tier tier;

    public CustomTier(io.github.vampirestudios.obsidian.api.obsidian.item.Tier tier) {
        this.tier = tier;
    }

    @Override
    public int getUses() {
        return tier.durability;
    }

    @Override
    public float getSpeed() {
        return tier.miningSpeed;
    }

    @Override
    public float getAttackDamageBonus() {
        return tier.attackDamage;
    }

    @Override
    public int getLevel() {
        return tier.miningLevel;
    }

    @Override
    public int getEnchantmentValue() {
        return tier.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(BuiltInRegistries.ITEM.get(tier.repairItem));
    }

}
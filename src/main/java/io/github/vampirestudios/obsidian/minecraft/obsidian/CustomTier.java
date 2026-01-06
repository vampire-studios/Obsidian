/*
package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class CustomTier implements ToolMaterial {

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
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return TagKey.create(Registries.BLOCK, tier.incorrectBlocksForDrops);
    }

    @Override
    public int getEnchantmentValue() {
        return tier.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        List<ItemStack> ingredients = new ArrayList<>();
        tier.repairItem.forEach(Identifier -> {
            Item item = BuiltInRegistries.ITEM.get(Identifier);
            ingredients.add(new ItemStack(item));
        });
        return Ingredient.of(ingredients.toArray(new ItemStack[0]));
    }

    public Tool createToolProperties(TagKey<Block> block) {
        return new Tool(List.of(Tool.Rule.deniesDrops(this.getIncorrectBlocksForDrops()), Tool.Rule.minesAndDrops(block, this.getSpeed())), 1.0F, 1);
    }

}*/

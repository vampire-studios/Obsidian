package io.github.vampirestudios.obsidian.api.item;

import io.github.vampirestudios.obsidian.api.TextureAndModelInformation;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ItemInformation {

    public String rarity = "common";
    public String itemGroup = "";
    public Integer max_count;
    public Identifier name;
    public String displayName;
    public TextureAndModelInformation texturesAndModels;

    public Rarity getRarity() {
        switch (rarity) {
            case "uncommon":
                return Rarity.UNCOMMON;
            case "rare":
                return Rarity.RARE;
            case "epic":
                return Rarity.EPIC;
            case "common":
            default:
                return Rarity.COMMON;
        }
    }

    public int getMaxCount() {
        return max_count == null ? 64 : max_count;
    }

    public ItemGroup getItemGroup() {
        switch (itemGroup) {
            case "minecraft:building_blocks":
                return ItemGroup.BUILDING_BLOCKS;
            case "minecraft:decorations":
                return ItemGroup.DECORATIONS;
            case "minecraft:redstone":
                return ItemGroup.REDSTONE;
            case "minecraft:transportation":
                return ItemGroup.TRANSPORTATION;
            case "minecraft:misc":
                return ItemGroup.MISC;
            case "minecraft:food":
                return ItemGroup.FOOD;
            case "minecraft:tools":
                return ItemGroup.TOOLS;
            case "minecraft:combat":
                return ItemGroup.COMBAT;
            case "minecraft:brewing":
                return ItemGroup.BREWING;
            case "minecraft:search":
            default:
                return ItemGroup.SEARCH;
        }
    }

}
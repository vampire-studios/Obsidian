package io.github.vampirestudios.obsidian.api_new.item;

import io.github.vampirestudios.obsidian.api.NameInformation;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;

public class ItemInformation {



    public String rarity;
    public String item_group;
    public Integer max_count;
    public NameInformation name;
    public boolean enchanted;
    public int use_duration;

    public ItemInformation(String rarity, String item_group, Integer max_count, NameInformation name, boolean enchanted, int use_duration) {
        this.rarity = rarity;
        this.item_group = item_group;
        this.max_count = max_count;
        this.name = name;
        this.enchanted = enchanted;
        this.use_duration = use_duration;
    }

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

    public ItemGroup getItemGroup() {
        switch (item_group) {
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
package io.github.vampirestudios.obsidian.api.item;

import io.github.vampirestudios.obsidian.api.NameInformation;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;

public class ItemInformation {

    public String rarity = "common";
    public String item_group = "";
    public Integer max_count = 64;
    public NameInformation name;
    public boolean has_glint = false;
    public boolean is_enchantable = false;
    public int enchantability = 5;
    public boolean hand_equipped = false;
    public int use_duration = 5;

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
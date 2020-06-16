package io.github.vampirestudios.obsidian.api.item;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.TextureAndModelInformation;
import io.github.vampirestudios.obsidian.api.TooltipInformation;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ItemInformation {

    public String rarity = "common";
    @SerializedName("item_group")
    public String itemGroup = "";
    @SerializedName("minecraft:max_stack_size")
    public Integer maxStackSize = 64;
    public Identifier name;
    public String displayName;
    public TextureAndModelInformation texturesAndModels;
    @SerializedName("minecraft:foil")
    public boolean enchanted = false;
    public TooltipInformation[] tooltip = new TooltipInformation[0];

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
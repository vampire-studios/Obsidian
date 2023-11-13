package io.github.vampirestudios.obsidian.api.obsidian;

import blue.endless.jankson.annotation.SerializedName;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.List;

public class ItemSettings {
    @SerializedName("base_item_properties")
    @com.google.gson.annotations.SerializedName("base_item_properties")
    public Object baseItemSettings;

    // Basic Info
    @SerializedName("item_group")
    @com.google.gson.annotations.SerializedName("item_group")
    public ResourceLocation creativeTab;

    @SerializedName("max_stack_size")
    @com.google.gson.annotations.SerializedName("max_stack_size")
    public Integer maxStackSize;

    @SerializedName("max_uses")
    @com.google.gson.annotations.SerializedName("max_uses")
    public int durability;

    public String rarity;
    public boolean fireproof;

    // Fuel
    @SerializedName("is_fuel")
    @com.google.gson.annotations.SerializedName("is_fuel")
    public boolean isFuel;

    @SerializedName("fuel_duration")
    @com.google.gson.annotations.SerializedName("fuel_duration")
    public int fuelDuration;

    // Enchanting
    @SerializedName("has_enchantment_glint")
    @com.google.gson.annotations.SerializedName("has_enchantment_glint")
    public boolean hasEnchantmentGlint;

    @SerializedName("is_enchantable")
    @com.google.gson.annotations.SerializedName("is_enchantable")
    public boolean isEnchantable;

    public int enchantability;

    // Block Placing
    @SerializedName("can_place_block") public boolean canPlaceBlock;
    @SerializedName("placable_block") public ResourceLocation placableBlock;

    // Wearable
    public boolean wearable;
    public String wearableSlot;

    // Dyeable
    public boolean dyeable;
    @SerializedName("default_color")
    @com.google.gson.annotations.SerializedName("default_color")
    public int defaultColor;

    // Rendering
    @SerializedName("custom_render_mode")
    @com.google.gson.annotations.SerializedName("custom_render_mode")
    public boolean customRenderMode;

    @SerializedName("render_mode_models")
    @com.google.gson.annotations.SerializedName("render_mode_models")
    public List<RenderModeModel> renderModeModels;

    public Conversion conversion;

    public ItemSettings() {
        this.creativeTab = new ResourceLocation("minecraft:building_blocks");

        this.maxStackSize = 64;

        this.durability = 5;

        this.rarity = "common";
        this.fireproof = false;

        this.isFuel = false;
        this.fuelDuration = 10;

        this.hasEnchantmentGlint = false;
        this.isEnchantable = false;
        this.enchantability = 5;

        this.canPlaceBlock = false;

        this.wearable = false;

        this.dyeable = false;
        this.defaultColor = 16579836;

        this.customRenderMode = false;
    }

    /**
     * Returns the base item settings.
     * @return An Optional<ItemSettings> object.
     */
    public ItemSettings getBaseItemSettings() {
        switch (baseItemSettings) {
            case ResourceLocation resourceLocation -> {
                return ContentRegistries.ITEM_SETTINGS.get(resourceLocation);
            }
            case String resourceLocation -> {
                ResourceLocation location = ResourceLocation.tryParse(resourceLocation);
                return ContentRegistries.ITEM_SETTINGS.get(location);
            }
            case ItemSettings itemSettings1 -> {
                return itemSettings1;
            }
            case null, default -> {
                return null;
            }
        }
    }

    public ResourceKey<CreativeModeTab> getItemGroup() {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, creativeTab);
    }

    /**
     * This class represents the conversion settings for an item.
     */
    public static class Conversion {
        private List<ResourceLocation> from;
        private ResourceLocation to;

        public List<ResourceLocation> getFrom() {
            return from;
        }

        public ResourceLocation getTo() {
            return to;
        }
    }
}
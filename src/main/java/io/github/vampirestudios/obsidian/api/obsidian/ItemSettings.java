package io.github.vampirestudios.obsidian.api.obsidian;

import blue.endless.jankson.annotation.SerializedName;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.components.Conversion;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.List;
import java.util.Map;

public class ItemSettings {
    @SerializedName("parent")
    @com.google.gson.annotations.SerializedName("parent")
    public Object baseItemSettings;

    // Basic Info
    @SerializedName("item_group")
    @com.google.gson.annotations.SerializedName("item_group")
    public ResourceLocation creativeTab;

    @SerializedName("max_stack_size")
    @com.google.gson.annotations.SerializedName("max_stack_size")
    public Integer maxStackSize = 64;

    @SerializedName("max_uses")
    @com.google.gson.annotations.SerializedName("max_uses")
    public int durability = 0;

    public String rarity = "common";
    public boolean fireproof = false;

    public Fuel fuel;

    public static class Fuel {
        public int duration = 10;
        @SerializedName("return_item")
        @com.google.gson.annotations.SerializedName("return_item")
        public ResourceLocation returnItem;
    }

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
    public TriState hasEnchantmentGlint;

    @SerializedName("is_enchantable")
    @com.google.gson.annotations.SerializedName("is_enchantable")
    public boolean isEnchantable;

    public int enchantability;

    // Block Placing
    @SerializedName("can_place_block") public boolean canPlaceBlock;
    @SerializedName("placable_block") public ResourceLocation placableBlock;

    // Wearable
    public boolean wearable;
    @SerializedName("wearable_slot")
    @com.google.gson.annotations.SerializedName("wearable_slot")
    public String wearableSlot;

    // Dyeable
    public boolean dyeable;
    @SerializedName("default_color")
    @com.google.gson.annotations.SerializedName("default_color")
    public int defaultColor;

    public int getDefaultColor() {
        int color;
        if (this.defaultColor != 0)
            color = this.defaultColor;
        else if (this.getParentSettings().defaultColor != 0)
            color = this.getParentSettings().defaultColor;
        else
            color = 10511680;

        return color;
    }

    // Rendering
    @SerializedName("custom_render_mode")
    @com.google.gson.annotations.SerializedName("custom_render_mode")
    public boolean customRenderMode;

    @SerializedName("render_mode_models")
    @com.google.gson.annotations.SerializedName("render_mode_models")
    public List<RenderModeModel> renderModeModels;

    public Conversion conversion;

    public ItemSettings() {
        this.creativeTab = ResourceLocation.withDefaultNamespace("building_blocks");

        this.hasEnchantmentGlint = TriState.DEFAULT;
        this.enchantability = 5;
        this.defaultColor = 16579836;
    }

    // This getter will handle the different possible types of 'itemSettings'
    public ItemSettings getParentSettings() {
		switch (baseItemSettings) {
			case Map<?, ?> propertiesMap -> {
				System.out.println(STR."Map: \{propertiesMap}");
				return constructItemSettingsFromMap(propertiesMap);
			}
			case JsonObject jsonObject -> {
				System.out.println(STR."Json Object: \{jsonObject.getAsString()}");
				return null;
			}
			case String s -> {
				System.out.println(STR."String: \{s}");
				return getItemSettingsFromReference(s);
			}
			case ItemSettings itemSettings1 -> {
				return itemSettings1;
			}
			case null, default -> {
				return handleUnknownItemSettingsType();
			}
		}
    }

    private ItemSettings constructItemSettingsFromMap(Map<?, ?> propertiesMap) {
//		System.out.println("Map: " + propertiesMap);
        ItemSettings settings = new ItemSettings();
        if (propertiesMap.containsKey("parent")) {
            settings.baseItemSettings = ContentRegistries.ITEM_SETTINGS.get(ResourceLocation.tryParse((String) propertiesMap.get("parent")));
        }
        return settings; // Replace with actual construction logic
    }

    private ItemSettings getItemSettingsFromReference(String reference) {
        ResourceLocation location = ResourceLocation.tryParse(reference);
        if (location != null) {
            return ContentRegistries.ITEM_SETTINGS.getValue(location);
        } else {
            System.out.println(STR."Invalid Reference: \{reference}");
            return handleInvalidReference(reference);
        }
    }

    private ItemSettings handleUnknownItemSettingsType() {
//		System.out.println("Unknown Item Settings Type");
        return new ItemSettings(); // Replace with actual error handling logic
    }

    private ItemSettings handleInvalidReference(String reference) {
//		System.out.println("Invalid reference: " + reference);
        return new ItemSettings(); // Replace with actual error handling logic
    }

    public ResourceKey<CreativeModeTab> getItemGroup() {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, creativeTab);
    }
}
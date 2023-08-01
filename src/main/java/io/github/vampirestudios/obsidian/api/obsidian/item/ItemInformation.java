package io.github.vampirestudios.obsidian.api.obsidian.item;

import blue.endless.jankson.annotation.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.List;

public class ItemInformation {
	public NameInformation name;

    // Basic Info
    @SerializedName("item_group")
    @com.google.gson.annotations.SerializedName("item_group")
    public ResourceLocation creativeTab = new ResourceLocation("minecraft:building_blocks");

    @SerializedName("max_stack_size")
    @com.google.gson.annotations.SerializedName("max_stack_size")
    public Integer maxStackSize = 64;

    @SerializedName("max_uses")
    @com.google.gson.annotations.SerializedName("max_uses")
    public int durability = 5;

    public String rarity = "common";

    // Fuel
    @SerializedName("is_fuel")
    @com.google.gson.annotations.SerializedName("is_fuel")
    public boolean isFuel = false;

    @SerializedName("fuel_duration")
    @com.google.gson.annotations.SerializedName("fuel_duration")
    public int fuelDuration = 10;

    // Enchanting
    @SerializedName("has_enchantment_glint")
    @com.google.gson.annotations.SerializedName("has_enchantment_glint")
    public boolean hasEnchantmentGlint = false;

    @SerializedName("is_enchantable")
    @com.google.gson.annotations.SerializedName("is_enchantable")
    public boolean isEnchantable = false;

    public int enchantability = 5;

    // Block Placing
    @SerializedName("can_place_block") public boolean canPlaceBlock = false;
    @SerializedName("placable_block") public ResourceLocation placableBlock;

    // Wearable
    public boolean wearable = false;
    public String wearableSlot;

    // Dyeable
    public boolean dyeable = false;
	@SerializedName("default_color")
    @com.google.gson.annotations.SerializedName("default_color")
    public int defaultColor = 16579836;

    // Rendering
	@SerializedName("custom_render_mode")
    @com.google.gson.annotations.SerializedName("custom_render_mode")
    public boolean customRenderMode = false;

    @SerializedName("render_mode_models")
    @com.google.gson.annotations.SerializedName("render_mode_models")
    public List<RenderModeModel> renderModeModels;

    public Conversion conversion;

    public ResourceKey<CreativeModeTab> getItemGroup() {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, creativeTab);
	}

    public static class Conversion {
        public List<ResourceLocation> from;
        public ResourceLocation to;
    }

    public static class RenderModeModel {
    	public ResourceLocation model;
    	public List<String> modes;
	}

}
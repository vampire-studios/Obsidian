package io.github.vampirestudios.obsidian.api.obsidian.item;

import blue.endless.jankson.annotation.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Rarity;

public class ItemInformation {
	public NameInformation name;

    public Rarity rarity = Rarity.COMMON;
    @SerializedName("creative_tab") public ResourceLocation creativeTab;
    @SerializedName("max_stack_size") public Integer maxStackSize = 64;
    @SerializedName("has_enchantment_glint") public boolean hasEnchantmentGlint = false;
    @SerializedName("is_enchantable") public boolean isEnchantable = false;
    public int enchantability = 5;
    @SerializedName("max_uses") public int useDuration = 5;
    @SerializedName("can_place_block") public boolean canPlaceBlock = false;
    @SerializedName("placable_block") public ResourceLocation placableBlock;
    public boolean wearable = false;
    public boolean dyeable = false;
	@SerializedName("default_color") public int defaultColor = 16579836;
	public String wearableSlot;
	@SerializedName("custom_render_mode") public boolean customRenderMode = false;
    @SerializedName("render_mode_models") public RenderModeModel[] renderModeModels;

	public ResourceKey<CreativeModeTab> getItemGroup() {
        CreativeModeTab creativeModeTab = BuiltInRegistries.CREATIVE_MODE_TAB.get(creativeTab);
		return BuiltInRegistries.CREATIVE_MODE_TAB.createIntrusiveHolder(creativeModeTab).key();
	}

    public static class RenderModeModel {
    	public ModelResourceLocation model;
    	public String[] modes;
	}

}
package io.github.vampirestudios.obsidian.api.obsidian.item;

import blue.endless.jankson.annotation.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.registry.Registries;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ItemInformation {
	public NameInformation name;

    public Rarity rarity = Rarity.COMMON;
    @SerializedName("creative_tab") public Identifier creativeTab;
    @SerializedName("max_stack_size") public Integer maxStackSize = 64;
    @SerializedName("has_enchantment_glint") public boolean hasEnchantmentGlint = false;
    @SerializedName("is_enchantable") public boolean isEnchantable = false;
    public int enchantability = 5;
    @SerializedName("max_uses") public int useDuration = 5;
    @SerializedName("can_place_block") public boolean canPlaceBlock = false;
    @SerializedName("placable_block") public Identifier placableBlock;
    public boolean wearable = false;
    public boolean dyeable = false;
	@SerializedName("default_color") public int defaultColor = 16579836;
	public String wearableSlot;
	@SerializedName("custom_render_mode") public boolean customRenderMode = false;
    @SerializedName("render_mode_models") public RenderModeModel[] renderModeModels;

	public ItemGroup getItemGroup() {
		return Registries.ITEM_GROUP_REGISTRY.get(creativeTab);
	}

    public static class RenderModeModel {
    	public ModelIdentifier model;
    	public String[] modes;
	}

}
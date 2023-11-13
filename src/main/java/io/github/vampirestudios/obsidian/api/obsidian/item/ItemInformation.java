package io.github.vampirestudios.obsidian.api.obsidian.item;

import blue.endless.jankson.annotation.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.ItemSettings;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.ResourceLocation;

public class ItemInformation extends ItemSettings {
	public NameInformation name;

	@SerializedName("item_properties")
	@com.google.gson.annotations.SerializedName("item_properties")
	public Object itemSettings;

	public ItemSettings getItemSettings() {
		switch (itemSettings) {
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
				return this;
			}
		}
	}
}
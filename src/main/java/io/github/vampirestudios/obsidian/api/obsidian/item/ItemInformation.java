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

	@SerializedName("item_type")
	@com.google.gson.annotations.SerializedName("item_type")
	public String itemType;

	public ItemSettings getItemSettings() {
		if (itemSettings instanceof ResourceLocation resourceLocation) {
			return ContentRegistries.ITEM_SETTINGS.get(resourceLocation);
		} else if (itemSettings instanceof  String s) {
			ResourceLocation location = ResourceLocation.tryParse(s);
			return ContentRegistries.ITEM_SETTINGS.get(location);
		} else if (itemSettings instanceof ItemSettings itemSettings1) {
			return itemSettings1;
		} else {
			return this;
		}
	}
}
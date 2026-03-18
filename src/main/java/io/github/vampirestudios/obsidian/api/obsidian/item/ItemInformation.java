package io.github.vampirestudios.obsidian.api.obsidian.item;

import blue.endless.jankson.annotation.SerializedName;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.api.obsidian.ItemSettings;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.components.Conversion;
import net.minecraft.resources.Identifier;

import java.util.Map;

public class ItemInformation {
	public NameInformation name;
	public transient Identifier id;

	@SerializedName("item_properties")
	@com.google.gson.annotations.SerializedName("item_properties")
	public Object itemSettings;

	@SerializedName("item_type")
	@com.google.gson.annotations.SerializedName("item_type")
	public String itemType;

	public Conversion conversion;

	// This getter will handle the different possible types of 'itemSettings'
	public ItemSettings getItemSettings() {
		switch (itemSettings) {
			case Map<?, ?> propertiesMap -> {
				return constructItemSettingsFromMap(propertiesMap);
			}
			case JsonObject jsonObject -> {
				System.out.println(STR."Json Object: \{jsonObject.getAsString()}");
				return null;
			}
			case String s -> {
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
			settings.baseItemSettings = ContentRegistries.ITEM_SETTINGS.get(Identifier.tryParse((String) propertiesMap.get("parent")));
		}
		return settings; // Replace with actual construction logic
	}

	private ItemSettings getItemSettingsFromReference(String reference) {
		Identifier location = Identifier.tryParse(reference);
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
}
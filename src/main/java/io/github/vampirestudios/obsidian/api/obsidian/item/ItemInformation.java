package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.api.obsidian.ItemSettings;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.registry.components.Conversion;
import net.minecraft.resources.Identifier;

public class ItemInformation {
	public NameInformation name;
	public transient Identifier id;

	@com.google.gson.annotations.SerializedName("item_properties")
	public Object itemSettings;

	@com.google.gson.annotations.SerializedName("item_type")
	public String itemType;

	public Conversion conversion;

	/**
	 * Settings written out in this file, kept once they have been read. Registration asks for them
	 * several times per item, and binding the same JSON over again for each of those is work for nothing.
	 * Only inline settings are kept: settings named by id are looked up fresh, since the entry they name
	 * may not have been registered the first time we were asked.
	 */
	private transient ItemSettings resolvedSettings;

	/**
	 * The item's settings, whether they were written out here, named as an entry in {@code item/property},
	 * or inherited from a {@code parent}.
	 */
	public ItemSettings getItemSettings() {
		boolean inline = !(itemSettings instanceof String) && !(itemSettings instanceof Identifier);
		if (inline && resolvedSettings != null) return resolvedSettings;

		ItemSettings settings = ItemSettings.resolve(itemSettings);
		if (inline) resolvedSettings = settings;
		return settings;
	}
}
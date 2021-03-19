package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ItemImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class Items implements AddonModule {
	@Override
	public void init(File file, ModIdAndAddonPath id) throws FileNotFoundException {
		io.github.vampirestudios.obsidian.api.obsidian.item.Item item = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
		try {
			if (item == null) return;
			RegistryUtils.registerItem(new ItemImpl(item, new net.minecraft.item.Item.Settings().group(item.information.getItemGroup())
					.maxCount(item.information.max_count)), item.information.name.id);
			register(ITEMS, "item", item.information.name.id.toString(), item);
		} catch (Exception e) {
			failedRegistering("item", item.information.name.id.toString(), e);
		}
	}

	@Override
	public String getType() {
		return "items";
	}
}

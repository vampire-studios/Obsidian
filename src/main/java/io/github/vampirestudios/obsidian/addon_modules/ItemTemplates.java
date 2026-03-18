package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class ItemTemplates implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		Item template = BaseGson.GSON.fromJson(new FileReader(file), Item.class);
		try {
			if (template == null) return;
			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replace(".json", ""));
			register(ContentRegistries.ITEM_TEMPLATES, "item_template", identifier, template);
		} catch (Exception e) {
			failedRegistering("item_template", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "item/template";
	}

}

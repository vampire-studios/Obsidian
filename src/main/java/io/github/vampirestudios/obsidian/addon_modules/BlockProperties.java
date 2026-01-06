package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.BlockSettings;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class BlockProperties implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		BlockSettings blockSettings = BaseGson.GSON.fromJson(new FileReader(file), BlockSettings.class);
		try {
			if (blockSettings == null) return;
			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
			register(ContentRegistries.BLOCK_SETTINGS, "block_properties", identifier, blockSettings);
		} catch (Exception e) {
			failedRegistering("block_properties", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "block/property";
	}

}

package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;

import java.io.File;
import java.io.FileNotFoundException;

public class EntityEvents implements AddonModule {
	@Override
	public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {

	}

	@Override
	public String getType() {
		return "entities/events";
	}
}

package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;

import java.io.FileNotFoundException;

public interface AddonModule {

	void init(ObsidianAddon addon, ModIdAndAddonPath id) throws FileNotFoundException;

	String getType();

}

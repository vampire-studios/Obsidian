package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;

import java.io.File;
import java.io.FileNotFoundException;

public interface AddonModule {

	void init(File file, ModIdAndAddonPath id) throws FileNotFoundException;

	String getType();

}

package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;

import java.io.File;
import java.io.IOException;

public interface AddonModule {

	void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException;

	String getType();

}

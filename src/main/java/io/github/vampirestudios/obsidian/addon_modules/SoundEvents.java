package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;

import java.io.File;
import java.io.IOException;

public class SoundEvents implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {

	}

	@Override
	public String getType() {
		return "sound_event";
	}
}

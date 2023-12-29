package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.Tier;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Tiers implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		Tier blockSettings = Obsidian.GSON.fromJson(new FileReader(file), Tier.class);
		try {
			if (blockSettings == null) return;
			ResourceLocation identifier = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
			register(ContentRegistries.TIERS, "tier", identifier, blockSettings);
		} catch (Exception e) {
			failedRegistering("tier", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "items/tiers";
	}

}

package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.block.CustomMaterial;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class BlockMaterials implements AddonModule {

	@Override
	public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
		CustomMaterial foodComponent1 = Obsidian.GSON.fromJson(new FileReader(file), CustomMaterial.class);
		try {
			if (foodComponent1 == null) return;
			register(BLOCK_MATERIALS, "block_material", foodComponent1.id, foodComponent1);
		} catch (Exception e) {
			failedRegistering("block_material", foodComponent1.id.toString(), e);
		}
	}

	@Override
	public String getType() {
		return "blocks/materials";
	}

}

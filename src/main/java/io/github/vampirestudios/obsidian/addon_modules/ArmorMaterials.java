package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class ArmorMaterials implements AddonModule {

	@Override
	public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
		ArmorMaterial armorMaterial = Obsidian.GSON.fromJson(new FileReader(file), ArmorMaterial.class);
		try {
			if (armorMaterial == null) return;
			register(ARMOR_MATERIALS, "armor_material", armorMaterial.name, armorMaterial);
		} catch (Exception e) {
			failedRegistering("armor_material", armorMaterial.name, e);
		}
	}

	@Override
	public String getType() {
		return "items/armor/materials";
	}

}

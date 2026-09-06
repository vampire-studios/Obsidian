package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class ArmorMaterials implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		ArmorMaterial armorMaterial = AddonFormats.read(addon, file, ArmorMaterial.class);
		try {
			if (armorMaterial == null) return;
			Identifier identifier = getIdentifier(armorMaterial, id, file);
			register(ContentRegistries.ARMOR_MATERIALS, "armor_material", identifier, armorMaterial);
		} catch (Exception e) {
			failedRegistering("armor_material", file.getName(), e);
		}
	}

	private Identifier getIdentifier(ArmorMaterial armorMaterial, BasicAddonInfo id, File file) {
		Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
		armorMaterial.name = identifier;
		return identifier;
	}

	@Override
	public String getType() {
		return "item/armor/material";
	}

}

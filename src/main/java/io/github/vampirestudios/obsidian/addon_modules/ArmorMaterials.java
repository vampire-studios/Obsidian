package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class ArmorMaterials implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		ArmorMaterial armorMaterial = Obsidian.GSON.fromJson(new FileReader(file), ArmorMaterial.class);
		try {
			if (armorMaterial == null) return;
			Identifier identifier = Objects.requireNonNullElseGet(
					armorMaterial.name,
					() -> new Identifier(id.modId(), file.getName().replaceAll(".json", ""))
			);
			if (armorMaterial.name == null) armorMaterial.name = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));
			register(ContentRegistries.ARMOR_MATERIALS, "armor_material", identifier, armorMaterial);
		} catch (Exception e) {
			failedRegistering("armor_material", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "items/armor/materials";
	}

}

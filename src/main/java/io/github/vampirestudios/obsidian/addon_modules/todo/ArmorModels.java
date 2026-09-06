package io.github.vampirestudios.obsidian.addon_modules.todo;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.ArmorModel;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class ArmorModels implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		ArmorModel entityModel = AddonFormats.read(addon, file, ArmorModel.class);
		try {
			if (entityModel == null) return;
			Identifier identifier = Objects.requireNonNullElseGet(
					entityModel.name,
					() -> Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file))
			);
			if (entityModel.name == null)
				entityModel.name = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			register(ContentRegistries.ARMOR_MODELS, "armor_model", identifier, entityModel);
		} catch (Exception e) {
			failedRegistering("armor_model", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "item/armor/model";
	}
}

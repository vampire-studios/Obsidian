package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.block.CustomMaterial;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class BlockMaterials implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		CustomMaterial customMaterial = Obsidian.GSON.fromJson(new FileReader(file), CustomMaterial.class);
		try {
			if (customMaterial == null) return;

			ResourceLocation identifier = Objects.requireNonNullElseGet(
					customMaterial.id,
					() -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
			);
			if (customMaterial.id == null) customMaterial.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));

			register(ContentRegistries.BLOCK_MATERIALS, "block_material", identifier, customMaterial);
		} catch (Exception e) {
			failedRegistering("block_material", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "blocks/materials";
	}

}

package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class BlockTemplates implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		Block template = AddonFormats.read(addon, file, Block.class);
		try {
			if (template == null) return;
			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			register(ContentRegistries.BLOCK_TEMPLATES, "block_template", identifier, template);
		} catch (Exception e) {
			failedRegistering("block_template", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "block/template";
	}

}

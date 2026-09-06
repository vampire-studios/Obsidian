package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.palette.Palette;
import io.github.vampirestudios.obsidian.api.obsidian.palette.PaletteResolver;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Palettes implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		Palette palette = AddonFormats.read(addon, file, Palette.class);
		try {
			if (palette == null) return;
			Identifier identifier = getIdentifier(palette, id, file);
			register(ContentRegistries.PALETTES, "palette", identifier, palette);
			PaletteResolver.invalidate();
		} catch (Exception e) {
			failedRegistering("palette", file.getName(), e);
		}
	}

	private Identifier getIdentifier(Palette palette, BasicAddonInfo id, File file) {
		Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
		palette.id = identifier;
		return identifier;
	}

	@Override
	public String getType() {
		return "palettes";
	}

}

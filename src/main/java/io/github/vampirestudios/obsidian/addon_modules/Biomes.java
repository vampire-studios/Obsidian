package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.world.Biome;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Biomes implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Biome biome = Obsidian.GSON.fromJson(new FileReader(file), Biome.class);
        try {
            if (biome == null) return;

            register(ContentRegistries.BIOMES, "biome", biome.id, biome);
        } catch (Exception e) {
            failedRegistering("biome", biome.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "world/biome";
    }
}

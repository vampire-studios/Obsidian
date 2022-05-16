package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.world.Biome;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Biomes implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Biome biome = Obsidian.GSON.fromJson(new FileReader(file), Biome.class);
        try {
            if (biome == null) return;

            register(BIOMES, "biome", biome.id, biome);
        } catch (Exception e) {
            failedRegistering("biome", biome.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "world/biome";
    }
}

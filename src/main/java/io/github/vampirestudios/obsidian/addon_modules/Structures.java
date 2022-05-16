package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.world.Structure;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Structures implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Structure structure = Obsidian.GSON.fromJson(new FileReader(file), Structure.class);
        try {
            if (structure == null) return;

            register(STRUCTURES, "structure", structure.id, structure);
        } catch (Exception e) {
            failedRegistering("structure", structure.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "world/structure";
    }
}

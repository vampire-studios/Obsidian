package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.world.Structure;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Structures implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Structure structure = Obsidian.GSON.fromJson(new FileReader(file), Structure.class);
        try {
            if (structure == null) return;

            register(ContentRegistries.STRUCTURES, "structure", structure.id, structure);
        } catch (Exception e) {
            failedRegistering("structure", structure.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "world/structure";
    }
}

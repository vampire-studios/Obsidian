package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.EntityModel;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class EntityModels implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        EntityModel entityModel = Obsidian.GSON.fromJson(new FileReader(file), EntityModel.class);
        try {
            if (entityModel == null) return;
            register(ENTITY_MODELS, "entity_model", entityModel.name, entityModel);
        } catch (Exception e) {
            failedRegistering("entity_model", entityModel.name.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "entities/models";
    }
}

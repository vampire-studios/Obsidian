package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.ArmorModel;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class ArmorModels implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        ArmorModel entityModel = Obsidian.GSON.fromJson(new FileReader(file), ArmorModel.class);
        try {
            if (entityModel == null) return;
            register(ARMOR_MODELS, "armor_model", entityModel.name, entityModel);
        } catch (Exception e) {
            failedRegistering("armor_model", entityModel.name.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/armor/models";
    }
}

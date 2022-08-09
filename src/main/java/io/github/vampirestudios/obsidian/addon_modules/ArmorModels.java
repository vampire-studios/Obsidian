package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.ArmorModel;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class ArmorModels implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        ArmorModel entityModel = Obsidian.GSON.fromJson(new FileReader(file), ArmorModel.class);
        try {
            if (entityModel == null) return;
            register(ContentRegistries.ARMOR_MODELS, "armor_model", entityModel.name, entityModel);
        } catch (Exception e) {
            failedRegistering("armor_model", entityModel.name.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/armor/models";
    }
}

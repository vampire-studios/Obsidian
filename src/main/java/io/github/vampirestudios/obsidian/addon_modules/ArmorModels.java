package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.ArmorModel;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class ArmorModels implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
        ArmorModel entityModel = Obsidian.GSON.fromJson(new FileReader(file), ArmorModel.class);
        try {
            if (entityModel == null) return;
            Identifier identifier = Objects.requireNonNullElseGet(
                    entityModel.name,
                    () -> new Identifier(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (entityModel.name == null) entityModel.name = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));
            register(ContentRegistries.ARMOR_MODELS, "armor_model", identifier, entityModel);
        } catch (Exception e) {
            failedRegistering("armor_model", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "items/armor/models";
    }
}

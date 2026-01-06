package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.ui.GUI;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Guis implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        GUI gui = BaseGson.GSON.fromJson(new FileReader(file), GUI.class);
        try {
            if (gui == null) return;
            Identifier fileId;
            if (gui.id != null) fileId = gui.id;
            else {
                fileId = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
                gui.id = fileId;
            }
            register(ContentRegistries.GUIS, "gui", fileId, gui);
        } catch (Exception e) {
            failedRegistering("gui", Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", "")), e);
        }
    }

    @Override
    public String getType() {
        return "gui";
    }
}

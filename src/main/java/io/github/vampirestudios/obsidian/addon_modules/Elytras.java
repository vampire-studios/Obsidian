package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.Elytra;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ElytraItemImpl;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import net.minecraft.item.Item;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Elytras implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Elytra item = Obsidian.GSON.fromJson(new FileReader(file), Elytra.class);
        try {
            if (item == null) return;
            RegistryUtils.registerItem(new ElytraItemImpl(item, new Item.Settings().group(item.information.getItemGroup())
                    .maxCount(1)), item.information.name.id);
            register(ContentRegistries.ELYTRAS, "elytra", item.information.name.id, item);
        } catch (Exception e) {
            failedRegistering("elytra", item.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/elytra";
    }
}

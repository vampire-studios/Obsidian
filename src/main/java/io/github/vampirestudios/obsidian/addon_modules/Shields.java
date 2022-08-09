package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.ShieldItem;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Shields implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        ShieldItem shieldItem = Obsidian.GSON.fromJson(new FileReader(file), ShieldItem.class);
        try {
            if(shieldItem == null) return;
//            ShieldItemImpl shieldItemImpl = new ShieldItemImpl(shieldItem, new Item.Settings().group(shieldItem.information.getItemGroup()));
//            REGISTRY_HELPER.registerItem(shieldItemImpl, shieldItem.information.name.id.getPath());
            register(ContentRegistries.SHIELDS, "shield", shieldItem.information.name.id, shieldItem);
        } catch (Exception e) {
            failedRegistering("shield", shieldItem.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/shields";
    }
}

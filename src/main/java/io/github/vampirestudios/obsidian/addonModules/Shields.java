package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.ShieldItem;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ShieldItemImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.item.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Shields implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        ShieldItem shieldItem = Obsidian.GSON.fromJson(new FileReader(file), ShieldItem.class);
        try {
            if(shieldItem == null) return;
            ShieldItemImpl shieldItemImpl = new ShieldItemImpl(shieldItem, new Item.Settings().group(shieldItem.information.getItemGroup()));
            REGISTRY_HELPER.registerItem(shieldItemImpl, shieldItem.information.name.id.getPath());
            register(SHIELDS, "shield", shieldItem.information.name.id, shieldItem);
        } catch (Exception e) {
            failedRegistering("shield", shieldItem.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/shields";
    }
}

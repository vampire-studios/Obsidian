package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.Elytra;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ElytraItemImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import net.minecraft.item.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Elytras implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Elytra item = Obsidian.GSON.fromJson(new FileReader(file), Elytra.class);
        try {
            if (item == null) return;
            RegistryUtils.registerItem(new ElytraItemImpl(item, new Item.Settings().group(item.information.getItemGroup())
                    .maxCount(1)), item.information.name.id);
            register(ELYTRAS, "elytra", item.information.name.id, item);
        } catch (Exception e) {
            failedRegistering("elytra", item.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/elytra";
    }
}

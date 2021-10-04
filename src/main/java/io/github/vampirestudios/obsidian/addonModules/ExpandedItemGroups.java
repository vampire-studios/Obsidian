package io.github.vampirestudios.obsidian.addonModules;

import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.ExpandedItemGroup;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.mixins.GroupTabLoaderAccessor;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ExpandedItemGroups implements AddonModule {

    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        ExpandedItemGroup itemGroup = Obsidian.GSON.fromJson(new FileReader(file), ExpandedItemGroup.class);
        JsonObject jsonObject = Obsidian.GSON.fromJson(new FileReader(file), JsonObject.class);

        try {
            if (itemGroup == null) return;
            GroupTabLoaderAccessor.callLoadGroups(jsonObject);
            ObsidianAddonLoader.register(ObsidianAddonLoader.EXPANDED_ITEM_GROUPS, "expanded_item_group", Utils.appendToPath(itemGroup.target_group, "_expanded"), itemGroup);
        } catch (Exception e) {
            ObsidianAddonLoader.failedRegistering("expanded_item_group", Utils.appendToPath(itemGroup.target_group, "_expanded").getPath(), e);
        }
    }

    @Override
    public String getType() {
        return "item_groups/expanded";
    }

}
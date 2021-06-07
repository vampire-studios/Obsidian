package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ItemGroups implements AddonModule {

    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        io.github.vampirestudios.obsidian.api.obsidian.ItemGroup itemGroup = Obsidian.GSON.fromJson(new FileReader(file),
                io.github.vampirestudios.obsidian.api.obsidian.ItemGroup.class);
        try {
            if (itemGroup == null) return;
            ItemGroup itemGroup1 = FabricItemGroupBuilder.create(itemGroup.name.id)
                    .icon(() -> new ItemStack(Registry.ITEM.get(itemGroup.icon)))
                    .build();
            Registry.register(Obsidian.ITEM_GROUP_REGISTRY, itemGroup.name.id, itemGroup1);
            ObsidianAddonLoader.register(ObsidianAddonLoader.ITEM_GROUPS, "block", itemGroup.name.id.toString(), itemGroup);
        } catch (Exception e) {
            ObsidianAddonLoader.failedRegistering("item group", itemGroup.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "item_groups";
    }

}
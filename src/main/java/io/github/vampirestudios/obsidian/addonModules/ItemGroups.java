package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
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
            FabricItemGroupBuilder itemGroup1 = FabricItemGroupBuilder.create(itemGroup.name.id)
                    .icon(() -> new ItemStack(Registry.ITEM.get(itemGroup.icon)));

            /*if (itemGroup.tags != null) {
                for (Identifier tag : itemGroup.tags) {
                    itemGroup1.appendItems(itemStacks -> {
                        if (ItemTags.getTagGroup().contains(tag)) {
                            Objects.requireNonNull(ItemTags.getTagGroup().getTag(tag)).values()
                                    .forEach(item -> itemStacks.add(new ItemStack(item)));
                        } else if (BlockTags.getTagGroup().contains(tag)) {
                            Objects.requireNonNull(BlockTags.getTagGroup().getTag(tag)).values()
                                    .forEach(block -> itemStacks.add(new ItemStack(block)));
                        }
                    });
                }
            }*/

            Registry.register(Obsidian.ITEM_GROUP_REGISTRY, itemGroup.name.id, itemGroup1.build());
            ObsidianAddonLoader.register(ObsidianAddonLoader.ITEM_GROUPS, "item_group", itemGroup.name.id, itemGroup);
        } catch (Exception e) {
            ObsidianAddonLoader.failedRegistering("item_group", itemGroup.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "item_groups";
    }

}
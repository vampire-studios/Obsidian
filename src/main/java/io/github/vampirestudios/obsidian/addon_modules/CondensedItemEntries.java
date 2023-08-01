package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.TabbedGroup;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.CondensedEntry;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.wispforest.condensed_creative.entry.impl.CondensedItemEntry;
import io.wispforest.condensed_creative.registry.CondensedEntryRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class CondensedItemEntries implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        CondensedEntry condensedEntry = Obsidian.GSON.fromJson(new FileReader(file), CondensedEntry.class);

        try {
            if (condensedEntry == null) return;
            CondensedItemEntry.Builder builder;
            if (condensedEntry.type == CondensedEntry.Type.ITEM_TAG) {
                builder = CondensedEntryRegistry.fromTag(condensedEntry.name, BuiltInRegistries.ITEM.get(condensedEntry.base), TagKey.create(net.minecraft.core.registries.Registries.ITEM, condensedEntry.tag));
                if (condensedEntry.specificCreativeTab) {
                    CreativeModeTab itemGroup = BuiltInRegistries.CREATIVE_MODE_TAB.get(condensedEntry.targetGroup);
                    TabbedGroup tabbedGroup = Registries.EXPANDED_ITEM_GROUPS.get(condensedEntry.tabbedGroup);
                    if (condensedEntry.specificTabbedGroup)
                        builder.addToItemGroup(itemGroup, Registries.EXPANDED_ITEM_GROUPS.getId(tabbedGroup));
                    else builder.addToItemGroup(BuiltInRegistries.CREATIVE_MODE_TAB.get(condensedEntry.targetGroup));
                }

                if (condensedEntry.specificCreativeTab)
                    builder.addToItemGroup(BuiltInRegistries.CREATIVE_MODE_TAB.get(condensedEntry.targetGroup));
            } else if (condensedEntry.type == CondensedEntry.Type.BLOCK_TAG) {
                builder = CondensedEntryRegistry.fromTag(condensedEntry.name, BuiltInRegistries.ITEM.get(condensedEntry.base), TagKey.create(net.minecraft.core.registries.Registries.BLOCK, condensedEntry.tag));
                if (condensedEntry.specificCreativeTab) {
                    CreativeModeTab itemGroup = BuiltInRegistries.CREATIVE_MODE_TAB.get(condensedEntry.targetGroup);
                    TabbedGroup tabbedGroup = Registries.EXPANDED_ITEM_GROUPS.get(condensedEntry.tabbedGroup);
                    if (condensedEntry.specificTabbedGroup)
                        builder.addToItemGroup(itemGroup, Registries.EXPANDED_ITEM_GROUPS.getId(tabbedGroup));
                    else builder.addToItemGroup(BuiltInRegistries.CREATIVE_MODE_TAB.get(condensedEntry.targetGroup));
                }

                if (condensedEntry.specificCreativeTab)
                    builder.addToItemGroup(BuiltInRegistries.CREATIVE_MODE_TAB.get(condensedEntry.targetGroup));
            } else if (condensedEntry.type == CondensedEntry.Type.ITEM_LIST) {
                List<Item> items = new ArrayList<>();
                condensedEntry.items.forEach(identifier -> items.add(BuiltInRegistries.ITEM.get(identifier)));
                builder = CondensedEntryRegistry.fromItems(condensedEntry.name, BuiltInRegistries.ITEM.get(condensedEntry.base), items);
                if (condensedEntry.specificCreativeTab) {
                    CreativeModeTab itemGroup = BuiltInRegistries.CREATIVE_MODE_TAB.get(condensedEntry.targetGroup);
                    TabbedGroup tabbedGroup = Registries.EXPANDED_ITEM_GROUPS.get(condensedEntry.tabbedGroup);
                    if (condensedEntry.specificTabbedGroup)
                        builder.addToItemGroup(itemGroup, Registries.EXPANDED_ITEM_GROUPS.getId(tabbedGroup));
                    else builder.addToItemGroup(BuiltInRegistries.CREATIVE_MODE_TAB.get(condensedEntry.targetGroup));
                }

                if (condensedEntry.specificCreativeTab)
                    builder.addToItemGroup(BuiltInRegistries.CREATIVE_MODE_TAB.get(condensedEntry.targetGroup));
            }
            register(ContentRegistries.CONDENSED_ITEM_ENTRIES, "condensed_item", new ResourceLocation(id.modId(), "condensed_" + condensedEntry.base.getPath() + "_entry"), condensedEntry);
        } catch (Exception e) {
            failedRegistering("condensed_item", "condensed_" + condensedEntry.base.getPath() + "_entry", e);
        }
    }

    @Override
    public String getType() {
        return "item_groups/condensed_items";
    }

}

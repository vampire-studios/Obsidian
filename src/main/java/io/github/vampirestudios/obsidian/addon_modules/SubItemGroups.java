package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import de.dafuqs.fractal.api.ItemSubGroup;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.SubItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class SubItemGroups implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        SubItemGroup itemGroup = Obsidian.GSON.fromJson(new FileReader(file), SubItemGroup.class);

        try {
            if (itemGroup == null) return;
            CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.get(itemGroup.targetGroup);
            assert tab != null;
            ItemSubGroup.Builder builder = new ItemSubGroup.Builder(tab, Component.translatable(tab.getDisplayName().getString() + "." + itemGroup.name.id.getPath()));
            if (itemGroup.hasCustomTexture) builder.backgroundTexture(itemGroup.customTexture);
            builder.entries((displayContext, entries) -> {
                if (itemGroup.tags != null) {
                    for (Map.Entry<String, ResourceLocation> tag : itemGroup.tags.entrySet()) {
                        if (tag.getKey().equals("block")) {
                            TagKey<Block> blockTagKey = TagKey.create(net.minecraft.core.registries.Registries.BLOCK, tag.getValue());
                            entries.accept(BuiltInRegistries.BLOCK.get(blockTagKey.location()));
                        }
                        if (tag.getKey().equals("item")) {
                            TagKey<Item> blockTagKey = TagKey.create(net.minecraft.core.registries.Registries.ITEM, tag.getValue());
                            entries.accept(BuiltInRegistries.ITEM.get(blockTagKey.location()));
                        }
                    }
                }
                if (itemGroup.items != null) {
                    for (ResourceLocation item : itemGroup.items) {
                        entries.accept(BuiltInRegistries.ITEM.get(item));
                    }
                }
                if (itemGroup.blocks != null) {
                    for (ResourceLocation block : itemGroup.blocks) {
                        entries.accept(BuiltInRegistries.BLOCK.get(block));
                    }
                }
                if (itemGroup.opItems != null && displayContext.hasPermissions()) {
                    for (ResourceLocation item : itemGroup.opItems) {
                        entries.accept(BuiltInRegistries.ITEM.get(item));
                    }
                }
                if (itemGroup.opBlocks != null && displayContext.hasPermissions()) {
                    for (ResourceLocation block : itemGroup.opBlocks) {
                        entries.accept(BuiltInRegistries.BLOCK.get(block));
                    }
                }
                if (itemGroup.featureSetItems != null) {
                    for (Map.Entry<String, ResourceLocation> entry : itemGroup.featureSetItems.entrySet()) {
                        if (entry.getKey().equals("vanilla") && displayContext.enabledFeatures().contains(FeatureFlags.VANILLA)) {
                            entries.accept(BuiltInRegistries.ITEM.get(entry.getValue()));
                        }
                        if (entry.getKey().equals("bundle") && displayContext.enabledFeatures().contains(FeatureFlags.BUNDLE)) {
                            entries.accept(BuiltInRegistries.ITEM.get(entry.getValue()));
                        }
                    }
                }
                if (itemGroup.featureSetBlocks != null) {
                    for (Map.Entry<String, ResourceLocation> entry : itemGroup.featureSetBlocks.entrySet()) {
                        if (entry.getKey().equals("vanilla") && displayContext.enabledFeatures().contains(FeatureFlags.VANILLA)) {
                            entries.accept(BuiltInRegistries.BLOCK.get(entry.getValue()));
                        }
                        if (entry.getKey().equals("bundle") && displayContext.enabledFeatures().contains(FeatureFlags.BUNDLE)) {
                            entries.accept(BuiltInRegistries.BLOCK.get(entry.getValue()));
                        }
                    }
                }
            });
            builder.build();
//            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, itemGroup.name.id, builder.build());
            register(Registries.SUB_ITEM_GROUPS, "sub_group", new ResourceLocation(itemGroup.name.id.getNamespace(), itemGroup.name.id.getPath() + "_sub_group"), itemGroup);
        } catch (Exception e) {
            failedRegistering("sub_group", itemGroup.name.id.getPath() + "_sub_group", e);
        }
    }

    @Override
    public String getType() {
        return "item_groups/sub_groups";
    }

}

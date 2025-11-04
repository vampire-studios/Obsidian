package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.SubItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;

public class SubItemGroups implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        SubItemGroup itemGroup = BaseGson.GSON.fromJson(new FileReader(file), SubItemGroup.class);

        try {
            if (itemGroup == null) return;
            CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValue(itemGroup.targetGroup);

            ResourceLocation tabId;
            if (itemGroup.name != null) {
                if (itemGroup.name.id != null) {
                    tabId = itemGroup.name.id;
                } else {
                    tabId = ResourceLocation.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
                    itemGroup.name.id = tabId;
                }
            } else {
                tabId = ResourceLocation.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
                NameInformation nameInformation = new NameInformation();
                nameInformation.id = tabId;
                itemGroup.name = nameInformation;
            }

//            ItemSubGroup.Builder builder = new ItemSubGroup.Builder(tab, tabId, Component.literal(WordUtils.capitalizeFully(tabId.getPath())));
//            if (itemGroup.styling != null) {
//                SubItemGroup.Styling styling = itemGroup.styling;
//                ItemSubGroupStyle.Builder style = new ItemSubGroupStyle.Builder();
//                if (styling.hasCustomBackground) style.background(styling.customBackground);
//                if (styling.hasCustomScrollBar) style.scrollbar(styling.customScrollBar[0], styling.customScrollBar[1]);
//                if (styling.hasCustomSubTab) style.subtab(styling.customSubTab[0], styling.customSubTab[1],
//                        styling.customSubTab[2], styling.customSubTab[3]
//                );
//                if (styling.hasCustomTab) style.tab(styling.customTab[0], styling.customTab[1], styling.customTab[2], styling.customTab[3],
//                        styling.customTab[4], styling.customTab[5], styling.customTab[6], styling.customTab[7], styling.customTab[8],
//                        styling.customTab[9], styling.customTab[10], styling.customTab[11]
//                );
//                builder.styled(style.build());
//            }
//            builder.entries((displayContext, entries) -> {
//                if (itemGroup.tags != null) {
//                    for (Map.Entry<String, ResourceLocation> tag : itemGroup.tags.entrySet()) {
//                        if (tag.getKey().equals("block")) {
//                            TagKey<Block> blockTagKey = TagKey.create(net.minecraft.core.registries.Registries.BLOCK, tag.getValue());
//                            entries.accept(BuiltInRegistries.BLOCK.getValue(blockTagKey.location()));
//                        }
//                        if (tag.getKey().equals("item")) {
//                            TagKey<Item> blockTagKey = TagKey.create(net.minecraft.core.registries.Registries.ITEM, tag.getValue());
//                            entries.accept(BuiltInRegistries.ITEM.getValue(blockTagKey.location()));
//                        }
//                    }
//                }
//                if (itemGroup.items != null) {
//                    for (ResourceLocation item : itemGroup.items) {
//                        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.getValue(item));
//                        if (stack.getCount() != 1) {
//                            System.out.println(item);
//                        } else {
//                            entries.accept(BuiltInRegistries.ITEM.getValue(item));
//                        }
//                    }
//                }
//                if (itemGroup.blocks != null) {
//                    for (ResourceLocation block : itemGroup.blocks) {
//                        entries.accept(BuiltInRegistries.BLOCK.getValue(block));
//                    }
//                }
//                if (itemGroup.opItems != null && displayContext.hasPermissions()) {
//                    for (ResourceLocation item : itemGroup.opItems) {
//                        entries.accept(BuiltInRegistries.ITEM.getValue(item));
//                    }
//                }
//                if (itemGroup.opBlocks != null && displayContext.hasPermissions()) {
//                    for (ResourceLocation block : itemGroup.opBlocks) {
//                        entries.accept(BuiltInRegistries.BLOCK.getValue(block));
//                    }
//                }
//                if (itemGroup.featureSetItems != null) {
//                    for (Map.Entry<String, ResourceLocation> entry : itemGroup.featureSetItems.entrySet()) {
//                        if (entry.getKey().equals("vanilla") && displayContext.enabledFeatures().contains(FeatureFlags.VANILLA)) {
//                            entries.accept(BuiltInRegistries.ITEM.getValue(entry.getValue()));
//                        }
//                        if (entry.getKey().equals("winter_drop") && displayContext.enabledFeatures().contains(FeatureFlags.WINTER_DROP)) {
//                            entries.accept(BuiltInRegistries.ITEM.getValue(entry.getValue()));
//                        }
//                    }
//                }
//                if (itemGroup.featureSetBlocks != null) {
//                    for (Map.Entry<String, ResourceLocation> entry : itemGroup.featureSetBlocks.entrySet()) {
//                        if (entry.getKey().equals("vanilla") && displayContext.enabledFeatures().contains(FeatureFlags.VANILLA)) {
//                            entries.accept(BuiltInRegistries.BLOCK.getValue(entry.getValue()));
//                        }
//                        if (entry.getKey().equals("winter_drop") && displayContext.enabledFeatures().contains(FeatureFlags.WINTER_DROP)) {
//                            entries.accept(BuiltInRegistries.BLOCK.getValue(entry.getValue()));
//                        }
//                    }
//                }
//            });
//            builder.build();
//            register(Registries.SUB_ITEM_GROUPS, "sub_group", tabId.withSuffix("_sub_group"), itemGroup);
        } catch (Exception e) {
            failedRegistering("sub_group", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "creative_tab/sub_group";
    }

}

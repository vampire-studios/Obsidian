package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.hjson.JsonValue;
import org.hjson.Stringify;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class LegacyItemGroups implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError, DeserializationException {
        io.github.vampirestudios.obsidian.api.obsidian.ItemGroup itemGroup;
        if (addon.getConfigPackInfo() instanceof LegacyObsidianAddonInfo) {
            itemGroup = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.ItemGroup.class);
        } else {
            ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addon.getConfigPackInfo();
            if (addonInfo.format == ObsidianAddonInfo.Format.JSON) {
                itemGroup = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.ItemGroup.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.JSON5) {
                JsonObject jsonObject = Obsidian.JANKSON.load(file);
                itemGroup = Obsidian.JANKSON.fromJson(jsonObject, io.github.vampirestudios.obsidian.api.obsidian.ItemGroup.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.YAML) {
                Yaml yaml = new Yaml();
                InputStream inputStream = this.getClass()
                        .getClassLoader()
                        .getResourceAsStream(file.getPath());
                itemGroup = yaml.load(inputStream);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.HJSON) {
                itemGroup = Obsidian.GSON.fromJson(JsonValue.readHjson(new FileReader(file)).toString(Stringify.FORMATTED), io.github.vampirestudios.obsidian.api.obsidian.ItemGroup.class);
            } else {
                itemGroup = null;
            }
        }
        try {
            if (itemGroup == null) return;

            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    itemGroup.name.id,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (itemGroup.name.id == null) itemGroup.name.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));

            CreativeModeTab itemGroup1 = FabricItemGroup.builder(identifier)
                    .icon(() -> new ItemStack(net.minecraft.registry.Registries.ITEM.get(itemGroup.icon)))
                    .entries((displayContext, entries) -> {
                        if (itemGroup.tags != null) {
                            for (Map.Entry<String, Identifier> tag : itemGroup.tags.entrySet()) {
                                if (tag.getKey().equals("block")) {
                                    TagKey<Block> blockTagKey = TagKey.of(RegistryKeys.BLOCK, tag.getValue());
                                    entries.add(net.minecraft.registry.Registries.BLOCK.get(blockTagKey.id()));
                                }
                                if (tag.getKey().equals("item")) {
                                    TagKey<Item> blockTagKey = TagKey.of(RegistryKeys.ITEM, tag.getValue());
                                    entries.add(net.minecraft.registry.Registries.ITEM.get(blockTagKey.id()));
                                }
                            }
                        }
                        if (itemGroup.items != null) {
                            for (Identifier item : itemGroup.items) {
                                entries.add(net.minecraft.registry.Registries.ITEM.get(item));
                            }
                        }
                        if (itemGroup.blocks != null) {
                            for (Identifier block : itemGroup.blocks) {
                                entries.add(net.minecraft.registry.Registries.BLOCK.get(block));
                            }
                        }
                        if (itemGroup.opItems != null && displayContext.comp_1252()) {
                            for (Identifier item : itemGroup.opItems) {
                                entries.add(net.minecraft.registry.Registries.ITEM.get(item));
                            }
                        }
                        if (itemGroup.opBlocks != null && displayContext.comp_1252()) {
                            for (Identifier block : itemGroup.opBlocks) {
                                entries.add(net.minecraft.registry.Registries.BLOCK.get(block));
                            }
                        }
                        if (itemGroup.featureSetItems != null) {
                            for (Map.Entry<String, Identifier> entry : itemGroup.featureSetItems.entrySet()) {
                                if (entry.getKey().equals("vanilla") && displayContext.comp_1251().contains(FeatureFlags.VANILLA)) {
                                    entries.add(net.minecraft.registry.Registries.ITEM.get(entry.getValue()));
                                }
                                if (entry.getKey().equals("bundle") && displayContext.comp_1251().contains(FeatureFlags.BUNDLE)) {
                                    entries.add(net.minecraft.registry.Registries.ITEM.get(entry.getValue()));
                                }
                            }
                        }
                        if (itemGroup.featureSetBlocks != null) {
                            for (Map.Entry<String, Identifier> entry : itemGroup.featureSetBlocks.entrySet()) {
                                if (entry.getKey().equals("vanilla") && displayContext.comp_1251().contains(FeatureFlags.VANILLA)) {
                                    entries.add(net.minecraft.registry.Registries.BLOCK.get(entry.getValue()));
                                }
                                if (entry.getKey().equals("bundle") && displayContext.comp_1251().contains(FeatureFlags.BUNDLE)) {
                                    entries.add(net.minecraft.registry.Registries.BLOCK.get(entry.getValue()));
                                }
                            }
                        }
                    })
                    .build();

            Registry.register(Registries.ITEM_GROUP_REGISTRY, identifier, itemGroup1);
            register(ContentRegistries.ITEM_GROUPS, "item_group", identifier, itemGroup);
        } catch (Exception e) {
            failedRegistering("item_group", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "item_groups";
    }

}
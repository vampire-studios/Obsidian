package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.SyntaxError;
import io.github.cottonmc.jankson.JanksonFactory;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
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
                JsonObject jsonObject = JanksonFactory.builder().build().load(file);
                itemGroup = JanksonFactory.builder().build().fromJson(jsonObject, io.github.vampirestudios.obsidian.api.obsidian.ItemGroup.class);
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

            CreativeModeTab itemGroup1 = FabricItemGroup.builder()
                    .icon(() -> new ItemStack(BuiltInRegistries.ITEM.get(itemGroup.icon)))
                    .title(Component.literal(identifier.getPath()))
                    .displayItems((displayContext, entries) -> {
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
                    })
                    .build();
            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, identifier, itemGroup1);
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
package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.hjson.JsonValue;
import org.hjson.Stringify;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class LegacyItemGroups implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
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
            ItemGroup itemGroup1 = QuiltItemGroup.createWithIcon(itemGroup.name.id,
                    () -> new ItemStack(Registry.ITEM.get(itemGroup.icon)));

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

            Registry.register(Registries.ITEM_GROUP_REGISTRY, itemGroup.name.id, itemGroup1);
            register(ContentRegistries.ITEM_GROUPS, "item_group", itemGroup.name.id, itemGroup);
        } catch (Exception e) {
            failedRegistering("item_group", itemGroup.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "item_groups";
    }

}
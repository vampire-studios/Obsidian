package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Tools implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem tool = BaseGson.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem.class);
        try {
            if (tool == null) return;

            Identifier identifier = Objects.requireNonNullElseGet(
                    tool.information.name.id,
                    () -> Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""))
            );

            if (tool.information.name.id == null) tool.information.name.id = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));

            ToolMaterial material = tool.getToolMaterial();
            Item.Properties settings = new Item.Properties().stacksTo(tool.information.getItemSettings().maxStackSize)
                    .rarity(Rarity.valueOf(tool.information.getItemSettings().rarity.toUpperCase(Locale.ROOT)))
                    .setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));
            if (tool.components != null) {
                for (Map.Entry<DataComponentType<?>, Optional<?>> dataComponentTypeOptionalEntry : tool.components.entrySet()) {
                    settings.component((DataComponentType)dataComponentTypeOptionalEntry.getKey(), dataComponentTypeOptionalEntry.getValue().orElseThrow());
                }
            }

            Item item = null;
            switch (tool.tool_type) {
                case "pickaxe" -> item = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new PickaxeItemImpl(tool, material, settings));
                case "shovel" -> item = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new ShovelItemImpl(tool, material, settings));
                case "hoe" -> item = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new HoeItemImpl(tool, material, settings));
                case "axe" -> item = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new AxeItemImpl(tool, material, settings));
            }
            Item finalItem = item;
            ItemGroupEvents.modifyEntriesEvent(tool.information.getItemSettings().getItemGroup()).register(entries -> entries.accept(finalItem));
            register(ContentRegistries.TOOLS, "tool", identifier, tool);
        } catch (Exception e) {
            failedRegistering("tool", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "item/tool";
    }
}

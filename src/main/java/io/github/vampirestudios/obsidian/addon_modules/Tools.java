package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.minecraft.obsidian.AxeItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.HoeItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.PickaxeItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ShovelItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Tools implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem tool = BaseGson.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem.class);
        try {
            if (tool == null) return;

            Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
            tool.information.id = identifier;

            ToolMaterial material = tool.getToolMaterial();

            Item.Properties settings = createItemProperties(tool).setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));
            ResourceKey<CreativeModeTab> creativeTab = getCreativeTab(tool);

            if (tool.tool_type == null) throw new IllegalArgumentException("tool_type must be specified");
            Item item = switch (tool.tool_type) {
                case PICKAXE -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new PickaxeItemImpl(tool, material, settings));
                case SHOVEL  -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new ShovelItemImpl(tool, material, settings));
                case HOE     -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new HoeItemImpl(tool, material, settings));
                case AXE     -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new AxeItemImpl(tool, material, settings));
            };
            Item finalItem = item;
            ItemGroupEvents.modifyEntriesEvent(creativeTab).register(entries -> entries.accept(finalItem));
            register(ContentRegistries.TOOLS, "tool", identifier, tool);
        } catch (Exception e) {
            failedRegistering("tool", file.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> void applyAllComponents(Item.Properties props, DataComponentPatch map) {
        for (var e : map.entrySet()) {
            var type = (DataComponentType<T>) e.getKey();
            var opt  = (Optional<T>) e.getValue();
            opt.ifPresent(v -> props.component(type, v));
        }
    }

    private Item.Properties createItemProperties(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
        Item.Properties props = new Item.Properties();

        var comps = item.components;
        if (comps == null) {
            return props;
        }

        applyAllComponents(props, comps);
        return props;
    }

    private ResourceKey<CreativeModeTab> getCreativeTab(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
        ResourceKey<CreativeModeTab> creativeTab;

        // Check for OItemComponents.CREATIVE_TAB first
        if (item.components != null && item.components.get(OItemComponents.CREATIVE_TAB) != null &&
                item.components.get(OItemComponents.CREATIVE_TAB).isPresent()) {
            Identifier tabLocation = (Identifier) Objects.requireNonNull(item.components.get(OItemComponents.CREATIVE_TAB)).orElseThrow();
            creativeTab = ResourceKey.create(Registries.CREATIVE_MODE_TAB, tabLocation);
        } else if (item.information.getItemSettings().getItemGroup() != null) {
            creativeTab = item.information.getItemSettings().getItemGroup();
        } else if (item.information.getItemSettings().getParentSettings().getItemGroup() != null) {
            creativeTab = item.information.getItemSettings().getParentSettings().getItemGroup();
        } else {
            creativeTab = CreativeModeTabs.BUILDING_BLOCKS;
        }
        return creativeTab;
    }

    @Override
    public String getType() {
        return "item/tool";
    }
}

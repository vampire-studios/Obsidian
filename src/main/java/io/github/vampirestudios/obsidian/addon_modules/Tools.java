package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.ConvertibleBlockPair;
import io.github.vampirestudios.obsidian.ConvertibleBlocksRegistry;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Tools implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        ToolItem tool = BaseGson.GSON.fromJson(new FileReader(file), ToolItem.class);
        try {
            if (tool == null) return;

            Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
            tool.information.id = identifier;

            if (tool.tool_type == null) throw new IllegalArgumentException("tool_type must be specified");

            Item.Properties settings = new Item.Properties();
            if (!tool.damageable) settings.component(DataComponents.UNBREAKABLE, Unit.INSTANCE);
            if (tool.components != null) ItemModuleHelper.applyAllComponents(settings, tool.components);
            settings.setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));

            ToolMaterial m = tool.getToolMaterial();
            Item item = switch (tool.tool_type) {
                case PICKAXE    -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new PickaxeItemImpl(tool, m, settings));
                case SHOVEL     -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new ShovelItemImpl(tool, m, settings));
                case HOE        -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new HoeItemImpl(tool, m, settings));
                case AXE        -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new AxeItemImpl(tool, m, settings));
                case BRUSH      -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new BrushItemImpl(tool, settings));
                case PAXEL      -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new PaxelItemImpl(tool, m, settings));
                case MATTOCK    -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new MattockItemImpl(tool, m, settings));
                case HAMMER     -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new HammerItemImpl(tool, m, settings));
                case DRILL      -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new DrillItemImpl(tool, m, settings));
                case EXCAVATOR  -> REGISTRY_HELPER.items().registerItem(identifier.getPath(), new ExcavatorItemImpl(tool, m, settings));
                case CHISEL     -> registerChisel(tool, m, settings, identifier, id);
            };
            CreativeModeTabEvents.modifyOutputEvent(ItemModuleHelper.getCreativeTab(tool)).register(entries -> entries.accept(item));
            register(ContentRegistries.TOOLS, "tool", identifier, tool);
        } catch (Exception e) {
            failedRegistering("tool", file.getName(), e);
        }
    }

    private static Item registerChisel(ToolItem tool, ToolMaterial m, Item.Properties settings,
                                       Identifier identifier, BasicAddonInfo id) {
        ChiselItemImpl chisel = new ChiselItemImpl(tool, m, settings);
        Item registered = REGISTRY_HELPER.items().registerItem(identifier.getPath(), chisel);

        if (tool.chisel_mappings != null) {
            for (ToolItem.ChiselMapping mapping : tool.chisel_mappings) {
                try {
                    Identifier fromId = Identifier.tryParse(mapping.from);
                    Identifier toId   = Identifier.tryParse(mapping.to);
                    if (fromId == null || toId == null) {
                        Obsidian.LOGGER.warn("[Obsidian] Chisel mapping has invalid identifier in {}", identifier);
                        continue;
                    }
                    Block fromBlock = BuiltInRegistries.BLOCK.get(fromId).orElseThrow().value();
                    Block toBlock   = BuiltInRegistries.BLOCK.get(toId).orElseThrow().value();

                    SoundEvent sound = mapping.sound != null
                            ? BuiltInRegistries.SOUND_EVENT.get(Identifier.tryParse(mapping.sound)).map(net.minecraft.core.Holder::value).orElse(null)
                            : null;
                    Item droppedItem = mapping.dropped_item != null
                            ? BuiltInRegistries.ITEM.get(Identifier.tryParse(mapping.dropped_item)).map(net.minecraft.core.Holder::value).orElse(null)
                            : null;

                    ConvertibleBlockPair.ConversionItem convItem = ConvertibleBlockPair.ConversionItem.of(registered);
                    ConvertibleBlockPair.ConversionItem reversalItem = resolveReversalItem(mapping, registered);

                    ConvertibleBlockPair pair = mapping.reversible
                            ? new ConvertibleBlockPair(fromBlock, toBlock, convItem, reversalItem, sound, droppedItem)
                            : new ConvertibleBlockPair(fromBlock, toBlock, convItem, sound, droppedItem);
                    ConvertibleBlocksRegistry.registerConvertibleBlockPair(pair);
                } catch (Exception ex) {
                    Obsidian.LOGGER.warn("[Obsidian] Failed to register chisel mapping {} → {} for {}: {}",
                            mapping.from, mapping.to, identifier, ex.getMessage());
                }
            }
        }
        return registered;
    }

    private static ConvertibleBlockPair.ConversionItem resolveReversalItem(ToolItem.ChiselMapping mapping, Item chisel) {
        if (mapping.reversal_item == null) return ConvertibleBlockPair.ConversionItem.of(chisel);
        if (mapping.reversal_item.tag != null) {
            Identifier tagId = Identifier.tryParse(mapping.reversal_item.tag);
            if (tagId != null) return ConvertibleBlockPair.ConversionItem.of(TagKey.create(net.minecraft.core.registries.Registries.ITEM, tagId));
        }
        if (mapping.reversal_item.item != null) {
            Identifier itemId = Identifier.tryParse(mapping.reversal_item.item);
            if (itemId != null) {
                Item reversalItem = BuiltInRegistries.ITEM.get(itemId).map(net.minecraft.core.Holder::value).orElse(null);
                if (reversalItem != null) return ConvertibleBlockPair.ConversionItem.of(reversalItem);
            }
        }
        return ConvertibleBlockPair.ConversionItem.of(chisel);
    }

    @Override
    public String getType() {
        return "item/tool";
    }
}

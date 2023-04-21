package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.item.ItemInformation;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Items implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        io.github.vampirestudios.obsidian.api.obsidian.item.Item item = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
        try {
            if (item == null) return;
			FabricItemSettings settings = new FabricItemSettings()
					.stacksTo(item.information.maxStackSize)
                    .rarity(item.information.rarity);

            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    item.information.name.id,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (item.information.name.id == null) item.information.name.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));

            RegistryHelperItemExpanded expanded = (RegistryHelperItemExpanded) REGISTRY_HELPER.items();

            Item registeredItem;
            if (item.information.canPlaceBlock) {
                registeredItem = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new BlockItemImpl(item, BuiltInRegistries.BLOCK.get(item.information.placableBlock),
                        settings));
            } else {
                if (item.information.renderModeModels != null) {
                    for (ItemInformation.RenderModeModel renderModeModel : item.information.renderModeModels) {
                        ClientInit.customModels.add(renderModeModel.model);
                    }
                }
                if (item.information.wearable) {
                    settings.equipmentSlot(stack -> EquipmentSlot.byName(item.information.wearableSlot));
                    if (item.information.dyeable) {
                        registeredItem = expanded.registerDyeableItem(new WearableAndDyeableItemImpl(item, settings), identifier.getPath(), item.information.getItemGroup());
                    } else {
                        registeredItem = expanded.registerItem(identifier.getPath(), new WearableItemImpl(item, settings), item.information.getItemGroup());
                    }
                } else {
                    if (item.information.dyeable) {
                        registeredItem = expanded.registerDyeableItem(new DyeableItemImpl(item, settings), identifier.getPath(), item.information.getItemGroup());
                    } else {
                        registeredItem = expanded.registerItem(identifier.getPath(), new ItemImpl(item, settings), item.information.getItemGroup());
                    }
                }
            }
            ItemGroupEvents.modifyEntriesEvent(item.information.getItemGroup()).register(entries -> entries.add(registeredItem));
            register(ContentRegistries.ITEMS, "item", identifier, item);
        } catch (Exception e) {
            failedRegistering("item", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "items";
    }
}

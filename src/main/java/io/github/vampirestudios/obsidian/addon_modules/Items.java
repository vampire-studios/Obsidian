package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.ItemInformation;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Items implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        io.github.vampirestudios.obsidian.api.obsidian.item.Item item = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
        try {
            if (item == null) return;
			QuiltItemSettings settings = new QuiltItemSettings()
                    .group(item.information.getItemGroup())
					.maxCount(item.information.maxStackSize)
                    .rarity(item.information.rarity);

            Identifier identifier = Objects.requireNonNullElseGet(
                    item.information.name.id,
                    () -> new Identifier(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (item.information.name.id == null) item.information.name.id = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));

            if (item.information.canPlaceBlock) {
                RegistryUtils.registerItem(new BlockItemImpl(item, Registry.BLOCK.get(item.information.placableBlock),
                        settings), identifier);
            } else {
                if (item.information.renderModeModels != null) {
                    for (ItemInformation.RenderModeModel renderModeModel : item.information.renderModeModels) {
                        ClientInit.customModels.add(renderModeModel.model);
                    }
                }
                if (item.information.wearable) {
                    settings.equipmentSlot(stack -> EquipmentSlot.byName(item.information.wearableSlot));
                    if (item.information.dyeable) {
                        RegistryUtils.registerItem(new WearableAndDyeableItemImpl(item, settings), identifier);
                    } else {
                        RegistryUtils.registerItem(new WearableItemImpl(item, settings), identifier);
                    }
                } else {
                    if (item.information.dyeable) {
                        RegistryUtils.registerItem(new DyeableItemImpl(item, settings), identifier);
                    } else {
                        RegistryUtils.registerItem(new ItemImpl(item, settings), identifier);
                    }
                }
            }
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

package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.ItemInformation;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.BlockItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.WearableItemImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Items implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        io.github.vampirestudios.obsidian.api.obsidian.item.Item item = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
        try {
            if (item == null) return;
			Item.Settings settings = new Item.Settings().group(item.information.getItemGroup())
					.maxCount(item.information.max_count).rarity(item.information.getRarity());
            if (item.information.can_place_block) {
                RegistryUtils.registerItem(new BlockItemImpl(item, Registry.BLOCK.get(item.information.placable_block),
                        settings), item.information.name.id);
            } else {
                if (item.information.renderModeModels != null) {
                    for (ItemInformation.RenderModeModel renderModeModel : item.information.renderModeModels) {
                        ClientInit.customModels.add(renderModeModel.model);
                    }
                }
                if (item.information.wearable) {
                    RegistryUtils.registerItem(new WearableItemImpl(item, settings), item.information.name.id);
                } else {
                    RegistryUtils.registerItem(new ItemImpl(item, settings), item.information.name.id);
                }
            }
            register(ITEMS, "item", item.information.name.id, item);
        } catch (Exception e) {
            failedRegistering("item", item.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items";
    }
}

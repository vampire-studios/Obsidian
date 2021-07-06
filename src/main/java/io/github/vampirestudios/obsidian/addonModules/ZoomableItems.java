package io.github.vampirestudios.obsidian.addonModules;

import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.ennuil.libzoomer.api.ZoomRegistry;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.ZoomableItem;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.SpyglassItemImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.item.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class ZoomableItems implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        ZoomableItem zoomableItem = Obsidian.GSON.fromJson(new FileReader(file), ZoomableItem.class);
        try {
            if (zoomableItem == null) return;
            ZoomInstance zoomInstance = ZoomRegistry.registerInstance(new ZoomInstance(
                    zoomableItem.information.name.id, zoomableItem.zoomInformation.zoom_length,
                    zoomableItem.zoomInformation.getTransitionMode(), zoomableItem.zoomInformation.getMouseModifier(),
                    zoomableItem.zoomInformation.getZoomOverlay()
            ));
            Item item = RegistryUtils.registerItem(new SpyglassItemImpl(zoomableItem, new Item.Settings().group(zoomableItem.information.getItemGroup())
                    .maxCount(1)), zoomableItem.information.name.id);
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                // This is how you get a spyglass-like zoom working
                if (client.player == null) return;
                zoomInstance.setZoom(client.options.getPerspective().isFirstPerson() && (client.player.isUsingItem() &&
                        client.player.getActiveItem().isOf(item)));
            });
            register(ZOOMABLE_ITEMS, "zoomable_item", zoomableItem.information.name.id, zoomableItem);
        } catch (Exception e) {
            failedRegistering("zoomable_item", zoomableItem.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/zoomable_items";
    }
}

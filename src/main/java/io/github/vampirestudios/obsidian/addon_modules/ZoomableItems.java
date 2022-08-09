package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.ZoomableItem;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.SpyglassItemImpl;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import net.minecraft.item.Item;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class ZoomableItems implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        ZoomableItem zoomableItem = Obsidian.GSON.fromJson(new FileReader(file), ZoomableItem.class);
        try {
            if (zoomableItem == null) return;
            ZoomInstance zoomInstance = new ZoomInstance(
                    zoomableItem.information.name.id, zoomableItem.zoomInformation.zoom_length,
                    zoomableItem.zoomInformation.getTransitionMode(), zoomableItem.zoomInformation.getMouseModifier(),
                    zoomableItem.zoomInformation.getZoomOverlay()
            );
            Item item = RegistryUtils.registerItem(new SpyglassItemImpl(zoomableItem, new Item.Settings().group(zoomableItem.information.getItemGroup())
                    .maxCount(1)), zoomableItem.information.name.id);
            ClientTickEvents.END.register(client -> {
                // This is how you get a spyglass-like zoom working
                if (client.player == null) return;
                zoomInstance.setZoom(client.options.getPerspective().isFirstPerson() && (client.player.isUsingItem() &&
                        client.player.getActiveItem().isOf(item)));
            });
            register(ContentRegistries.ZOOMABLE_ITEMS, "zoomable_item", zoomableItem.information.name.id, zoomableItem);
        } catch (Exception e) {
            failedRegistering("zoomable_item", zoomableItem.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/zoomable_items";
    }
}

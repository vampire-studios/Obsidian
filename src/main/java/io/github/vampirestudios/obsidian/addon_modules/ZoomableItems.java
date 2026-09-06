/*
package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.item.ZoomableItem;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;

public class ZoomableItems implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
        ZoomableItem zoomableItem = AddonFormats.read(addon, file, ZoomableItem.class);
        try {
            if (zoomableItem == null) return;

			Identifier identifier = Objects.requireNonNullElseGet(
                    zoomableItem.information.name.id,
                    () -> Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file))
            );
            if (zoomableItem.information.name.id == null) zoomableItem.information.name.id = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));

			RegistryHelperItemExpanded expanded = new RegistryHelperItemExpanded(id.modId());

//            ZoomInstance zoomInstance = new ZoomInstance(
//                    identifier, zoomableItem.zoomInformation.zoom_length,
//                    zoomableItem.zoomInformation.getTransitionMode(), zoomableItem.zoomInformation.getMouseModifier(),
//                    zoomableItem.zoomInformation.getZoomOverlay()
//            );
//            Item item = expanded.registerItem(identifier.getPath(), new SpyglassItemImpl(zoomableItem, new Item.Properties().stacksTo(1)));
//            ClientTickEvents.END_CLIENT_TICK.register(client -> {
//                // This is how you get a spyglass-like zoom working
//                if (client.player == null) return;
//                zoomInstance.setZooming(client.options.getCameraType().isFirstPerson() && (client.player.isUsingItem() &&
//                        client.player.getUseItem().is(item)));
//            });
//            register(ContentRegistries.ZOOMABLE_ITEMS, "zoomable_item", identifier, zoomableItem);
        } catch (Exception e) {
            failedRegistering("zoomable_item", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "item/zoomable_item";
    }
}
*/

/*
package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.ShieldItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ShieldItemImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.Utils;
import io.github.vampirestudios.vampirelib.api.ShieldRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.item.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class Shields implements AddonModule {
    @Override
    public void init(File file, ModIdAndAddonPath id) throws FileNotFoundException {
        ShieldItem shieldItem = Obsidian.GSON.fromJson(new FileReader(file), ShieldItem.class);
        try {
            if(shieldItem == null) return;
            ShieldItemImpl shieldItemImpl = new ShieldItemImpl(shieldItem, new Item.Settings().group(shieldItem.information.getItemGroup()));
            REGISTRY_HELPER.registerItem(shieldItemImpl, shieldItem.information.name.id.getPath());
            ShieldRegistry.INSTANCE.add(shieldItemImpl);
            FabricModelPredicateProviderRegistry.register(shieldItemImpl, Utils.appendToPath(shieldItem.information.name.id, "_blocking"), (stack, world, entity, seed) ->
                    entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
            register(SHIELDS, "shield", shieldItem.information.name.id.toString(), shieldItem);
        } catch (Exception e) {
            failedRegistering("shield", shieldItem.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/shields";
    }
}
*/

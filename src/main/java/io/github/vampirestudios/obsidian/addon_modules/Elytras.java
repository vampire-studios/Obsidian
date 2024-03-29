package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.Elytra;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ElytraItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Elytras implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Elytra item = Obsidian.GSON.fromJson(new FileReader(file), Elytra.class);
        try {
            if (item == null) return;
            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    item.information.name.id,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (item.information.name.id == null) item.information.name.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
            Item registeredItem = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new ElytraItemImpl(item, new Item.Properties().stacksTo(1)));
            ItemGroupEvents.modifyEntriesEvent(item.information.getItemSettings().getItemGroup()).register(entries -> entries.accept(registeredItem));
            register(ContentRegistries.ELYTRAS, "elytra", identifier, item);
        } catch (Exception e) {
            failedRegistering("elytra", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "items/elytra";
    }
}

package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.FuelSource;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class FuelSources implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        FuelSource fuelSource = Obsidian.GSON.fromJson(new FileReader(file), FuelSource.class);
        JsonObject fuelSourceJsonObject = Obsidian.GSON.fromJson(new FileReader(file), JsonObject.class);
        try {
            if (fuelSource == null) return;
            if (fuelSourceJsonObject.entrySet().isEmpty()) {
                String fileName = file.getName();
                if (fileName.contains("remove_")) fileName.replace("remove_", "");
                if (fileName.contains("item")) {
                    fileName.replace("_item.json", "");
                    FuelRegistry.INSTANCE.remove(Registry.ITEM.get(Identifier.tryParse(fileName)));
                } else {
                    fileName.replace("_tag.json", "");
                    FuelRegistry.INSTANCE.remove(TagKey.of(Registry.ITEM_KEY, Identifier.tryParse(fileName)));
                }
            } else {
                if (fuelSource.burn_time > 0) {
                    if (fuelSource.item != null) {
                        FuelRegistry.INSTANCE.add(Registry.ITEM.get(fuelSource.item), fuelSource.burn_time);
                    } else {
                        FuelRegistry.INSTANCE.add(fuelSource.getTag(), fuelSource.burn_time);
                    }
                } else {
                    if (fuelSource.item != null) {
                        FuelRegistry.INSTANCE.remove(Registry.ITEM.get(fuelSource.item));
                    } else {
                        FuelRegistry.INSTANCE.remove(fuelSource.getTag());
                    }
                }
            }
            register(ContentRegistries.FUEL_SOURCES, "fuel_source", new Identifier(id.modId(), file.getName().replace(".json", "")), fuelSource);
        } catch (Exception e) {
            failedRegistering("fuel_source", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "fuel_sources";
    }

}

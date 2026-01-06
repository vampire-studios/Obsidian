package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.FuelSource;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class FuelSources implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        FuelSource fuelSource = BaseGson.GSON.fromJson(new FileReader(file), FuelSource.class);
        JsonObject fuelSourceJsonObject = BaseGson.GSON.fromJson(new FileReader(file), JsonObject.class);
        try {
            if (fuelSource == null) return;
            if (fuelSourceJsonObject.entrySet().isEmpty()) {
                String fileName = file.getName();
                if (fileName.contains("remove_")) fileName.replace("remove_", "");
                /*if (fileName.contains("item")) {
                    fileName.replace("_item.json", "");
                    FuelRegistryEvents.BUILD.register((builder, _) -> builder.remove(BuiltInRegistries.ITEM.getValue(Identifier.parse(fileName))));
                } else {
                    fileName.replace("_tag.json", "");
                    FuelRegistryEvents.BUILD.register((builder, _) -> builder.remove(TagKey.create(Registries.ITEM, Identifier.parse(fileName))));
                }*/
            } else {
                if (fuelSource.burn_time > 0) {
                    if (fuelSource.item != null) {
                        FuelRegistryEvents.EXCLUSIONS.register((builder, _) -> builder.add(BuiltInRegistries.ITEM.getValue(fuelSource.item), fuelSource.burn_time));
                    } else {
                        FuelRegistryEvents.EXCLUSIONS.register((builder, _) -> builder.add(fuelSource.getTag(), fuelSource.burn_time));
                    }
                } else {
                    /*if (fuelSource.item != null) {
                        FuelRegistryEvents.BUILD.register((builder, _) -> builder.remove(BuiltInRegistries.ITEM.getValue(fuelSource.item)));
                        FuelRegistry.INSTANCE.remove(BuiltInRegistries.ITEM.get(fuelSource.item));
                    } else {
                        FuelRegistry.INSTANCE.remove(fuelSource.getTag());
                    }*/
                    FuelRegistryEvents.BUILD.register((builder, _) -> builder.remove(fuelSource.getTag()));
                }
            }
            register(ContentRegistries.FUEL_SOURCES, "fuel_source", Identifier.fromNamespaceAndPath(id.modId(), file.getName().replace(".json", "")), fuelSource);
        } catch (Exception e) {
            failedRegistering("fuel_source", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "fuel_source";
    }

}

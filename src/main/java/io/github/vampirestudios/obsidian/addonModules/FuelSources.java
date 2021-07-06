package io.github.vampirestudios.obsidian.addonModules;

import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.FuelSource;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class FuelSources implements AddonModule {

    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
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
                    FuelRegistry.INSTANCE.remove(TagRegistry.item(Identifier.tryParse(fileName)));
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
            register(FUEL_SOURCES, "fuel_source", new Identifier(id.getModId(), file.getName().replace(".json", "")), fuelSource);
        } catch (Exception e) {
            failedRegistering("fuel_source", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "fuel_sources";
    }

}

package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Fluids implements AddonModule {

    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Fluid foodItem = Obsidian.GSON.fromJson(new FileReader(file), Fluid.class);
        try {
            if (foodItem == null) return;
            register(FLUIDS, "fluid", foodItem.name.id, foodItem);
        } catch (Exception e) {
            failedRegistering("fluid", foodItem.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "fluids";
    }
}

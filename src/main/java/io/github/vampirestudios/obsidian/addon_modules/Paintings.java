package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.Painting;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Paintings implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Painting painting = Obsidian.GSON.fromJson(new FileReader(file), Painting.class);
        try {
            if (painting == null) return;
            Registry.register(Registry.PAINTING_VARIANT, painting.name, new PaintingVariant(painting.width, painting.height));
            register(PAINTINGS, "painting", painting.name, painting);
        } catch (Exception e) {
            failedRegistering("painting", painting.name.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "paintings";
    }
}
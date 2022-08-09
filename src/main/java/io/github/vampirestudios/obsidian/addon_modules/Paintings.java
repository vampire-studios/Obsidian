package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.Painting;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Paintings implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Painting painting = Obsidian.GSON.fromJson(new FileReader(file), Painting.class);
        try {
            if (painting == null) return;
            Registry.register(Registry.PAINTING_VARIANT, painting.name, new PaintingVariant(painting.width, painting.height));
            register(ContentRegistries.PAINTINGS, "painting", painting.name, painting);
        } catch (Exception e) {
            failedRegistering("painting", painting.name.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "paintings";
    }
}

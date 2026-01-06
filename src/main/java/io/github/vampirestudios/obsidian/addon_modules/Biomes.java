package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.world.Biome;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Biomes implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Biome biome = BaseGson.GSON.fromJson(new FileReader(file), Biome.class);
        try {
            if (biome == null) return;

            Identifier identifier = Objects.requireNonNullElseGet(
                    biome.id,
                    () -> Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (biome.id == null) biome.id = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));

            register(ContentRegistries.BIOMES, "biome", identifier, biome);
        } catch (Exception e) {
            failedRegistering("biome", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "world/biome";
    }
}

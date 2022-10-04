package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.villager.VillagerBiomeType;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerTypeHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class VillagerBiomeTypes implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        VillagerBiomeType villagerBiomeType = Obsidian.GSON.fromJson(new FileReader(file), VillagerBiomeType.class);
        try {
            if (villagerBiomeType == null) return;
            Identifier identifier = Objects.requireNonNullElseGet(
                    villagerBiomeType.name.id,
                    () -> new Identifier(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (villagerBiomeType.name.id == null) villagerBiomeType.name.id = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));
            VillagerType villagerType = VillagerTypeHelper.register(identifier);
            villagerBiomeType.getBiomes().forEach(biome -> {
                RegistryKey<Biome> registryKey = BuiltinRegistries.BIOME.getKey(biome).get();
                VillagerTypeHelper.addVillagerTypeToBiome(registryKey, villagerType);
            });
            register(ContentRegistries.VILLAGER_BIOME_TYPES, "villager_biome_type", identifier, villagerBiomeType);
        } catch (Exception e) {
            failedRegistering("villager_biome_type", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "villagers/biome_types";
    }
}

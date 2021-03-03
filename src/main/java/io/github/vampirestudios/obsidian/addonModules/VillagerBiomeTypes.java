package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.villager.VillagerBiomeType;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerTypeHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class VillagerBiomeTypes implements AddonModule {
	@Override
	public void init(File file, ModIdAndAddonPath id) throws FileNotFoundException {
		VillagerBiomeType villagerBiomeType = Obsidian.GSON.fromJson(new FileReader(file), VillagerBiomeType.class);
		try {
			if(villagerBiomeType == null) return;
			VillagerType villagerType = VillagerTypeHelper.register(villagerBiomeType.name.id);
			villagerBiomeType.getBiomes().forEach(biome -> {
				RegistryKey<Biome> registryKey = BuiltinRegistries.BIOME.getKey(biome).get();
				VillagerTypeHelper.addVillagerTypeToBiome(registryKey, villagerType);
			});
;			register(VILLAGER_BIOME_TYPES, "villager_biome_type", villagerBiomeType.name.id.toString(), villagerBiomeType);
		} catch (Exception e) {
			failedRegistering("villager_biome_type", villagerBiomeType.name.id.toString(), e);
		}
	}

	@Override
	public String getType() {
		return "items/elytra";
	}
}

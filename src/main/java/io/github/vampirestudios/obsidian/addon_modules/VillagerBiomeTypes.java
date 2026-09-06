package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.villager.VillagerBiomeType;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.villager.AddonVillagerTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerType;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class VillagerBiomeTypes implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		VillagerBiomeType villagerBiomeType = AddonFormats.read(addon, file, VillagerBiomeType.class);
		try {
			if (villagerBiomeType == null) return;
			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			ResourceKey<VillagerType> typeKey = ResourceKey.create(Registries.VILLAGER_TYPE, identifier);

			// VillagerType carries no data of its own — it is an identity the villager stores and the
			// renderer turns into textures/entity/villager/type/<path>.png
			if (!BuiltInRegistries.VILLAGER_TYPE.containsKey(identifier)) {
				Registry.register(BuiltInRegistries.VILLAGER_TYPE, typeKey, new VillagerType());
			}

			if (villagerBiomeType.biomes != null) {
				for (Identifier biome : villagerBiomeType.biomes) {
					if (biome == null) continue;
					AddonVillagerTypes.bind(ResourceKey.create(Registries.BIOME, biome), typeKey);
				}
			}

			register(ContentRegistries.VILLAGER_BIOME_TYPES, "villager_biome_type", identifier, villagerBiomeType);
		} catch (Exception e) {
			failedRegistering("villager_biome_type", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "villager/biome_type";
	}
}

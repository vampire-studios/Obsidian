package io.github.vampirestudios.obsidian.api.obsidian.villager;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class VillagerBiomeType {

	public NameInformation name;
	public List<Identifier> biomes;

	public List<Biome> getBiomes() {
		List<Biome> biomes2 = new ArrayList<>();
		biomes.forEach(identifier -> biomes2.add(BuiltinRegistries.BIOME.get(identifier)));
		return biomes2;
	}

}

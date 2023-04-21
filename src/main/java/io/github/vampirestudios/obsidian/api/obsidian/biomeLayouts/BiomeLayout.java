package io.github.vampirestudios.obsidian.api.obsidian.biomeLayouts;

import net.minecraft.resources.ResourceLocation;

public class BiomeLayout {

	public ResourceLocation regionName;
	public BiomeInformation[] biomes;
	public DimensionType dimensionType = DimensionType.OVERWORLD;

	public static class BiomeInformation {
		public ResourceLocation name;
		public BiomeSpawnType type;
		public ResourceLocation similarBiomeName;
		public MultiNoise multiNoise;

		public enum BiomeSpawnType {
			SIMILAR,
			MULTI_NOISE
		}
	}

	public enum DimensionType {
		OVERWORLD,
		NETHER
	}

}

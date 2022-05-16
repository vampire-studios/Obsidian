package io.github.vampirestudios.obsidian.api.obsidian.biomeLayouts;

import net.minecraft.util.Identifier;

public class BiomeLayout {

	public Identifier regionName;
	public BiomeInformation[] biomes;
	public DimensionType dimensionType = DimensionType.OVERWORLD;

	public static class BiomeInformation {
		public Identifier name;
		public BiomeSpawnType type;
		public Identifier similarBiomeName;
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

package io.github.vampirestudios.obsidian.init;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.utils.ExpandedTagFactory;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class ObsidianTags {

	public static class Biomes {

		public static final Tag<Biome> ANIMAL = tag("animal");
		public static final Tag<Biome> BAMBOO = tag("bamboo");
		public static final Tag<Biome> BASALT_DELTAS = tag("basalt_deltas");
		public static final Tag<Biome> BEACH = tag("beach");
		public static final Tag<Biome> BEE_HABITAT = tag("bee_habitat");
		public static final Tag<Biome> BIRCH = tag("birch");
		public static final Tag<Biome> COLD = tag("cold");
		public static final Tag<Biome> CRIMSON_FOREST = tag("crimson_forest");
		public static final Tag<Biome> DEEP = tag("deep");
		public static final Tag<Biome> DESERT = tag("desert");
		public static final Tag<Biome> EDGE = tag("edge");
		public static final Tag<Biome> EXTREME_HILLS = tag("extreme_hills");

		private static Tag<Biome> tag(String name) {
			return ExpandedTagFactory.BIOME_EXPANDED.create(new Identifier(Obsidian.MOD_ID, name));
		}
	}

}

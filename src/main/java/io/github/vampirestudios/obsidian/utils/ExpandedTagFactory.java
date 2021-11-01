package io.github.vampirestudios.obsidian.utils;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

/**
 * A factory for accessing datapack tags.
 */
public interface ExpandedTagFactory<T> extends TagFactory<T> {
	TagFactory<Biome> BIOME_EXPANDED = TagFactory.of(Registry.BIOME_KEY, "tags/worldgen/biomes");
	TagFactory<DimensionType> DIMENSION_TYPE = TagFactory.of(Registry.DIMENSION_TYPE_KEY, "tags/dimension_types");
	TagFactory<ChunkGeneratorSettings> NOISE_SETTINGS = TagFactory.of(Registry.CHUNK_GENERATOR_SETTINGS_KEY, "tags/worldgen/noise_settings");
	TagFactory<DimensionOptions> DIMENSIONS = TagFactory.of(Registry.DIMENSION_KEY, "tags/dimensions");
}
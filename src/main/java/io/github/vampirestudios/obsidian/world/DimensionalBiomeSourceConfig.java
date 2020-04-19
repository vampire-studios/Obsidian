package io.github.vampirestudios.obsidian.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSourceConfig;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;

import java.util.Set;

public class DimensionalBiomeSourceConfig implements BiomeSourceConfig {
    private final long seed;
    private final LevelGeneratorType generatorType;
    private OverworldChunkGeneratorConfig generatorSettings = new OverworldChunkGeneratorConfig();
    private Set<Biome> biomes;

    public DimensionalBiomeSourceConfig(LevelProperties levelProperties_1) {
        this.seed = levelProperties_1.getSeed();
        this.generatorType = levelProperties_1.getGeneratorType();
    }

    public DimensionalBiomeSourceConfig(Object o) {
        this.seed = ((LevelProperties) o).getSeed();
        this.generatorType = ((LevelProperties) o).getGeneratorType();
    }

    public long getSeed() {
        return this.seed;
    }

    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
    }

    public OverworldChunkGeneratorConfig getGeneratorSettings() {
        return this.generatorSettings;
    }

    public DimensionalBiomeSourceConfig setGeneratorSettings(OverworldChunkGeneratorConfig overworldChunkGeneratorConfig_1) {
        this.generatorSettings = overworldChunkGeneratorConfig_1;
        return this;
    }

    public Set<Biome> getBiomes() {
        return biomes;
    }

    public DimensionalBiomeSourceConfig setBiomes(Set<Biome> biomes) {
        this.biomes = biomes;
        return this;
    }
}

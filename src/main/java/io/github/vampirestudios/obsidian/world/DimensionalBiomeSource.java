package io.github.vampirestudios.obsidian.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DimensionalBiomeSource extends BiomeSource {
    private static Set<Biome> BIOMES;
    private final BiomeLayerSampler noiseLayer;

    public DimensionalBiomeSource(Object o) {
        super(((DimensionalBiomeSourceConfig) o).getBiomes());
        this.noiseLayer = DimensionalBiomeLayers.build(((DimensionalBiomeSourceConfig) o).getSeed(), ((DimensionalBiomeSourceConfig) o).getBiomes());
        BIOMES = ((DimensionalBiomeSourceConfig) o).getBiomes();
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.noiseLayer.sample(biomeX, biomeZ);
    }

    @Override
    public List<Biome> getSpawnBiomes() {
        return new ArrayList<>(BIOMES);
    }

}

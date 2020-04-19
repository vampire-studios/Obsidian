package io.github.vampirestudios.obsidian.minecraft;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;

public class BiomeImpl extends Biome {

    public io.github.vampirestudios.obsidian.api.world.Biome biome;

    public BiomeImpl(io.github.vampirestudios.obsidian.api.world.Biome biome) {
        super(
                new Settings()
                        .configureSurfaceBuilder(biome.getSurfaceBuilder(), biome.getSurfaceConfig())
                        .depth(biome.depth)
                        .downfall(biome.downfall)
                        .parent(biome.parent)
                        .scale(biome.scale)
                        .temperature(biome.temperature)
                        .effects(
                                new BiomeEffects.Builder()
                                        .waterColor(biome.waterColor)
                                        .fogColor(biome.fogColor)
                                        .waterFogColor(biome.waterFogColor)
                                        .build()
                        )
                        .category(Category.PLAINS)
                        .precipitation(Precipitation.NONE)
                        .noises(
                                ImmutableList.of(
                                        new MixedNoisePoint(
                                                biome.biomeNoiseInformation.temperature,
                                                biome.biomeNoiseInformation.humidity,
                                                biome.biomeNoiseInformation.altitude,
                                                biome.biomeNoiseInformation.weirdness,
                                                1.0F
                                        )
                                )
                        )

        );
        this.biome = biome;
    }

    @Override
    public int getFoliageColor() {
        return biome.foliageColor;
    }

    @Override
    public int getGrassColorAt(double x, double z) {
        return biome.grassColor;
    }

}

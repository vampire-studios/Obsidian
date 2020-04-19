package io.github.vampirestudios.obsidian.api.world;

import io.github.vampirestudios.obsidian.PlayerPlacementHandlers;
import net.fabricmc.fabric.api.dimension.v1.EntityPlacer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.*;

import java.util.ArrayList;
import java.util.List;

public class Dimension {

    public Identifier name;
    public boolean canSleep;
    public boolean netherLike;
    public boolean waterVaporize;
    public boolean renderFog;
    public boolean hasSkyLight;
    public boolean hasSky;
//    public HashMap<String, int[]> mobs;
    public DimensionColors colors;
    public String dimensionType;
    public Identifier stoneBlock;
    public Identifier fluid;
    public List<Identifier> biomes = new ArrayList<>();

    public ChunkGenerator<?> getChunkGenerator(World world, BiomeSource biomeSource) {
        OverworldChunkGeneratorConfig config = new OverworldChunkGeneratorConfig();
        config.setDefaultFluid(Registry.BLOCK.get(fluid).getDefaultState());
        config.setDefaultBlock(Registry.BLOCK.get(stoneBlock).getDefaultState());

        CavesChunkGeneratorConfig caveConfig = new CavesChunkGeneratorConfig() { //set the bedrock ceiling y to 256
            @Override
            public int getBedrockCeilingY() {
                return 255;
            }
        };
        caveConfig.setDefaultBlock(Registry.BLOCK.get(stoneBlock).getDefaultState());
        caveConfig.setDefaultFluid(Registry.BLOCK.get(fluid).getDefaultState());

        FloatingIslandsChunkGeneratorConfig floatingConfig = new FloatingIslandsChunkGeneratorConfig();
        floatingConfig.setDefaultBlock(Registry.BLOCK.get(stoneBlock).getDefaultState());
        floatingConfig.setDefaultFluid(Registry.BLOCK.get(fluid).getDefaultState());

        FlatChunkGeneratorConfig flatConfig = new FlatChunkGeneratorConfig();
        flatConfig.setBiome(biomeSource.getBiomeForNoiseGen(0,0, 0));
        flatConfig.setDefaultBlock(Registry.BLOCK.get(stoneBlock).getDefaultState());
        flatConfig.setDefaultFluid(Registry.BLOCK.get(fluid).getDefaultState());

        switch (dimensionType) {
            case "overworld": return ChunkGeneratorType.SURFACE.create(world, biomeSource, config);
            case "nether": return ChunkGeneratorType.CAVES.create(world, biomeSource, caveConfig);
            case "end": return ChunkGeneratorType.FLOATING_ISLANDS.create(world, biomeSource, floatingConfig);
            case "flat": return ChunkGeneratorType.FLAT.create(world, biomeSource, flatConfig);
        }

        return ChunkGeneratorType.SURFACE.create(world, biomeSource, config);
    }

    public EntityPlacer getPlayerPlacer() {
        switch (dimensionType) {
            case "overworld": return PlayerPlacementHandlers.OVERWORLD.getEntityPlacer();
            case "nether": return PlayerPlacementHandlers.CAVE_WORLD.getEntityPlacer();
            case "end": return PlayerPlacementHandlers.FLOATING_WORLD.getEntityPlacer();
            case "flat": return PlayerPlacementHandlers.SURFACE_WORLD.getEntityPlacer();
        }

        return PlayerPlacementHandlers.OVERWORLD.getEntityPlacer();
    }

}
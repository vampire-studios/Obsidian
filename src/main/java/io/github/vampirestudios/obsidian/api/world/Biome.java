package io.github.vampirestudios.obsidian.api.world;

import net.minecraft.util.Identifier;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class Biome {

    public Identifier name;
    public String displayName;
    public NetherBiomeNoise biomeNoiseInformation;
    public String parent = "";
    public String dimension;
    public String category = "plains";
    public String precipitation = "none";
    public Identifier surfaceBuilder;
    public Identifier surfaceConfig;
    public float depth;
    public float scale;
    public float temperature;
    public float downfall;
    public int waterColor;
    public int waterFogColor;
    public int fogColor;
    public int grassColor;
    public int foliageColor;

    public TernarySurfaceConfig getSurfaceConfig() {
        if (surfaceConfig.equals(new Identifier("gravel_config"))) return SurfaceBuilder.GRAVEL_CONFIG;
        if (surfaceConfig.equals(new Identifier("grass_config"))) return SurfaceBuilder.GRASS_CONFIG;
        if (surfaceConfig.equals(new Identifier("dirt_config"))) return SurfaceBuilder.DIRT_CONFIG;
        if (surfaceConfig.equals(new Identifier("stone_config"))) return SurfaceBuilder.STONE_CONFIG;
        if (surfaceConfig.equals(new Identifier("coarse_dirt_config"))) return SurfaceBuilder.COARSE_DIRT_CONFIG;
        if (surfaceConfig.equals(new Identifier("sand_config"))) return SurfaceBuilder.SAND_CONFIG;
        if (surfaceConfig.equals(new Identifier("grass_sand_underwater_config"))) return SurfaceBuilder.GRASS_SAND_UNDERWATER_CONFIG;
        if (surfaceConfig.equals(new Identifier("sand_sand_underwater_config"))) return SurfaceBuilder.SAND_SAND_UNDERWATER_CONFIG;
        if (surfaceConfig.equals(new Identifier("badlands_config"))) return SurfaceBuilder.BADLANDS_CONFIG;
        if (surfaceConfig.equals(new Identifier("mycelium_config"))) return SurfaceBuilder.MYCELIUM_CONFIG;
        if (surfaceConfig.equals(new Identifier("nether_config"))) return SurfaceBuilder.NETHER_CONFIG;
        if (surfaceConfig.equals(new Identifier("soul_sand_config"))) return SurfaceBuilder.SOUL_SAND_CONFIG;
        if (surfaceConfig.equals(new Identifier("end_config"))) return SurfaceBuilder.END_CONFIG;

        return SurfaceBuilder.GRASS_CONFIG;
    }

    public SurfaceBuilder<TernarySurfaceConfig> getSurfaceBuilder() {
        if (surfaceBuilder.equals(new Identifier("default"))) return SurfaceBuilder.DEFAULT;
        if (surfaceBuilder.equals(new Identifier("mountain"))) return SurfaceBuilder.MOUNTAIN;
        if (surfaceBuilder.equals(new Identifier("shattered_savanna"))) return SurfaceBuilder.SHATTERED_SAVANNA;
        if (surfaceBuilder.equals(new Identifier("gravelly_mountain"))) return SurfaceBuilder.GRAVELLY_MOUNTAIN;
        if (surfaceBuilder.equals(new Identifier("giant_tree_taiga"))) return SurfaceBuilder.GIANT_TREE_TAIGA;
        if (surfaceBuilder.equals(new Identifier("swamp"))) return SurfaceBuilder.SWAMP;
        if (surfaceBuilder.equals(new Identifier("badlands"))) return SurfaceBuilder.BADLANDS;
        if (surfaceBuilder.equals(new Identifier("wooded_badlands"))) return SurfaceBuilder.WOODED_BADLANDS;
        if (surfaceBuilder.equals(new Identifier("eroded_badlands"))) return SurfaceBuilder.ERODED_BADLANDS;
        if (surfaceBuilder.equals(new Identifier("frozen_ocean"))) return SurfaceBuilder.FROZEN_OCEAN;
        if (surfaceBuilder.equals(new Identifier("nether"))) return SurfaceBuilder.NETHER;
        if (surfaceBuilder.equals(new Identifier("nether_forest"))) return SurfaceBuilder.NETHER_FOREST;
        if (surfaceBuilder.equals(new Identifier("soul_sand_valley"))) return SurfaceBuilder.SOUL_SAND_VALLEY;
        if (surfaceBuilder.equals(new Identifier("basalt_deltas"))) return SurfaceBuilder.BASALT_DELTAS;
        if (surfaceBuilder.equals(new Identifier("nope"))) return SurfaceBuilder.NOPE;

        return SurfaceBuilder.DEFAULT;
    }

}
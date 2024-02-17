package io.github.vampirestudios.obsidian.api;

import com.google.common.collect.ImmutableMap;
import io.github.vampirestudios.obsidian.addon_modules.ContentUtils;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Map;

public class VanillaWoodTypes {
    public static final Map<ResourceLocation, WoodType> SOUND_TYPES = ImmutableMap.<ResourceLocation, WoodType>builder()
            .put(new ResourceLocation("oak"), WoodType.OAK)
            .put(new ResourceLocation("spruce"), WoodType.SPRUCE)
            .put(new ResourceLocation("birch"), WoodType.BIRCH)
            .put(new ResourceLocation("acacia"), WoodType.ACACIA)
            .put(new ResourceLocation("cherry"), WoodType.CHERRY)
            .put(new ResourceLocation("jungle"), WoodType.JUNGLE)
            .put(new ResourceLocation("dark_oak"), WoodType.DARK_OAK)
            .put(new ResourceLocation("crimson"), WoodType.CRIMSON)
            .put(new ResourceLocation("warped"), WoodType.WARPED)
            .put(new ResourceLocation("mangrove"), WoodType.MANGROVE)
            .put(new ResourceLocation("bamboo"), WoodType.BAMBOO)
            .build();

    public static void init() {
        /* nothing to do */
    }

    public static WoodType get(ResourceLocation woodType) {
        WoodType woodType1 = SOUND_TYPES.get(woodType);
        if (WoodType.values().toList().contains(woodType1)) {
            return woodType1;
        } else if (ContentRegistries.BLOCK_SET_TYPES.containsKey(woodType)) {
            return ContentUtils.getWoodType(woodType);
        } else {
            throw new IllegalStateException("No wood type known with name " + woodType);
        }
    }
}
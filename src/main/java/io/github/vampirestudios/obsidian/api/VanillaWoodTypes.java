package io.github.vampirestudios.obsidian.api;

import com.google.common.collect.ImmutableMap;
import io.github.vampirestudios.obsidian.addon_modules.ContentUtils;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Map;

public class VanillaWoodTypes {
    public static final Map<ResourceLocation, WoodType> SOUND_TYPES = ImmutableMap.<ResourceLocation, WoodType>builder()
            .put(ResourceLocation.withDefaultNamespace("oak"), WoodType.OAK)
            .put(ResourceLocation.withDefaultNamespace("spruce"), WoodType.SPRUCE)
            .put(ResourceLocation.withDefaultNamespace("birch"), WoodType.BIRCH)
            .put(ResourceLocation.withDefaultNamespace("acacia"), WoodType.ACACIA)
            .put(ResourceLocation.withDefaultNamespace("cherry"), WoodType.CHERRY)
            .put(ResourceLocation.withDefaultNamespace("jungle"), WoodType.JUNGLE)
            .put(ResourceLocation.withDefaultNamespace("dark_oak"), WoodType.DARK_OAK)
            .put(ResourceLocation.withDefaultNamespace("crimson"), WoodType.CRIMSON)
            .put(ResourceLocation.withDefaultNamespace("warped"), WoodType.WARPED)
            .put(ResourceLocation.withDefaultNamespace("mangrove"), WoodType.MANGROVE)
            .put(ResourceLocation.withDefaultNamespace("bamboo"), WoodType.BAMBOO)
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
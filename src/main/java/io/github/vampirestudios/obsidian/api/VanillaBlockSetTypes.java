package io.github.vampirestudios.obsidian.api;

import com.google.common.collect.ImmutableMap;
import io.github.vampirestudios.obsidian.addon_modules.ContentUtils;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.Map;

public class VanillaBlockSetTypes {
    public static final Map<ResourceLocation, BlockSetType> SOUND_TYPES = ImmutableMap.<ResourceLocation, BlockSetType>builder()
            .put(new ResourceLocation("iron"), BlockSetType.IRON)
            .put(new ResourceLocation("copper"), BlockSetType.COPPER)
            .put(new ResourceLocation("gold"), BlockSetType.GOLD)
            .put(new ResourceLocation("stone"), BlockSetType.STONE)
            .put(new ResourceLocation("polished_blackstone"), BlockSetType.POLISHED_BLACKSTONE)
            .put(new ResourceLocation("oak"), BlockSetType.OAK)
            .put(new ResourceLocation("spruce"), BlockSetType.SPRUCE)
            .put(new ResourceLocation("birch"), BlockSetType.BIRCH)
            .put(new ResourceLocation("acacia"), BlockSetType.ACACIA)
            .put(new ResourceLocation("cherry"), BlockSetType.CHERRY)
            .put(new ResourceLocation("jungle"), BlockSetType.JUNGLE)
            .put(new ResourceLocation("dark_oak"), BlockSetType.DARK_OAK)
            .put(new ResourceLocation("crimson"), BlockSetType.CRIMSON)
            .put(new ResourceLocation("warped"), BlockSetType.WARPED)
            .put(new ResourceLocation("mangrove"), BlockSetType.MANGROVE)
            .put(new ResourceLocation("bamboo"), BlockSetType.BAMBOO)
            .build();

    public static void init() {
        /* nothing to do */
    }

    public static BlockSetType get(ResourceLocation blockSetType) {
        BlockSetType setType = SOUND_TYPES.get(blockSetType);
        if (BlockSetType.values().toList().contains(setType)) {
            return setType;
        } else if (ContentRegistries.BLOCK_SET_TYPES.containsKey(blockSetType)) {
            return ContentUtils.getBlockSetType(blockSetType);
        } else {
            throw new IllegalStateException("No block set type known with name " + blockSetType);
        }
    }
}
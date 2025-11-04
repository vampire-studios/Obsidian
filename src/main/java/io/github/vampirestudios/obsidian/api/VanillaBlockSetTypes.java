package io.github.vampirestudios.obsidian.api;

import com.google.common.collect.ImmutableMap;
import io.github.vampirestudios.obsidian.addon_modules.ContentUtils;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.Map;

public class VanillaBlockSetTypes {
    public static final Map<ResourceLocation, BlockSetType> SOUND_TYPES = ImmutableMap.<ResourceLocation, BlockSetType>builder()
            .put(ResourceLocation.withDefaultNamespace("iron"), BlockSetType.IRON)
            .put(ResourceLocation.withDefaultNamespace("copper"), BlockSetType.COPPER)
            .put(ResourceLocation.withDefaultNamespace("gold"), BlockSetType.GOLD)
            .put(ResourceLocation.withDefaultNamespace("stone"), BlockSetType.STONE)
            .put(ResourceLocation.withDefaultNamespace("polished_blackstone"), BlockSetType.POLISHED_BLACKSTONE)
            .put(ResourceLocation.withDefaultNamespace("oak"), BlockSetType.OAK)
            .put(ResourceLocation.withDefaultNamespace("spruce"), BlockSetType.SPRUCE)
            .put(ResourceLocation.withDefaultNamespace("birch"), BlockSetType.BIRCH)
            .put(ResourceLocation.withDefaultNamespace("acacia"), BlockSetType.ACACIA)
            .put(ResourceLocation.withDefaultNamespace("cherry"), BlockSetType.CHERRY)
            .put(ResourceLocation.withDefaultNamespace("jungle"), BlockSetType.JUNGLE)
            .put(ResourceLocation.withDefaultNamespace("dark_oak"), BlockSetType.DARK_OAK)
            .put(ResourceLocation.withDefaultNamespace("crimson"), BlockSetType.CRIMSON)
            .put(ResourceLocation.withDefaultNamespace("warped"), BlockSetType.WARPED)
            .put(ResourceLocation.withDefaultNamespace("mangrove"), BlockSetType.MANGROVE)
            .put(ResourceLocation.withDefaultNamespace("bamboo"), BlockSetType.BAMBOO)
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
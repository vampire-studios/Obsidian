package io.github.vampirestudios.obsidian.api;

import com.google.common.collect.ImmutableMap;
import io.github.vampirestudios.obsidian.addon_modules.ContentUtils;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.Map;

public class VanillaBlockSetTypes {
    public static final Map<Identifier, BlockSetType> SOUND_TYPES = ImmutableMap.<Identifier, BlockSetType>builder()
            .put(Identifier.withDefaultNamespace("iron"), BlockSetType.IRON)
            .put(Identifier.withDefaultNamespace("copper"), BlockSetType.COPPER)
            .put(Identifier.withDefaultNamespace("gold"), BlockSetType.GOLD)
            .put(Identifier.withDefaultNamespace("stone"), BlockSetType.STONE)
            .put(Identifier.withDefaultNamespace("polished_blackstone"), BlockSetType.POLISHED_BLACKSTONE)
            .put(Identifier.withDefaultNamespace("oak"), BlockSetType.OAK)
            .put(Identifier.withDefaultNamespace("spruce"), BlockSetType.SPRUCE)
            .put(Identifier.withDefaultNamespace("birch"), BlockSetType.BIRCH)
            .put(Identifier.withDefaultNamespace("acacia"), BlockSetType.ACACIA)
            .put(Identifier.withDefaultNamespace("cherry"), BlockSetType.CHERRY)
            .put(Identifier.withDefaultNamespace("jungle"), BlockSetType.JUNGLE)
            .put(Identifier.withDefaultNamespace("dark_oak"), BlockSetType.DARK_OAK)
            .put(Identifier.withDefaultNamespace("crimson"), BlockSetType.CRIMSON)
            .put(Identifier.withDefaultNamespace("warped"), BlockSetType.WARPED)
            .put(Identifier.withDefaultNamespace("mangrove"), BlockSetType.MANGROVE)
            .put(Identifier.withDefaultNamespace("bamboo"), BlockSetType.BAMBOO)
            .build();

    public static void init() {
        /* nothing to do */
    }

    public static BlockSetType get(Identifier blockSetType) {
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
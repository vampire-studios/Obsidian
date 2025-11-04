package io.github.vampirestudios.obsidian;

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

/**
 * Provides methods for registering convertible blocks.
 */
public final class ConvertibleBlocksRegistry {
	private ConvertibleBlocksRegistry() {
	}

	/**
	 * Registers a link between two different blocks.
	 * (See for example {@link OxidizableBlocksRegistry#registerOxidizableBlockPair(Block, Block)} and
	 * {@link OxidizableBlocksRegistry#registerWaxableBlockPair(Block, Block)})
	 *
	 * @param convertibleBlockPair the convertible block pair
	 */
	public static void registerConvertibleBlockPair(ConvertibleBlockPair convertibleBlockPair) {
		Objects.requireNonNull(convertibleBlockPair, "ConvertibleBlockPair cannot be null!");
		Obsidian.CONVERTIBLE_BLOCKS.add(convertibleBlockPair);
	}
}

package io.github.vampirestudios.obsidian.api;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import org.jspecify.annotations.Nullable;

/**
 * A block that stores items, declared through {@code behaviour.container}.
 *
 * <p>Only the block classes built for it implement this — a block that declares no container is
 * registered as an ordinary block and never gains a block entity.
 */
public interface IContainerProvider {

	/** The container declaration this block was registered from. Never null on a registered container block. */
	Block.Behaviour.@Nullable Container getContainer();

	/** The definition the container belongs to, for its name and events. */
	Block getDefinition();
}

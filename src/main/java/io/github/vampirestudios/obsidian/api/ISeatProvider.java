package io.github.vampirestudios.obsidian.api;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * A block whose occupants are carried by Obsidian's invisible seat entities.
 *
 * <p>The block is the source of truth. Seat entities only keep an anchor and list index, then ask
 * the provider for the current declaration when the block rotates, reloads, or is removed.
 */
public interface ISeatProvider {

	/** The seats currently exposed by this state. An empty list means the block is not usable as a seat. */
	List<Block.Behaviour.Seat> getSeats(BlockState state);

	/**
	 * Resolves multi-block furniture to the one block position which owns its seats. A normal block
	 * owns itself; a bed foot, for example, redirects to its head.
	 */
	default BlockPos getSeatAnchor(Level level, BlockPos pos, BlockState state) {
		return pos;
	}

	/** Additional multi-block survival rule evaluated at the resolved anchor. */
	default boolean isSeatAnchorValid(Level level, BlockPos anchor, BlockState state) {
		return true;
	}

	/** Blocks belonging to the furniture itself are excluded from passenger-space collision checks. */
	default boolean isSeatPart(Level level, BlockPos anchor, BlockPos candidate, BlockState candidateState) {
		return anchor.equals(candidate);
	}
}

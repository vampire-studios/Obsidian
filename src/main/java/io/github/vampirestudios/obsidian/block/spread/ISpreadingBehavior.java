package io.github.vampirestudios.obsidian.block.spread;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface ISpreadingBehavior {
	/**
	 * Used for {@link IForgeSpreadingBlock}, which allows extending spread behavior.
	 *
	 * @param state previous state at this position
	 * @return new state to place at the location
	 */
	BlockState getSpreadingState(BlockState state, Level level, BlockPos pos);
}
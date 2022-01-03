package io.github.vampirestudios.obsidian;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface ISpreadingBehavior {
	/**
	 * Used for {@link IForgeSpreadingBlock}, which allows extending spread behavior.
	 *
	 * @param state previous state at this position
	 * @return new state to place at the location
	 */
	BlockState getSpreadingState(BlockState state, World level, BlockPos pos);
}
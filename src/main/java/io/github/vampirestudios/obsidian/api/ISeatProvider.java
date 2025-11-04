package io.github.vampirestudios.obsidian.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ISeatProvider {
	BlockState getState();

	Level getLevel();

	BlockPos getPos();

	float getSeatHeight(BlockState state);

	double getOffsetFor(EntityType<?> type);

	void markOccupied(boolean occ);

	boolean isOccupied(BlockState state);
}
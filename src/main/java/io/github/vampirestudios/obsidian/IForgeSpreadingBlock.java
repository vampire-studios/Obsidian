package io.github.vampirestudios.obsidian;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;

public interface IForgeSpreadingBlock {
	SpreaderType getSpreadingType(BlockState state);

	default void spread(BlockState state, ServerWorld level, BlockPos pos, RandomGenerator random, int tries, int range) {
		if (!level.isRegionLoaded(pos.add(-(range + 1), -(range + 1), -(range + 1)), pos.add(range + 1, range + 1, range + 1)))
			return;
		range = (range * 2) + 1;
		for (int i = 0; i < tries; ++i) {
			BlockPos blockpos = pos.add(random.nextInt(range) - 1, random.nextInt(5) - 3, random.nextInt(range) - 1);
			BlockState targetState = level.getBlockState(blockpos);
			if (SpreadBehaviors.canSpread(targetState, getSpreadingType(state))) {
				level.setBlockState(blockpos, SpreadBehaviors.getSpreadState(targetState, level, pos, getSpreadingType(state)));
			}
		}
	}
}
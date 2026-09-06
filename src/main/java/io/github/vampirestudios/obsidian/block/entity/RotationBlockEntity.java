package io.github.vampirestudios.obsidian.block.entity;

import io.github.vampirestudios.obsidian.registry.OBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RotationBlockEntity extends BlockEntity {

	public RotationBlockEntity(BlockPos pos, BlockState blockState) {
		super(OBE.ROTATION_BLOCK, pos, blockState);
	}
}

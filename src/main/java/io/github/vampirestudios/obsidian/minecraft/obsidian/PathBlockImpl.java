package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PathBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PathBlockImpl extends PathBlock {

	public Block block;

	public PathBlockImpl(net.minecraft.world.level.block.Block baseBlock, Properties settings, Block block) {
		super(baseBlock, settings);
		this.block = block;
	}

	public PathBlockImpl(Properties settings, Block block) {
		super(Blocks.DIRT, settings);
		this.block = block;
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		return block.information.getBlockSettings() != null ? !block.information.getBlockSettings().translucent ? 0.2F : 1.0F : super.getShadeBrightness(state, world, pos);
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
		return block.information.getBlockSettings() != null ? !block.information.getBlockSettings().translucent : super.isCollisionShapeFullBlock(state, world, pos);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state) {
		return block.information.getBlockSettings() != null ? block.information.getBlockSettings().translucent : super.propagatesSkylightDown(state);
	}

}
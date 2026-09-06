package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CandleBlockImpl extends CandleBlock {

	public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

	public CandleBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
		super(settings);
		this.block = block;
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		return block.information.getBlockSettings() != null ? !block.information.getBlockSettings().translucent ? 0.2F : 1.0F : super.getShadeBrightness(state, world, pos);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state) {
		return block.information.getBlockSettings() != null ? block.information.getBlockSettings().translucent : super.propagatesSkylightDown(state);
	}
}

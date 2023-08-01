package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PlantBlockImpl extends BushBlock {

    private final Block block;

    public PlantBlockImpl(Block block, Properties settings) {
        super(settings.noCollission().instabreak());
        this.block = block;
    }

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		return block.information.blockProperties != null ? !block.information.blockProperties.translucent ? 0.2F : 1.0F : super.getShadeBrightness(state, world, pos);
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
		return block.information.blockProperties != null ? !block.information.blockProperties.translucent : super.isCollisionShapeFullBlock(state, world, pos);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
		return block.information.blockProperties != null ? block.information.blockProperties.translucent : super.propagatesSkylightDown(state, world, pos);
	}

	@Override
	protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
		/*for(net.minecraft.block.Block block1 : block.getSupportableBlocks()) {
			return floor.isOf(block1);
		}
		return false;*/
		return !floor.isAir() && !floor.isSolidRender(world, pos);
	}
}

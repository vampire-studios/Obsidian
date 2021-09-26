package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;

public class HorizontalFacingPlantBlockImpl extends PlantBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private final Block block;

    public HorizontalFacingPlantBlockImpl(Block block, Settings settings) {
        super(settings);
        this.block = block;
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

	/*@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		for(net.minecraft.block.Block block1 : block.getSupportableBlocks()) {
			return floor.isOf(block1);
		}
		return false;
	}*/
}

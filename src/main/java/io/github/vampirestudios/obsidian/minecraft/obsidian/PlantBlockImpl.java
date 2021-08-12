package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class PlantBlockImpl extends PlantBlock {

    private final Block block;

    public PlantBlockImpl(Block block, Settings settings) {
        super(settings.noCollision().breakInstantly());
        this.block = block;
    }

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		/*for(net.minecraft.block.Block block1 : block.getSupportableBlocks()) {
			return floor.isOf(block1);
		}
		return false;*/
		return !floor.isAir() && !floor.isOpaque();
	}
}

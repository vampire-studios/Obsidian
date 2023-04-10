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
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return !block.information.blockProperties.translucent ? 0.2F : 1.0F;
	}

	@Override
	public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
		return !block.information.blockProperties.translucent;
	}

	@Override
	public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
		return block.information.blockProperties.translucent;
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		/*for(net.minecraft.block.Block block1 : block.getSupportableBlocks()) {
			return floor.isOf(block1);
		}
		return false;*/
		return !floor.isAir() && !floor.isOpaqueFullCube(world, pos);
	}
}

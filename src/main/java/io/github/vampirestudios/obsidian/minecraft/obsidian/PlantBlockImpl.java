package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.mojang.serialization.MapCodec;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PlantBlockImpl extends VegetationBlock {

    private final Block block;
	private static final MapCodec<VegetationBlock> CODEC = simpleCodec(PlantBlockImpl::new);

	@Override
	public MapCodec<? extends VegetationBlock> codec() {
		return CODEC;
	}

	public PlantBlockImpl(Properties settings) {
		super(settings);
		this.block = null;
	}

    public PlantBlockImpl(Block block, Properties settings) {
        super(settings.noCollision().instabreak());
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

	@Override
	protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
		/*for(net.minecraft.block.Block block1 : block.getSupportableBlocks()) {
			return floor.isOf(block1);
		}
		return false;*/
		return !floor.isAir() && !floor.isSolidRender();
	}
}

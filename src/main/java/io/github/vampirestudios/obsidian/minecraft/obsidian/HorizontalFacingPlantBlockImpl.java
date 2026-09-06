package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HorizontalFacingPlantBlockImpl extends VegetationBlock {
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	private final Block block;

	public HorizontalFacingPlantBlockImpl(Block block, Properties settings) {
		super(settings);
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

	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		VoxelShape resolved = BlockShapeUtils.resolve(block.information.collisionShape,
				block.information.shape, block.information.shapes, state.getValue(FACING));
		return resolved != null ? resolved : Shapes.block();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		VoxelShape resolved = BlockShapeUtils.resolve(block.information.outlineShape,
				block.information.shape, block.information.shapes, state.getValue(FACING));
		return resolved != null ? resolved : Shapes.block();
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state) {
		VoxelShape resolved = BlockShapeUtils.resolve(block.information.outlineShape,
				block.information.shape, block.information.shapes, state.getValue(FACING));
		return resolved != null ? resolved : BlockShapeUtils.occlusionFallback(block);
	}

}

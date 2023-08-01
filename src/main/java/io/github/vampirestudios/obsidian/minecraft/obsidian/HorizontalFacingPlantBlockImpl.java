package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HorizontalFacingPlantBlockImpl extends BushBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private final Block block;

    public HorizontalFacingPlantBlockImpl(Block block, Properties settings) {
        super(settings);
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

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = createShape(block.information.collisionShape.full_shape);
        VoxelShape northShape = createShape(block.information.collisionShape.north_shape);
        VoxelShape southShape = createShape(block.information.collisionShape.south_shape);
        VoxelShape eastShape = createShape(block.information.collisionShape.east_shape);
        VoxelShape westShape = createShape(block.information.collisionShape.west_shape);
        Direction direction = state.getValue(FACING);
        switch(direction) {
            case NORTH:
                if (northShape != null) return northShape;
                else return shape;
            case SOUTH:
                if (southShape != null) return southShape;
                else return shape;
            case EAST:
                if (eastShape != null) return eastShape;
                else return shape;
            case WEST:
                if (westShape != null) return westShape;
                else return shape;
            default:
                return shape;
        }
    }

    private VoxelShape createShape(float[] boundingBox) {
        return net.minecraft.world.level.block.Block.box(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], boundingBox[4], boundingBox[5]);
    }
}

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class HorizontalFacingPlantBlockImpl extends PlantBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private final Block block;

    public HorizontalFacingPlantBlockImpl(Block block, Settings settings) {
        super(settings);
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
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return block.information.blockProperties.translucent;
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = createShape(block.information.boundingBox.full_shape);
        VoxelShape northShape = createShape(block.information.boundingBox.north_shape);
        VoxelShape southShape = createShape(block.information.boundingBox.south_shape);
        VoxelShape eastShape = createShape(block.information.boundingBox.east_shape);
        VoxelShape westShape = createShape(block.information.boundingBox.west_shape);
        Direction direction = state.get(FACING);
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
        return net.minecraft.block.Block.createCuboidShape(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], boundingBox[4], boundingBox[5]);
    }
}

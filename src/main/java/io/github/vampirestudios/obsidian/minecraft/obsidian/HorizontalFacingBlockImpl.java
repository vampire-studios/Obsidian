package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import java.util.List;

public class HorizontalFacingBlockImpl extends HorizontalFacingBlock {

    public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

    public HorizontalFacingBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Settings settings) {
        super(settings);
        this.block = block;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
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
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        if (block.rendering != null && block.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
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
        return Block.createCuboidShape(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], boundingBox[4], boundingBox[5]);
    }
}
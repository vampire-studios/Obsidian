package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FacingBlockImpl extends DirectionalBlock {

    public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

    public FacingBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
        super(settings);
        this.block = block;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return !block.information.blockProperties.translucent ? 0.2F : 1.0F;
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return !block.information.blockProperties.translucent;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return block.information.blockProperties.translucent;
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(world, pos, state, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        if (block.rendering != null && block.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getNearestLookingDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = createShape(block.information.boundingBox.full_shape);
        VoxelShape northShape = createShape(block.information.boundingBox.north_shape);
        VoxelShape southShape = createShape(block.information.boundingBox.south_shape);
        VoxelShape eastShape = createShape(block.information.boundingBox.east_shape);
        VoxelShape westShape = createShape(block.information.boundingBox.west_shape);
        VoxelShape upShape = createShape( block.information.boundingBox.up_shape);
        VoxelShape downShape = createShape(block.information.boundingBox.down_shape);
        Direction direction = state.getValue(FACING);
        switch (direction) {
            case NORTH -> {
                if (northShape != null) return northShape;
                else return shape;
            }
            case SOUTH -> {
                if (southShape != null) return southShape;
                else return shape;
            }
            case EAST -> {
                if (eastShape != null) return eastShape;
                else return shape;
            }
            case WEST -> {
                if (westShape != null) return westShape;
                else return shape;
            }
            case DOWN -> {
                if (downShape != null) return downShape;
                else return shape;
            }
            case UP -> {
                if (upShape != null) return upShape;
                else return shape;
            }
            default -> {
                return shape;
            }
        }
    }

    private VoxelShape createShape(float[] boundingBox) {
        return Block.box(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], boundingBox[4], boundingBox[5]);
    }

}
package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HorizontalFacingBlockImpl extends HorizontalDirectionalBlock {

    public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

    public HorizontalFacingBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
        super(settings);
        this.block = block;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public String toString() {
        return block.information.name.id.toString();
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
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return block.information.getBlockSettings() != null ? block.information.getBlockSettings().translucent : super.propagatesSkylightDown(state, world, pos);
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        if (block.lore != null && block.lore.length > 0) {
            for (TooltipInformation tooltipInformation : block.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (block.information.collisionShape != null) {
            if(block.information.collisionShape.collisionType != null) {
                return switch(block.information.collisionShape.collisionType) {
                    case FULL_BLOCK -> Shapes.block();
                    case BOTTOM_SLAB -> box(0, 0, 0, 16, 8.0, 16);
                    case TOP_SLAB -> box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
                    case CUSTOM -> {
                        VoxelShape shape = createShape(block.information.collisionShape.full_shape);
                        VoxelShape northShape = createShape(block.information.collisionShape.north_shape);
                        VoxelShape southShape = createShape(block.information.collisionShape.south_shape);
                        VoxelShape eastShape = createShape(block.information.collisionShape.east_shape);
                        VoxelShape westShape = createShape(block.information.collisionShape.west_shape);
                        VoxelShape upShape = createShape( block.information.collisionShape.up_shape);
                        VoxelShape downShape = createShape(block.information.collisionShape.down_shape);
                        Direction direction = state.getValue(FACING);
                        switch (direction) {
                            case NORTH -> {
                                if (northShape != null) yield northShape;
                                else yield shape;
                            }
                            case SOUTH -> {
                                if (southShape != null) yield southShape;
                                else yield shape;
                            }
                            case EAST -> {
                                if (eastShape != null) yield eastShape;
                                else yield shape;
                            }
                            case WEST -> {
                                if (westShape != null) yield westShape;
                                else yield shape;
                            }
                            case DOWN -> {
                                if (downShape != null) yield downShape;
                                else yield shape;
                            }
                            case UP -> {
                                if (upShape != null) yield upShape;
                                else yield shape;
                            }
                            default -> {
                                yield shape;
                            }
                        }
                    }
                    case NONE -> Shapes.empty();
                };
            } else {
                return Shapes.block();
            }
        } else {
            return Shapes.block();
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (block.information.outlineShape != null) {
            if(block.information.outlineShape.collisionType != null) {
                return switch(block.information.outlineShape.collisionType) {
                    case FULL_BLOCK -> Shapes.block();
                    case BOTTOM_SLAB -> box(0, 0, 0, 16, 8.0, 16);
                    case TOP_SLAB -> box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
                    case CUSTOM -> {
                        VoxelShape shape = createShape(block.information.outlineShape.full_shape);
                        VoxelShape northShape = createShape(block.information.outlineShape.north_shape);
                        VoxelShape southShape = createShape(block.information.outlineShape.south_shape);
                        VoxelShape eastShape = createShape(block.information.outlineShape.east_shape);
                        VoxelShape westShape = createShape(block.information.outlineShape.west_shape);
                        VoxelShape upShape = createShape( block.information.outlineShape.up_shape);
                        VoxelShape downShape = createShape(block.information.outlineShape.down_shape);
                        Direction direction = state.getValue(FACING);
                        switch (direction) {
                            case NORTH -> {
                                if (northShape != null) yield northShape;
                                else yield shape;
                            }
                            case SOUTH -> {
                                if (southShape != null) yield southShape;
                                else yield shape;
                            }
                            case EAST -> {
                                if (eastShape != null) yield eastShape;
                                else yield shape;
                            }
                            case WEST -> {
                                if (westShape != null) yield westShape;
                                else yield shape;
                            }
                            case DOWN -> {
                                if (downShape != null) yield downShape;
                                else yield shape;
                            }
                            case UP -> {
                                if (upShape != null) yield upShape;
                                else yield shape;
                            }
                            default -> {
                                yield shape;
                            }
                        }
                    }
                    case NONE -> Shapes.empty();
                };
            } else {
                return Shapes.block();
            }
        } else {
            return Shapes.block();
        }
    }

    private VoxelShape createShape(float[] boundingBox) {
        return Block.box(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], boundingBox[4], boundingBox[5]);
    }
}
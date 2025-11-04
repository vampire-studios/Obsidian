package io.github.vampirestudios.obsidian.minecraft.oraxen;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ConnectableFurnitureBlock extends FurnitureBlock {
    public enum ConnectType implements StringRepresentable {
        SINGLE("single"),
        LEFT("left"),
        STRAIGHT("straight"),
        RIGHT("right"),
        INNER_LEFT("inner_left"),
        INNER_RIGHT("inner_right"),
        OUTER_LEFT("outer_left"),
        OUTER_RIGHT("outer_right");

        private final String name;

        private ConnectType(String type) {
            this.name = type;
        }

        public String toString() {
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }

    public static final EnumProperty<ConnectType> TYPE = EnumProperty.create("connectable", ConnectType.class);

    public ConnectableFurnitureBlock(NexoItem.Mechanics.Furniture mech, Properties settings) {
        super(mech, settings);
        this.registerDefaultState(this.defaultBlockState()
            .setValue(FACING, Direction.NORTH)
            .setValue(TYPE, ConnectType.SINGLE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        super.createBlockStateDefinition(b);
        b.add(TYPE);
    }

    private ConnectType getConnection(BlockState state, Level level, BlockPos pos) {
        Direction facing = state.getValue(FACING);

        Direction dir1;
        BlockState state1 = level.getBlockState(pos.relative(facing));
        if (state1.getBlock() instanceof ConnectableFurnitureBlock && (dir1 = state1.getValue(FACING)).getAxis() != state.getValue(FACING).getAxis() && isDifferentOrientation(state, level, pos, dir1.getOpposite())) {
            if (dir1 == facing.getCounterClockWise()) {
                return ConnectType.INNER_LEFT;
            }
            return ConnectType.INNER_RIGHT;
        }

        Direction dir2;
        BlockState state2 = level.getBlockState(pos.relative(facing.getOpposite()));
        if (state2.getBlock() instanceof ConnectableFurnitureBlock && (dir2 = state2.getValue(FACING)).getAxis() != state.getValue(FACING).getAxis() && isDifferentOrientation(state, level, pos, dir2)) {
            if (dir2 == facing.getCounterClockWise()) return ConnectType.OUTER_LEFT;
            return ConnectType.OUTER_RIGHT;
        }

        boolean left = canConnect(level, pos, state.getValue(FACING).getCounterClockWise());
        boolean right = canConnect(level, pos, state.getValue(FACING).getClockWise());

        if (left && right) return ConnectType.STRAIGHT;
        else if (left) return ConnectType.LEFT;
        else if (right) return ConnectType.RIGHT;

        return ConnectType.SINGLE;
    }

    public static boolean canConnect(Level level, BlockPos pos, Direction direction) {
        BlockState state = level.getBlockState(pos.relative(direction));
        return state.getBlock() instanceof ConnectableFurnitureBlock;
    }

    public static boolean isDifferentOrientation(BlockState state, Level level, BlockPos pos, Direction dir) {
        BlockState blockState = level.getBlockState(pos.relative(dir));
        return !(blockState.getBlock() instanceof ConnectableFurnitureBlock) || blockState.getValue(FACING) != state.getValue(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
        return blockState.setValue(TYPE, getConnection(blockState, context.getLevel(), blockPos));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos currentPos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return direction.getAxis().isHorizontal() ? state.setValue(TYPE, getConnection(state, (Level)level, currentPos)) : super.updateShape(state, level, scheduledTickAccess, currentPos, direction, neighborPos, neighborState, random);
    }
}

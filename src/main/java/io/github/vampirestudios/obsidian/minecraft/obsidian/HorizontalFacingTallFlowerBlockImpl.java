package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HorizontalFacingTallFlowerBlockImpl extends TallPlantBlock implements Fertilizable {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public HorizontalFacingTallFlowerBlockImpl(AbstractBlock.Settings settings) {
        super(settings);
    }

    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return false;
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    public boolean canGrow(World world, RandomGenerator random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, RandomGenerator random, BlockPos pos, BlockState state) {
        dropStack(world, pos, new ItemStack(this));
    }
}

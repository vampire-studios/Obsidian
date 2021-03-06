package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class WaterloggableTallFlowerBlockImpl extends TallPlantBlock implements Fertilizable, Waterloggable {
    public static IntProperty AGE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private final Block block;

    public WaterloggableTallFlowerBlockImpl(Block block, Settings settings) {
        super(settings);
        this.block = block;
        AGE = IntProperty.of("age", block.growable.min_age, block.growable.max_age);
        this.setDefaultState(this.stateManager.getDefaultState().with(this.getAgeProperty(), 0).with(WATERLOGGED, false));
    }

    public IntProperty getAgeProperty() {
        return AGE;
    }

    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return false;
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        dropStack(world, pos, new ItemStack(this));
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        boolean bl = world.getFluidState(blockPos).getFluid() == Fluids.WATER;
        return blockPos.getY() < world.getTopY() - 1 && world.getBlockState(blockPos.up()).with(WATERLOGGED, bl).canReplace(ctx) ? super.getPlacementState(ctx) : null;
    }

    public int getMaxAge() {
        return block.growable.max_age;
    }

    protected int getAge(BlockState state) {
        return state.get(this.getAgeProperty());
    }

    public BlockState withAge(int age) {
        return this.getDefaultState().with(this.getAgeProperty(), age);
    }

    public boolean isMature(BlockState state) {
        return state.get(this.getAgeProperty()) >= this.getMaxAge();
    }

    public boolean hasRandomTicks(BlockState state) {
        return !this.isMature(state);
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                float f = 1.0F;
                if (random.nextInt((int)(25.0F / f) + 1) == 0) {
                    world.setBlockState(pos, this.withAge(i + 1), 2);
                }
            }
        }

    }

    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int i = this.getAge(state) + this.getGrowthAmount(world);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        world.setBlockState(pos, this.withAge(i), 2);
    }

    protected int getGrowthAmount(World world) {
        return MathHelper.nextInt(world.random, 2, 5);
    }
}

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class TallCropBlock extends PlantBlock implements Fertilizable {

    public static final IntProperty AGE;
    public static final EnumProperty<DoubleBlockHalf> HALF;
    public static int growthDelay;

    static {
        AGE = Properties.AGE_7;
        HALF = Properties.DOUBLE_BLOCK_HALF;
    }

    public TallCropBlock(int delay, Settings settings) {
        super(settings);
        growthDelay = delay;
        this.setDefaultState(this.stateManager.getDefaultState().with(this.getAgeProperty(), 0).with(HALF, DoubleBlockHalf.LOWER));
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos) {
        return floor.isOf(Blocks.SOUL_SAND) || floor.isOf(Blocks.SOUL_SOIL);
    }

    public IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 6;
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

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            int age = state.get(AGE);
            if (state.get(HALF).equals(DoubleBlockHalf.UPPER)) {
                if (age < getMaxAge() && age >= 5 && world.random.nextInt(growthDelay) == 0) {
                    world.setBlockState(pos, this.withAge(age + 1).with(HALF, DoubleBlockHalf.UPPER), 2);
                    world.setBlockState(pos.down(), this.withAge(age + 1).with(HALF, DoubleBlockHalf.LOWER), 2);
                }
            } else {
                if (age < getMaxAge() && world.random.nextInt(growthDelay) == 0) {
                    if (age >= 4)
                        world.setBlockState(pos.up(), this.withAge(age + 1).with(HALF, DoubleBlockHalf.UPPER), 2);
                    world.setBlockState(pos, this.withAge(age + 1).with(HALF, DoubleBlockHalf.LOWER), 2);
                }
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(AGE).equals(getMaxAge())) {
            int count = world.random.nextInt(3) + 1;
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this, count));
            if (state.get(HALF).equals(DoubleBlockHalf.UPPER)) {
                world.setBlockState(pos, this.withAge(6).with(HALF, DoubleBlockHalf.UPPER));
                world.setBlockState(pos.down(), this.withAge(6).with(HALF, DoubleBlockHalf.LOWER));
            } else {
                world.setBlockState(pos.up(), this.withAge(6).with(HALF, DoubleBlockHalf.UPPER));
                world.setBlockState(pos, this.withAge(6).with(HALF, DoubleBlockHalf.LOWER));
            }
        }
        return ActionResult.PASS;
    }

    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int age = state.get(AGE);
        int i = this.getAge(state) + this.getGrowthAmount(world);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        if (state.get(HALF).equals(DoubleBlockHalf.UPPER)) {
            if (age < getMaxAge() && world.random.nextInt(growthDelay) == 0) {
                world.setBlockState(pos, this.withAge(i).with(HALF, DoubleBlockHalf.UPPER));
                world.setBlockState(pos.down(), this.withAge(i).with(HALF, DoubleBlockHalf.LOWER));
            }
        } else {
            if (age < getMaxAge() && world.random.nextInt(growthDelay) == 0) {
                world.setBlockState(pos.up(), this.withAge(i).with(HALF, DoubleBlockHalf.UPPER));
                world.setBlockState(pos, this.withAge(i).with(HALF, DoubleBlockHalf.LOWER));
            }
        }
    }

    protected int getGrowthAmount(World world) {
        return MathHelper.nextInt(world.random, 1, 2);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(this);
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return !this.isMature(state);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.applyGrowth(world, pos, state);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF);
        builder.add(AGE);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
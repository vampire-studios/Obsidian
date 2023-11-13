package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class TallFlowerBlockImpl extends DoublePlantBlock implements BonemealableBlock {
    public static IntegerProperty AGE;
    private final Block block;

    public TallFlowerBlockImpl(Block block, BlockBehaviour.Properties settings) {
        super(settings.dynamicShape());
        this.block = block;
        if (block.growable != null) {
            AGE = IntegerProperty.create("age", block.growable.min_age, block.growable.max_age);
            this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), 0));
        }
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

    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return false;
    }

    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        popResource(world, pos, new ItemStack(this));
    }

    public int getMaxAge() {
        return block.growable.max_age;
    }

    protected int getAge(BlockState state) {
        return state.getValue(this.getAgeProperty());
    }

    public BlockState withAge(int age) {
        return this.defaultBlockState().setValue(this.getAgeProperty(), age);
    }

    public boolean isMature(BlockState state) {
        return state.getValue(this.getAgeProperty()) >= this.getMaxAge();
    }

    public boolean isRandomlyTicking(BlockState state) {
        if (block.growable != null) {
            return !this.isMature(state);
        }
        return false;
    }

    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (world.getRawBrightness(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                float f = 1.0F;
                if (random.nextInt((int)(25.0F / f) + 1) == 0) {
                    world.setBlock(pos, this.withAge(i + 1), 2);
                }
            }
        }

    }

    public void applyGrowth(Level world, BlockPos pos, BlockState state) {
        int i = this.getAge(state) + this.getGrowthAmount(world);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }
        world.setBlock(pos, this.withAge(i), 2);
    }

    protected int getGrowthAmount(Level world) {
        return Mth.nextInt(world.random, 2, 5);
    }
}

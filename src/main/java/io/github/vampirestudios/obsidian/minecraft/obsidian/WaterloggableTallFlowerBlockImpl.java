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
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class WaterloggableTallFlowerBlockImpl extends DoublePlantBlock implements BonemealableBlock, SimpleWaterloggedBlock {
    public static IntegerProperty AGE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final Block block;

    public WaterloggableTallFlowerBlockImpl(Block block, Properties settings) {
        super(settings);
        this.block = block;
        if (block.growable != null) {
            AGE = IntegerProperty.create("age", block.growable.min_age, block.growable.max_age);
            this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), 0).setValue(WATERLOGGED, false));
        } else {
            this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
        }
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

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();
        Level world = ctx.getLevel();
        boolean bl = world.getFluidState(blockPos).getType() == Fluids.WATER;
        return blockPos.getY() < world.getMaxBuildHeight() - 1 && world.getBlockState(blockPos.above()).setValue(WATERLOGGED, bl).canBeReplaced(ctx) ? super.getStateForPlacement(ctx) : null;
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
        return !this.isMature(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
        if (block.growable != null) {
            builder.add(AGE);
        }
    }

    @Override
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

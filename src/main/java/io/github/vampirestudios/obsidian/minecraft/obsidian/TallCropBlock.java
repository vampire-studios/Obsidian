package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class TallCropBlock extends BushBlock implements BonemealableBlock {

    public static final IntegerProperty AGE;
    public static final EnumProperty<DoubleBlockHalf> HALF;
    public static int growthDelay;

    static {
        AGE = BlockStateProperties.AGE_7;
        HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    }

    public TallCropBlock(int delay, Properties settings) {
        super(settings);
        growthDelay = delay;
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), 0).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    protected boolean mayPlaceOn(BlockState floor, BlockGetter view, BlockPos pos) {
        return floor.is(Blocks.SOUL_SAND) || floor.is(Blocks.SOUL_SOIL);
    }

    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 6;
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

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        if (world.getRawBrightness(pos, 0) >= 9) {
            int age = state.getValue(AGE);
            if (state.getValue(HALF).equals(DoubleBlockHalf.UPPER)) {
                if (age < getMaxAge() && age >= 5 && world.random.nextInt(growthDelay) == 0) {
                    world.setBlock(pos, this.withAge(age + 1).setValue(HALF, DoubleBlockHalf.UPPER), 2);
                    world.setBlock(pos.below(), this.withAge(age + 1).setValue(HALF, DoubleBlockHalf.LOWER), 2);
                }
            } else {
                if (age < getMaxAge() && world.random.nextInt(growthDelay) == 0) {
                    if (age >= 4)
                        world.setBlock(pos.above(), this.withAge(age + 1).setValue(HALF, DoubleBlockHalf.UPPER), 2);
                    world.setBlock(pos, this.withAge(age + 1).setValue(HALF, DoubleBlockHalf.LOWER), 2);
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(AGE).equals(getMaxAge())) {
            int count = world.random.nextInt(3) + 1;
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this, count));
            if (state.getValue(HALF).equals(DoubleBlockHalf.UPPER)) {
                world.setBlockAndUpdate(pos, this.withAge(6).setValue(HALF, DoubleBlockHalf.UPPER));
                world.setBlockAndUpdate(pos.below(), this.withAge(6).setValue(HALF, DoubleBlockHalf.LOWER));
            } else {
                world.setBlockAndUpdate(pos.above(), this.withAge(6).setValue(HALF, DoubleBlockHalf.UPPER));
                world.setBlockAndUpdate(pos, this.withAge(6).setValue(HALF, DoubleBlockHalf.LOWER));
            }
        }
        return InteractionResult.PASS;
    }

    public void applyGrowth(Level world, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        int i = this.getAge(state) + this.getGrowthAmount(world);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        if (state.getValue(HALF).equals(DoubleBlockHalf.UPPER)) {
            if (age < getMaxAge() && world.random.nextInt(growthDelay) == 0) {
                world.setBlockAndUpdate(pos, this.withAge(i).setValue(HALF, DoubleBlockHalf.UPPER));
                world.setBlockAndUpdate(pos.below(), this.withAge(i).setValue(HALF, DoubleBlockHalf.LOWER));
            }
        } else {
            if (age < getMaxAge() && world.random.nextInt(growthDelay) == 0) {
                world.setBlockAndUpdate(pos.above(), this.withAge(i).setValue(HALF, DoubleBlockHalf.UPPER));
                world.setBlockAndUpdate(pos, this.withAge(i).setValue(HALF, DoubleBlockHalf.LOWER));
            }
        }
    }

    protected int getGrowthAmount(Level world) {
        return Mth.nextInt(world.random, 1, 2);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(this);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
        return !this.isMature(state);
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        this.applyGrowth(world, pos, state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF);
        builder.add(AGE);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

}
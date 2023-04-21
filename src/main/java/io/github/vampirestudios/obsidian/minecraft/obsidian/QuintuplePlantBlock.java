package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import java.util.ArrayList;

/**
 * A three-tall plant block
 * @author ShadewEnder redgalaxysw@gmail.com
 */
public class QuintuplePlantBlock extends BushBlock {
    public static final EnumProperty<QuadrupleBlockPart> PART = CProperties.QUADRUPLE_BLOCK_PART;

    public QuintuplePlantBlock(Properties settings) {
        super(settings.offsetType(OffsetType.XZ));
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, QuadrupleBlockPart.LOWER));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        QuadrupleBlockPart part = state.getValue(PART);
        if (direction == Direction.UP && part != QuadrupleBlockPart.UPPER) {
            Block block = newState.getBlock();
            if (block != this) return Blocks.AIR.defaultBlockState();
        } else if (direction == Direction.DOWN && part != QuadrupleBlockPart.LOWER) {
            Block block = newState.getBlock();
            if (block != this) return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();
        return blockPos.getY() < ctx.getLevel().getMaxBuildHeight() - 3 &&
                ctx.getLevel().getBlockState(blockPos.above(1)).canBeReplaced(ctx) &&
                ctx.getLevel().getBlockState(blockPos.above(2)).canBeReplaced(ctx) &&
                ctx.getLevel().getBlockState(blockPos.above(3)).canBeReplaced(ctx)
            ? super.getStateForPlacement(ctx)
            : null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlock(pos.above(1), this.defaultBlockState().setValue(PART, QuadrupleBlockPart.LOWER_MIDDLE), 3);
        world.setBlock(pos.above(2), this.defaultBlockState().setValue(PART, QuadrupleBlockPart.UPPER_MIDDLE), 3);
        world.setBlock(pos.above(3), this.defaultBlockState().setValue(PART, QuadrupleBlockPart.UPPER), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(PART) == QuadrupleBlockPart.LOWER) {
            return super.canSurvive(state, world, pos);
        } else if(state.getValue(PART) == QuadrupleBlockPart.LOWER_MIDDLE) {
            BlockState blockStateDown1 = world.getBlockState(pos.below(1));
            return (blockStateDown1.is(this) && blockStateDown1.getValue(PART) == QuadrupleBlockPart.LOWER);
        } else if(state.getValue(PART) == QuadrupleBlockPart.UPPER_MIDDLE) {
            BlockState blockStateDown1 = world.getBlockState(pos.below(1));
            return (blockStateDown1.is(this) && blockStateDown1.getValue(PART) == QuadrupleBlockPart.LOWER_MIDDLE);
        } else {
            BlockState blockStateDown1 = world.getBlockState(pos.below(1));
            BlockState blockStateDown2 = world.getBlockState(pos.below(2));
            BlockState blockStateDown3 = world.getBlockState(pos.below(3));
            return (blockStateDown1.is(this) && blockStateDown1.getValue(PART) == QuadrupleBlockPart.UPPER_MIDDLE)
                && (blockStateDown2.is(this) && blockStateDown2.getValue(PART) == QuadrupleBlockPart.LOWER_MIDDLE)
                && (blockStateDown3.is(this) && blockStateDown3.getValue(PART) == QuadrupleBlockPart.LOWER);
        }
    }

    public void placeAt(LevelAccessor world, BlockPos pos, int flags) {
        world.setBlock(pos, this.defaultBlockState().setValue(PART, QuadrupleBlockPart.LOWER), flags);
        world.setBlock(pos.above(1), this.defaultBlockState().setValue(PART, QuadrupleBlockPart.LOWER_MIDDLE), flags);
        world.setBlock(pos.above(2), this.defaultBlockState().setValue(PART, QuadrupleBlockPart.UPPER_MIDDLE), flags);
        world.setBlock(pos.above(3), this.defaultBlockState().setValue(PART, QuadrupleBlockPart.UPPER), flags);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide) {
            if (player.isCreative()) {
                onBreakInCreative(world, pos, state, player);
            } else {
                dropResources(state, world, pos, null, player, player.getMainHandItem());
            }
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        super.playerDestroy(world, player, pos, Blocks.AIR.defaultBlockState(), blockEntity, stack);
    }

    protected static void onBreakInCreative(Level world, BlockPos pos, BlockState state, Player player) {
        ArrayList<BlockPos> positions = new ArrayList<>();
        QuadrupleBlockPart tripleBlockPart = state.getValue(PART);

        if (tripleBlockPart == QuadrupleBlockPart.UPPER) {
            positions.add(pos.below(1));
            positions.add(pos.below(2));
        } else if (tripleBlockPart == QuadrupleBlockPart.LOWER_MIDDLE) {
            positions.add(pos.below(1));
            positions.add(pos.above(1));
        } else if (tripleBlockPart == QuadrupleBlockPart.UPPER_MIDDLE) {
            positions.add(pos.below(1));
            positions.add(pos.below(2));
            positions.add(pos.above(1));
        } else if (tripleBlockPart == QuadrupleBlockPart.LOWER) {
            positions.add(pos.above(1));
            positions.add(pos.above(2));
        }

        positions.forEach((blockPos) -> {
            world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 35);
            world.levelEvent(player, 2001, blockPos, Block.getId(state));
        });
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Environment(EnvType.CLIENT)
    public long getSeed(BlockState state, BlockPos pos) {
        return Mth.getSeed(pos.getX(), pos.below(state.getValue(PART) == QuadrupleBlockPart.LOWER ? 0 : 1).getY(), pos.getZ());
    }
}
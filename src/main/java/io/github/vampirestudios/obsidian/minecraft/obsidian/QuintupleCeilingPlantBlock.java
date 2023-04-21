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
import net.minecraft.world.level.BlockGetter;
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

@SuppressWarnings("unused")
public class QuintupleCeilingPlantBlock extends BushBlock {
    public static final EnumProperty<QuintupleBlockPart> PART = CProperties.QUINTUPLE_BLOCK_PART;

    public QuintupleCeilingPlantBlock(Properties settings) {
        super(settings.offsetType(OffsetType.XZ));
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, QuintupleBlockPart.UPPER));
    }

    protected boolean canPlantBelow(BlockState state, BlockGetter world, BlockPos pos) {
        return this.mayPlaceOn(state, world, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        QuintupleBlockPart part = state.getValue(PART);
        if (direction == Direction.UP && part != QuintupleBlockPart.LOWER) {
            Block block = newState.getBlock();
            if (block != this) return Blocks.AIR.defaultBlockState();
        } else if (direction == Direction.DOWN && part != QuintupleBlockPart.UPPER) {
            Block block = newState.getBlock();
            if (block != this) return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();
        return blockPos.getY() > 0 &&
                ctx.getLevel().getBlockState(blockPos.below(1)).canBeReplaced(ctx) &&
                ctx.getLevel().getBlockState(blockPos.below(2)).canBeReplaced(ctx) &&
                ctx.getLevel().getBlockState(blockPos.below(3)).canBeReplaced(ctx) &&
                ctx.getLevel().getBlockState(blockPos.below(4)).canBeReplaced(ctx)
                ? super.getStateForPlacement(ctx)
                : null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlock(pos.below(1), this.defaultBlockState().setValue(PART, QuintupleBlockPart.UPPER_MIDDLE), 3);
        world.setBlock(pos.below(2), this.defaultBlockState().setValue(PART, QuintupleBlockPart.MIDDLE), 3);
        world.setBlock(pos.below(3), this.defaultBlockState().setValue(PART, QuintupleBlockPart.LOWER_MIDDLE), 3);
        world.setBlock(pos.below(4), this.defaultBlockState().setValue(PART, QuintupleBlockPart.LOWER), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(PART) == QuintupleBlockPart.UPPER) {
            BlockPos blockPos = pos.above();
            return this.canPlantBelow(world.getBlockState(blockPos), world, pos);
        } else {
            BlockState blockState = world.getBlockState(pos.above());
            return blockState.is(this) && blockState.getValue(PART) == QuintupleBlockPart.UPPER;
        }
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        super.playerDestroy(world, player, pos, Blocks.AIR.defaultBlockState(), blockEntity, stack);
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

    protected static void onBreakInCreative(Level world, BlockPos pos, BlockState state, Player player) {
        ArrayList<BlockPos> positions = new ArrayList<>();
        QuintupleBlockPart tripleBlockPart = state.getValue(PART);

        if (tripleBlockPart == QuintupleBlockPart.UPPER) {
            positions.add(pos.below(1));
            positions.add(pos.below(2));
            positions.add(pos.below(3));
            positions.add(pos.below(4));
        } else if (tripleBlockPart == QuintupleBlockPart.LOWER_MIDDLE) {
            positions.add(pos.below(1));
            positions.add(pos.above(1));
            positions.add(pos.above(2));
            positions.add(pos.above(3));
        } else if (tripleBlockPart == QuintupleBlockPart.UPPER_MIDDLE) {
            positions.add(pos.below(1));
            positions.add(pos.below(2));
            positions.add(pos.below(3));
            positions.add(pos.above(1));
        } else if (tripleBlockPart == QuintupleBlockPart.LOWER) {
            positions.add(pos.above(1));
            positions.add(pos.above(2));
            positions.add(pos.above(3));
            positions.add(pos.above(4));
        }

        positions.forEach((blockPos) -> {
            world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 35);
            world.levelEvent(player, 2001, blockPos, Block.getId(state));
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    @Environment(EnvType.CLIENT)
    public long getSeed(BlockState state, BlockPos pos) {
        return Mth.getSeed(pos.getX(), pos.below(state.getValue(PART) == QuintupleBlockPart.LOWER ? 0 : 1).getY(), pos.getZ());
    }
}
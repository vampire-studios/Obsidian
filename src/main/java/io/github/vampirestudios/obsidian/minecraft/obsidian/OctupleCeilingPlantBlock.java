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
public class OctupleCeilingPlantBlock extends BushBlock {
    public static final EnumProperty<OctupleBlockPart> PART = CProperties.OCTUPLE_BLOCK_PART;

    public OctupleCeilingPlantBlock(Properties settings) {
        super(settings.offsetType(OffsetType.XZ));
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, OctupleBlockPart.TOP));
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
        OctupleBlockPart part = state.getValue(PART);
        if (direction == Direction.UP && part != OctupleBlockPart.BOTTOM) {
            Block block = newState.getBlock();
            if (block != this) return Blocks.AIR.defaultBlockState();
        } else if (direction == Direction.DOWN && part != OctupleBlockPart.TOP) {
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
                ctx.getLevel().getBlockState(blockPos.below(4)).canBeReplaced(ctx) &&
                ctx.getLevel().getBlockState(blockPos.below(5)).canBeReplaced(ctx) &&
                ctx.getLevel().getBlockState(blockPos.below(6)).canBeReplaced(ctx) &&
                ctx.getLevel().getBlockState(blockPos.below(7)).canBeReplaced(ctx)
                ? super.getStateForPlacement(ctx)
                : null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlock(pos.below(1), this.defaultBlockState().setValue(PART, OctupleBlockPart.UPPER), 3);
        world.setBlock(pos.below(2), this.defaultBlockState().setValue(PART, OctupleBlockPart.UPPER_MIDDLE), 3);
        world.setBlock(pos.below(3), this.defaultBlockState().setValue(PART, OctupleBlockPart.MIDDLE_UPPER), 3);
        world.setBlock(pos.below(3), this.defaultBlockState().setValue(PART, OctupleBlockPart.MIDDLE_LOWER), 3);
        world.setBlock(pos.below(4), this.defaultBlockState().setValue(PART, OctupleBlockPart.LOWER_MIDDLE), 3);
        world.setBlock(pos.below(5), this.defaultBlockState().setValue(PART, OctupleBlockPart.LOWER), 3);
        world.setBlock(pos.below(6), this.defaultBlockState().setValue(PART, OctupleBlockPart.BOTTOM), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(PART) == OctupleBlockPart.TOP) {
            BlockPos blockPos = pos.above();
            return this.canPlantBelow(world.getBlockState(blockPos), world, pos);
        } else {
            BlockState blockState = world.getBlockState(pos.above());
            return blockState.is(this) && blockState.getValue(PART) == OctupleBlockPart.TOP;
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
        OctupleBlockPart tripleBlockPart = state.getValue(PART);

        switch(tripleBlockPart) {
            case TOP -> {
                positions.add(pos.below(1));
                positions.add(pos.below(2));
                positions.add(pos.below(3));
                positions.add(pos.below(4));
                positions.add(pos.below(5));
                positions.add(pos.below(6));
            }
            case UPPER -> {
                positions.add(pos.above(1));
                positions.add(pos.below(1));
                positions.add(pos.below(2));
                positions.add(pos.below(3));
                positions.add(pos.below(4));
                positions.add(pos.below(5));
            }
            case UPPER_MIDDLE -> {
                positions.add(pos.below(1));
                positions.add(pos.below(2));
                positions.add(pos.below(3));
                positions.add(pos.below(4));
                positions.add(pos.above(1));
                positions.add(pos.above(2));
            }
            case MIDDLE_UPPER -> {
                positions.add(pos.below(1));
                positions.add(pos.below(2));
                positions.add(pos.below(3));
                positions.add(pos.below(4));
                positions.add(pos.above(1));
                positions.add(pos.above(2));
                positions.add(pos.above(3));
            }
            case MIDDLE_LOWER -> {
                positions.add(pos.below(1));
                positions.add(pos.below(2));
                positions.add(pos.below(3));
                positions.add(pos.above(1));
                positions.add(pos.above(2));
                positions.add(pos.above(3));
                positions.add(pos.above(4));
            }
            case LOWER_MIDDLE -> {
                positions.add(pos.above(1));
                positions.add(pos.above(2));
                positions.add(pos.above(3));
                positions.add(pos.above(4));
                positions.add(pos.above(5));
                positions.add(pos.below(1));
                positions.add(pos.below(2));
            }
            case LOWER -> {
                positions.add(pos.above(1));
                positions.add(pos.above(2));
                positions.add(pos.above(3));
                positions.add(pos.above(4));
                positions.add(pos.above(5));
                positions.add(pos.above(6));
                positions.add(pos.below(1));
            }
            case BOTTOM -> {
                positions.add(pos.above(1));
                positions.add(pos.above(2));
                positions.add(pos.above(3));
                positions.add(pos.above(4));
                positions.add(pos.above(5));
                positions.add(pos.above(6));
                positions.add(pos.above(7));
            }
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
        return Mth.getSeed(pos.getX(), pos.below(state.getValue(PART) == OctupleBlockPart.BOTTOM ? 0 : 1).getY(), pos.getZ());
    }
}
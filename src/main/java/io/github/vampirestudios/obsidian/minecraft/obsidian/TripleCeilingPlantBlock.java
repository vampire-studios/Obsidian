package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

@SuppressWarnings("unused")
public class TripleCeilingPlantBlock extends PlantBlock {
    public static final EnumProperty<TripleBlockPart> PART = CProperties.TRIPLE_BLOCK_PART;

    public TripleCeilingPlantBlock(Settings settings) {
        super(settings.offsetType(OffsetType.XZ));
        this.setDefaultState(this.stateManager.getDefaultState().with(PART, TripleBlockPart.UPPER));
    }

    protected boolean canPlantBelow(BlockState state, BlockView world, BlockPos pos) {
        return this.canPlantOnTop(state, world, pos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        TripleBlockPart doubleBlockHalf = state.get(PART);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == TripleBlockPart.LOWER == (direction == Direction.UP) && (!newState.isOf(this) || newState.get(PART) == doubleBlockHalf)) {
            return Blocks.AIR.getDefaultState();
        } else {
            return doubleBlockHalf == TripleBlockPart.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        return blockPos.getY() > 0 && ctx.getWorld().getBlockState(blockPos.down()).canReplace(ctx) ? super.getPlacementState(ctx) : null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.down(1), this.getDefaultState().with(PART, TripleBlockPart.MIDDLE), 3);
        world.setBlockState(pos.down(2), this.getDefaultState().with(PART, TripleBlockPart.LOWER), 3);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(PART) == TripleBlockPart.UPPER) {
            BlockPos blockPos = pos.up();
            return this.canPlantBelow(world.getBlockState(blockPos), world, pos);
        } else {
            BlockState blockState = world.getBlockState(pos.up());
            return blockState.isOf(this) && blockState.get(PART) == TripleBlockPart.UPPER;
        }
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            if (player.isCreative()) {
                TripleCeilingPlantBlock.onBreakInCreative(world, pos, state, player);
            } else {
                TripleCeilingPlantBlock.dropStacks(state, world, pos, null, player, player.getMainHandStack());
            }
        }

        super.onBreak(world, pos, state, player);
    }

    protected static void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TripleBlockPart doubleBlockHalf = state.get(PART);
        if (doubleBlockHalf == TripleBlockPart.UPPER) {
            BlockPos blockPos = pos.up();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.getBlock() == state.getBlock() && blockState.get(PART) == TripleBlockPart.LOWER) {
                world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
                world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    @Environment(EnvType.CLIENT)
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos.getX(), pos.down(state.get(PART) == TripleBlockPart.LOWER ? 0 : 1).getY(), pos.getZ());
    }
}
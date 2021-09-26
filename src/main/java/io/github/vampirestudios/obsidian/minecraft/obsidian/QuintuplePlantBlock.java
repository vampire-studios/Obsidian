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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.ArrayList;

/**
 * A three-tall plant block
 * @author ShadewEnder redgalaxysw@gmail.com
 */
public class QuintuplePlantBlock extends PlantBlock {
    public static final EnumProperty<QuadrupleBlockPart> PART = CProperties.QUADRUPLE_BLOCK_PART;

    public QuintuplePlantBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(PART, QuadrupleBlockPart.LOWER));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        QuadrupleBlockPart part = state.get(PART);
        if (direction == Direction.UP && part != QuadrupleBlockPart.UPPER) {
            Block block = newState.getBlock();
            if (block != this) return Blocks.AIR.getDefaultState();
        } else if (direction == Direction.DOWN && part != QuadrupleBlockPart.LOWER) {
            Block block = newState.getBlock();
            if (block != this) return Blocks.AIR.getDefaultState();
        }

        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        return blockPos.getY() < ctx.getWorld().getTopY() - 3 &&
                ctx.getWorld().getBlockState(blockPos.up(1)).canReplace(ctx) &&
                ctx.getWorld().getBlockState(blockPos.up(2)).canReplace(ctx) &&
                ctx.getWorld().getBlockState(blockPos.up(3)).canReplace(ctx)
            ? super.getPlacementState(ctx)
            : null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(1), this.getDefaultState().with(PART, QuadrupleBlockPart.LOWER_MIDDLE), 3);
        world.setBlockState(pos.up(2), this.getDefaultState().with(PART, QuadrupleBlockPart.UPPER_MIDDLE), 3);
        world.setBlockState(pos.up(3), this.getDefaultState().with(PART, QuadrupleBlockPart.UPPER), 3);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(PART) == QuadrupleBlockPart.LOWER) {
            return super.canPlaceAt(state, world, pos);
        } else if(state.get(PART) == QuadrupleBlockPart.LOWER_MIDDLE) {
            BlockState blockStateDown1 = world.getBlockState(pos.down(1));
            return (blockStateDown1.isOf(this) && blockStateDown1.get(PART) == QuadrupleBlockPart.LOWER);
        } else if(state.get(PART) == QuadrupleBlockPart.UPPER_MIDDLE) {
            BlockState blockStateDown1 = world.getBlockState(pos.down(1));
            return (blockStateDown1.isOf(this) && blockStateDown1.get(PART) == QuadrupleBlockPart.LOWER_MIDDLE);
        } else {
            BlockState blockStateDown1 = world.getBlockState(pos.down(1));
            BlockState blockStateDown2 = world.getBlockState(pos.down(2));
            BlockState blockStateDown3 = world.getBlockState(pos.down(3));
            return (blockStateDown1.isOf(this) && blockStateDown1.get(PART) == QuadrupleBlockPart.UPPER_MIDDLE)
                && (blockStateDown2.isOf(this) && blockStateDown2.get(PART) == QuadrupleBlockPart.LOWER_MIDDLE)
                && (blockStateDown3.isOf(this) && blockStateDown3.get(PART) == QuadrupleBlockPart.LOWER);
        }
    }

    public void placeAt(WorldAccess world, BlockPos pos, int flags) {
        world.setBlockState(pos, this.getDefaultState().with(PART, QuadrupleBlockPart.LOWER), flags);
        world.setBlockState(pos.up(1), this.getDefaultState().with(PART, QuadrupleBlockPart.LOWER_MIDDLE), flags);
        world.setBlockState(pos.up(2), this.getDefaultState().with(PART, QuadrupleBlockPart.UPPER_MIDDLE), flags);
        world.setBlockState(pos.up(3), this.getDefaultState().with(PART, QuadrupleBlockPart.UPPER), flags);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            if (player.isCreative()) {
                onBreakInCreative(world, pos, state, player);
            } else {
                dropStacks(state, world, pos, null, player, player.getMainHandStack());
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
    }

    protected static void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ArrayList<BlockPos> positions = new ArrayList<>();
        QuadrupleBlockPart tripleBlockPart = state.get(PART);

        if (tripleBlockPart == QuadrupleBlockPart.UPPER) {
            positions.add(pos.down(1));
            positions.add(pos.down(2));
        } else if (tripleBlockPart == QuadrupleBlockPart.LOWER_MIDDLE) {
            positions.add(pos.down(1));
            positions.add(pos.up(1));
        } else if (tripleBlockPart == QuadrupleBlockPart.UPPER_MIDDLE) {
            positions.add(pos.down(1));
            positions.add(pos.down(2));
            positions.add(pos.up(1));
        } else if (tripleBlockPart == QuadrupleBlockPart.LOWER) {
            positions.add(pos.up(1));
            positions.add(pos.up(2));
        }

        positions.forEach((blockPos) -> {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
            world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(state));
        });
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Environment(EnvType.CLIENT)
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos.getX(), pos.down(state.get(PART) == QuadrupleBlockPart.LOWER ? 0 : 1).getY(), pos.getZ());
    }
}
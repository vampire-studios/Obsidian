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

import java.util.ArrayList;

@SuppressWarnings("unused")
public class SextupleCeilingPlantBlock extends PlantBlock {
    public static final EnumProperty<SextupleBlockPart> PART = CProperties.SEXTUPLE_BLOCK_PART;

    public SextupleCeilingPlantBlock(Settings settings) {
        super(settings.offsetType(OffsetType.XZ));
        this.setDefaultState(this.stateManager.getDefaultState().with(PART, SextupleBlockPart.TOP));
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
        SextupleBlockPart part = state.get(PART);
        if (direction == Direction.UP && part != SextupleBlockPart.BOTTOM) {
            Block block = newState.getBlock();
            if (block != this) return Blocks.AIR.getDefaultState();
        } else if (direction == Direction.DOWN && part != SextupleBlockPart.TOP) {
            Block block = newState.getBlock();
            if (block != this) return Blocks.AIR.getDefaultState();
        }

        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        return blockPos.getY() > 0 &&
                ctx.getWorld().getBlockState(blockPos.down(1)).canReplace(ctx) &&
                ctx.getWorld().getBlockState(blockPos.down(2)).canReplace(ctx) &&
                ctx.getWorld().getBlockState(blockPos.down(3)).canReplace(ctx) &&
                ctx.getWorld().getBlockState(blockPos.down(4)).canReplace(ctx) &&
                ctx.getWorld().getBlockState(blockPos.down(5)).canReplace(ctx)
                ? super.getPlacementState(ctx)
                : null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.down(1), this.getDefaultState().with(PART, SextupleBlockPart.UPPER), 3);
        world.setBlockState(pos.down(2), this.getDefaultState().with(PART, SextupleBlockPart.UPPER_MIDDLE), 3);
        world.setBlockState(pos.down(3), this.getDefaultState().with(PART, SextupleBlockPart.LOWER_MIDDLE), 3);
        world.setBlockState(pos.down(4), this.getDefaultState().with(PART, SextupleBlockPart.LOWER), 3);
        world.setBlockState(pos.down(4), this.getDefaultState().with(PART, SextupleBlockPart.BOTTOM), 3);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(PART) == SextupleBlockPart.TOP) {
            BlockPos blockPos = pos.up();
            return this.canPlantBelow(world.getBlockState(blockPos), world, pos);
        } else {
            BlockState blockState = world.getBlockState(pos.up());
            return blockState.isOf(this) && blockState.get(PART) == SextupleBlockPart.TOP;
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
                onBreakInCreative(world, pos, state, player);
            } else {
                dropStacks(state, world, pos, null, player, player.getMainHandStack());
            }
        }

        super.onBreak(world, pos, state, player);
    }

    protected static void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ArrayList<BlockPos> positions = new ArrayList<>();
        SextupleBlockPart tripleBlockPart = state.get(PART);

        if (tripleBlockPart == SextupleBlockPart.TOP) {
            positions.add(pos.down(1));
            positions.add(pos.down(2));
            positions.add(pos.down(3));
            positions.add(pos.down(4));
            positions.add(pos.down(5));
        } else if (tripleBlockPart == SextupleBlockPart.UPPER) {
            positions.add(pos.up(1));
            positions.add(pos.down(1));
            positions.add(pos.down(2));
            positions.add(pos.down(3));
            positions.add(pos.down(4));
        } else if (tripleBlockPart == SextupleBlockPart.UPPER_MIDDLE) {
            positions.add(pos.down(1));
            positions.add(pos.down(2));
            positions.add(pos.down(3));
            positions.add(pos.up(1));
            positions.add(pos.up(2));
        } else if (tripleBlockPart == SextupleBlockPart.LOWER_MIDDLE) {
            positions.add(pos.up(1));
            positions.add(pos.up(2));
            positions.add(pos.up(3));
            positions.add(pos.down(1));
            positions.add(pos.down(2));
        } else if (tripleBlockPart == SextupleBlockPart.LOWER) {
            positions.add(pos.up(1));
            positions.add(pos.up(2));
            positions.add(pos.up(3));
            positions.add(pos.up(4));
            positions.add(pos.down(1));
        } else if (tripleBlockPart == SextupleBlockPart.BOTTOM) {
            positions.add(pos.up(1));
            positions.add(pos.up(2));
            positions.add(pos.up(3));
            positions.add(pos.up(4));
            positions.add(pos.up(5));
        }

        positions.forEach((blockPos) -> {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
            world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(state));
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    @Environment(EnvType.CLIENT)
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos.getX(), pos.down(state.get(PART) == SextupleBlockPart.BOTTOM ? 0 : 1).getY(), pos.getZ());
    }
}
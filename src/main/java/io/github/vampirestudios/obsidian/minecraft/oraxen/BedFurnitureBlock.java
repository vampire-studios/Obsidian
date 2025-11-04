package io.github.vampirestudios.obsidian.minecraft.oraxen;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BedFurnitureBlock extends FurnitureBlock {
    // BedBlock already uses this property:
    public static final EnumProperty<BedPart> PART = BedBlock.PART;

    public BedFurnitureBlock(NexoItem.Mechanics.Furniture mech, Properties settings) {
        super(mech, settings);
        // include the PART property too:
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING,     Direction.NORTH)
                .setValue(LIT, false)
                .setValue(PART,       BedPart.FOOT)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        super.createBlockStateDefinition(b);
        b.add(PART);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // vanilla BedBlock logic to choose HEAD vs FOOT:
        BlockPos pos = ctx.getClickedPos();
        Direction dir = ctx.getHorizontalDirection();
        BlockPos headPos = pos.relative(dir);
        if (!ctx.getLevel().getBlockState(headPos).canBeReplaced(ctx)) {
            return null;
        }
        return this.defaultBlockState()
                .setValue(FACING, dir)
                .setValue(PART, BedPart.FOOT);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, world, pos, oldState, moved);
        // place the HEAD half:
        if (state.getValue(PART) == BedPart.FOOT) {
            BlockPos headPos = pos.relative(state.getValue(FACING));
            world.setBlock(headPos,
                    state.setValue(PART, BedPart.HEAD),
                    3
            );
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state,
                                               Level world,
                                               BlockPos pos,
                                               Player player,
                                               BlockHitResult hit) {
        // first, run all the standard furniture interactions:
        InteractionResult base = super.useWithoutItem(state, world, pos, player, hit);
        if (base.consumesAction()) return base;

        // only the HEAD half actually lets you sleep:
        if (state.getValue(PART) != BedPart.HEAD) {
            // redirect to the head
            return InteractionResult.SUCCESS;
        }

        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        // when you break one half, drop both:
        BedPart part = state.getValue(PART);
        BlockPos other = pos.relative(state.getValue(FACING), part == BedPart.HEAD ? -1 : +1);
        if (level.getBlockState(other).getBlock() == this) {
            level.removeBlock(other, false);
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }
}

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HorizontalFacingDyableBlockImpl extends HorizontalFacingBlock implements BlockEntityProvider {

    public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

    public HorizontalFacingDyableBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Settings settings) {
        super(settings);
        this.block = block;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return !block.information.translucent ? 0.2F : 1.0F;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DyableBlockEntity(block, pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        if (stack.getItem() instanceof CustomDyeableItem item) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof DyableBlockEntity dyeableBlockEntity) {
                item.setColor(stack, dyeableBlockEntity.getDyeColor());
            }
        }
        return stack;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DyableBlockEntity dyableBlockEntity) {
            int hue = itemStack.getOrCreateSubNbt("display").getInt("color");
            if (hue != 0) {
                dyableBlockEntity.setDyeColor(itemStack.getOrCreateSubNbt("display").getInt("color"));
            } else {
                dyableBlockEntity.setDyeColor(block.additional_information.defaultColor);
            }
        }

    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return !block.information.translucent;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return block.information.translucent;
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        if (block.display != null && block.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.display.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
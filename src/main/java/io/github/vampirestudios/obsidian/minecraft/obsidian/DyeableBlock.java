package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DyeableBlock extends BlockWithEntity {
    private final Block block;

    public DyeableBlock(Block block, AbstractBlock.Settings settings) {
        super(settings);
        this.block = block;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return block.information.translucent;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return block.information.translucent;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getServer().getCommandFunctionManager().getFunction(block.functions.scheduled_tick).isPresent()) {
            world.getServer().getCommandFunctionManager().execute(world.getServer().getCommandFunctionManager().getFunction(block.functions.scheduled_tick).get(), world.getServer().getCommandSource());
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getServer().getCommandFunctionManager().getFunction(block.functions.random_tick).isPresent()) {
            world.getServer().getCommandFunctionManager().execute(world.getServer().getCommandFunctionManager().getFunction(block.functions.random_tick).get(), world.getServer().getCommandSource());
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!world.isClient) {
            if (Objects.requireNonNull(world.getServer()).getCommandFunctionManager().getFunction(block.functions.random_display_tick).isPresent()) {
                Objects.requireNonNull(world.getServer()).getCommandFunctionManager().execute(world.getServer().getCommandFunctionManager().getFunction(block.functions.random_display_tick).get(), world.getServer().getCommandSource());
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        if (block.display != null && block.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.display.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
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
}

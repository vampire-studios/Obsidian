package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.minecraft.ModBlockColor;
import io.github.vampirestudios.obsidian.minecraft.ModItemColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DyableBlockImpl extends Block implements ModBlockColor, ModItemColor {

    public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

    public DyableBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Settings settings) {
        super(settings);
        this.block = block;
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

    @Override
    public int getColor(BlockState blockState, BlockRenderView blockRenderView, BlockPos blockPos, int color) {
        return getColor(color);
    }

    @Override
    public int getColor(ItemStack stack, int color) {
        return getColor(color);
    }

    public int getColor(int tintIndex) {
        return tintIndex == 0 ? DyeColor.CYAN.getId() : -1;
    }
}
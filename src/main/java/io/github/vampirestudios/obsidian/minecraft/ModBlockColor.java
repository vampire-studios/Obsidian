package io.github.vampirestudios.obsidian.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public interface ModBlockColor {
   int getColor(BlockState blockState, BlockRenderView blockRenderView, BlockPos blockPos, int color);
}
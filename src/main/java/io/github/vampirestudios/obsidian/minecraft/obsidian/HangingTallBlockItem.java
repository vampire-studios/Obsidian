package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;

public class HangingTallBlockItem extends CustomBlockItem {
    public HangingTallBlockItem(Block block, net.minecraft.block.Block block2, Settings settings) {
        super(block, block2, settings);
    }

    protected boolean place(ItemPlacementContext context, BlockState state) {
        context.getWorld().setBlockState(context.getBlockPos().down(), Blocks.AIR.getDefaultState(), 27);
        return super.place(context, state);
    }
}

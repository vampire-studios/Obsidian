package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class HangingTallBlockItem extends CustomBlockItem {
    public HangingTallBlockItem(Block block, net.minecraft.world.level.block.Block block2, Properties settings) {
        super(block, block2, settings);
    }

    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        context.getLevel().setBlock(context.getClickedPos().below(), Blocks.AIR.defaultBlockState(), 27);
        return super.placeBlock(context, state);
    }
}

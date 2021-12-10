package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class TrapdoorBlockImpl extends TrapdoorBlock {
    public TrapdoorBlockImpl(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
}
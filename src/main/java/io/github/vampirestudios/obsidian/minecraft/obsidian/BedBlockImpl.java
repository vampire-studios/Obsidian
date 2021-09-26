package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.BedBlock;
import net.minecraft.block.enums.BedPart;
import net.minecraft.util.DyeColor;

public class BedBlockImpl extends BedBlock {
    public BedBlockImpl(Block block, Settings settings) {
        super(DyeColor.BLACK, settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(PART, BedPart.FOOT).with(OCCUPIED, false));
    }
}

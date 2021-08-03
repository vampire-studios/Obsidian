package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.CraftingTableBlock;

public class CraftingTableBlockImpl extends CraftingTableBlock {
    public CraftingTableBlockImpl(Block block, AbstractBlock.Settings settings) {
        super(settings);
    }
}

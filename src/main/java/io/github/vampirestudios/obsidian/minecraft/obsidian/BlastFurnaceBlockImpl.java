package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.vampirelib.api.VanillaTargetedItemGroupFiller;
import net.minecraft.block.BlastFurnaceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class BlastFurnaceBlockImpl extends BlastFurnaceBlock {
	private final VanillaTargetedItemGroupFiller FILLER;

	public BlastFurnaceBlockImpl(Block block, Settings settings) {
		super(settings);
		FILLER = new VanillaTargetedItemGroupFiller(Blocks.BLAST_FURNACE.asItem());
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
		FILLER.fillItem(this.asItem(), group, list);
	}

}
package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.vampirelib.api.VanillaTargetedItemGroupFiller;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class FurnaceBlockImpl extends FurnaceBlock {
	private final VanillaTargetedItemGroupFiller FILLER;

	public FurnaceBlockImpl(Block block, Settings settings) {
		super(settings);
		FILLER = new VanillaTargetedItemGroupFiller(Blocks.FURNACE);
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
		FILLER.fillItem(this.asItem(), group, list);
	}
}

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.vampirelib.api.VanillaTargetedItemGroupFiller;
import net.minecraft.block.Blocks;
import net.minecraft.block.SmokerBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class SmokerBlockImpl extends SmokerBlock {
	private final VanillaTargetedItemGroupFiller FILLER;

	public SmokerBlockImpl(Block block, Settings settings) {
		super(settings);
		FILLER = new VanillaTargetedItemGroupFiller(Blocks.SMOKER.asItem());
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
		FILLER.fillItem(this.asItem(), group, list);
	}
}

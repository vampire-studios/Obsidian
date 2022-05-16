package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.vampirelib.api.VanillaTargetedItemGroupFiller;
import net.minecraft.block.Blocks;
import net.minecraft.block.SmokerBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class SmokerBlockImpl extends SmokerBlock {
	private final VanillaTargetedItemGroupFiller FILLER;

	public SmokerBlockImpl(Settings settings) {
		super(settings);
		FILLER = new VanillaTargetedItemGroupFiller(Blocks.SMOKER);
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
		FILLER.fillItem(this.asItem(), group, list);
	}
}

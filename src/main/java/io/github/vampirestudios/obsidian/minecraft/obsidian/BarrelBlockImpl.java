package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.vampirelib.api.itemGroupSorting.VanillaTargetedItemGroupFiller;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class BarrelBlockImpl extends BarrelBlock {
	private final VanillaTargetedItemGroupFiller FILLER;

	public BarrelBlockImpl(FabricBlockSettings blockSettings) {
		super(blockSettings);
		FILLER = new VanillaTargetedItemGroupFiller(Blocks.BARREL);
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
		FILLER.fillItem(this.asItem(), group, list);
	}
}

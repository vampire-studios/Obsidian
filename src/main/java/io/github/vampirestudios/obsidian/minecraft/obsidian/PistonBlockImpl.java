package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.vampirelib.api.VanillaTargetedItemGroupFiller;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.PistonBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

public class PistonBlockImpl extends PistonBlock {
	private final VanillaTargetedItemGroupFiller FILLER;

	public PistonBlockImpl(boolean sticky, FabricBlockSettings blockSettings) {
		super(sticky, blockSettings);
		FILLER = new VanillaTargetedItemGroupFiller(sticky ? Items.STICKY_PISTON : Items.PISTON);
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
		FILLER.fillItem(this.asItem(), group, list);
	}
}

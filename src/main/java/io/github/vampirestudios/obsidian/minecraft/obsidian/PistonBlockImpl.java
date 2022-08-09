package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.vampirelib.api.itemGroupSorting.VanillaTargetedItemGroupFiller;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class PistonBlockImpl extends PistonBlock {
	private final VanillaTargetedItemGroupFiller FILLER;

	public PistonBlockImpl(boolean sticky, QuiltBlockSettings blockSettings) {
		super(sticky, blockSettings);
		FILLER = new VanillaTargetedItemGroupFiller(sticky ? Blocks.STICKY_PISTON : Blocks.PISTON);
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
		FILLER.fillItem(this.asItem(), group, list);
	}
}

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.vampirelib.api.itemGroupSorting.VanillaTargetedItemGroupFiller;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class CraftingTableBlockImpl extends CraftingTableBlock {
    private final VanillaTargetedItemGroupFiller FILLER;

    public CraftingTableBlockImpl(Block block, AbstractBlock.Settings settings) {
        super(settings);
        FILLER = new VanillaTargetedItemGroupFiller(Blocks.CRAFTING_TABLE);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
        FILLER.fillItem(this.asItem(), group, list);
    }
}

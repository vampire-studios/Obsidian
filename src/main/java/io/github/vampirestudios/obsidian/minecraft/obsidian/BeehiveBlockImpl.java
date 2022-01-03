package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.vampirelib.api.VanillaTargetedItemGroupFiller;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

public class BeehiveBlockImpl extends BeehiveBlock {
    private final VanillaTargetedItemGroupFiller FILLER;

    public BeehiveBlockImpl(Settings settings) {
        super(settings);
        FILLER = new VanillaTargetedItemGroupFiller(Blocks.BEEHIVE.asItem());
        this.setDefaultState(this.stateManager.getDefaultState().with(HONEY_LEVEL, 0).with(FACING, Direction.NORTH));
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
        FILLER.fillItem(this.asItem(), group, list);
    }

}
package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockItemImpl extends BlockItem {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

    public BlockItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Block block, Properties settings) {
        super(block, settings);
        this.item = item;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
    }
}

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class CustomBlockItem extends BlockItem {

    private final Block block;

    public CustomBlockItem(Block block, net.minecraft.block.Block blockImpl, Settings settings) {
        super(blockImpl, settings);
        this.block = block;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return block.information.has_glint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return block.information.is_enchantable;
    }

    @Override
    public int getEnchantability() {
        return block.information.enchantability;
    }

    @Override
    public Text getName() {
        return block.information.name.getName("block");
    }

}

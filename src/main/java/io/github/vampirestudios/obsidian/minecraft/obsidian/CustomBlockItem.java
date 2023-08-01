package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class CustomBlockItem extends BlockItem {

    public final Block block;

    public CustomBlockItem(Block block, net.minecraft.world.level.block.Block blockImpl, Properties settings) {
        super(blockImpl, settings);
        this.block = block;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        block.information.getRemovedTooltipSections().forEach(stack::hideTooltipPart);
        return stack;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return block.information.itemProperties != null ? block.information.itemProperties.has_glint : super.isFoil(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return block.information.itemProperties != null ? block.information.itemProperties.is_enchantable : super.isEnchantable(stack);
    }

    @Override
    public int getEnchantmentValue() {
        return block.information.itemProperties != null ? block.information.itemProperties.enchantability : super.getEnchantmentValue();
    }

    @Override
    public Component getDescription() {
        return block.information.name.getName("block");
    }

}

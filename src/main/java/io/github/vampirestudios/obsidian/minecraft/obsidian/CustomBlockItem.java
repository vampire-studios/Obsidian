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
        return block.information.getItemSettings() != null ? block.information.getItemSettings().hasEnchantmentGlint : super.isFoil(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return block.information.getItemSettings() != null ? block.information.getItemSettings().isEnchantable : super.isEnchantable(stack);
    }

    @Override
    public int getEnchantmentValue() {
        return block.information.getItemSettings() != null ? block.information.getItemSettings().enchantability : super.getEnchantmentValue();
    }

    @Override
    public Component getDescription() {
        return block.information.name.getName("block");
    }

}

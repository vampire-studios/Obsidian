package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BlockItemImpl extends BlockItem {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

    public BlockItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Block block, Properties settings) {
        super(block, settings);
        this.item = item;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.hasEnchantmentGlint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return item.information.isEnchantable;
    }

    @Override
    public int getEnchantmentValue() {
        return item.information.enchantability;
    }

    @Override
    public String getDescriptionId() {
        return super.getDescriptionId();
    }

    @Override
    public Component getDescription() {
        return item.information.name.getName("block");
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        if (item.lore != null) {
            for (TooltipInformation tooltipInformation : item.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

}

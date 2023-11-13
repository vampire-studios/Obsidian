package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpyglassItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class SpyglassItemImpl extends SpyglassItem {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

    public SpyglassItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Properties settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.getItemSettings().hasEnchantmentGlint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return item.information.getItemSettings().isEnchantable;
    }

    @Override
    public int getEnchantmentValue() {
        return item.information.getItemSettings().enchantability;
    }

    @Override
    public Component getDescription() {
        return item.information.name.getName("item");
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

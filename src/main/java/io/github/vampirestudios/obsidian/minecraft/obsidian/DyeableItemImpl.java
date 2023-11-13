package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

public class DyeableItemImpl extends ItemImpl implements DyeableLeatherItem {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

    public DyeableItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Properties settings) {
        super(item, settings);
        this.item = item;
    }

    @Override
    public int getColor(ItemStack stack) {
        CompoundTag nbtCompound = stack.getTagElement("display");
        return nbtCompound != null && nbtCompound.contains("color", 99) ? nbtCompound.getInt("color") : item.information.getItemSettings().defaultColor;
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        DyeableLeatherItem.super.setColor(stack, color);
        if (color == item.information.getItemSettings().defaultColor) {
            stack.hideTooltipPart(ItemStack.TooltipPart.DYE);
        } else {
            CompoundTag nbtCompound = stack.getOrCreateTag();
            nbtCompound.putInt("HideFlags", nbtCompound.getInt("HideFlags") |~ ItemStack.TooltipPart.DYE.getMask());
        }
    }

}

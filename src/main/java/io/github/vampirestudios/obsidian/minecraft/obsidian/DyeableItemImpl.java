package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class DyeableItemImpl extends ItemImpl implements DyeableItem {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

    public DyeableItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Settings settings) {
        super(item, settings);
        this.item = item;
    }

    @Override
    public int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt("display");
        return nbtCompound != null && nbtCompound.contains("color", 99) ? nbtCompound.getInt("color") : item.information.defaultColor;
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        DyeableItem.super.setColor(stack, color);
        if (color == item.information.defaultColor) {
            stack.addHideFlag(ItemStack.TooltipSection.DYE);
        } else {
            NbtCompound nbtCompound = stack.getOrCreateNbt();
            nbtCompound.putInt("HideFlags", nbtCompound.getInt("HideFlags") |~ ItemStack.TooltipSection.DYE.getFlag());
        }
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        super.appendStacks(group, stacks);
        if (this.isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            this.setColor(stack, item.information.defaultColor);
            stacks.add(stack);
        }
    }

}

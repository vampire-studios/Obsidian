package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class CustomDyeableItem extends CustomBlockItem implements DyeableItem {
    public CustomDyeableItem(Block block, net.minecraft.block.Block blockImpl, Settings settings) {
        super(block, blockImpl, settings);
    }

    @Override
    public int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt("display");
        return nbtCompound != null && nbtCompound.contains("color", 99) ? nbtCompound.getInt("color") : block.additional_information.defaultColor;
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        DyeableItem.super.setColor(stack, color);
        if (color == block.additional_information.defaultColor) {
            stack.addHideFlag(ItemStack.TooltipSection.DYE);
        } else {
            NbtCompound nbtCompound = stack.getNbt();
            nbtCompound.putInt("HideFlags", nbtCompound.getInt("HideFlags") |~ ItemStack.TooltipSection.DYE.getFlag());
        }
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        super.appendStacks(group, stacks);
        if (this.isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            this.setColor(stack, block.additional_information.defaultColor);
            stacks.add(stack);
        }
    }
}

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

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

}

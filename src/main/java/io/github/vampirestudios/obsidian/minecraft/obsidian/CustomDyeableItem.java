package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

public class CustomDyeableItem extends CustomBlockItem implements DyeableLeatherItem {
    public CustomDyeableItem(Block block, net.minecraft.world.level.block.Block blockImpl, Properties settings) {
        super(block, blockImpl, settings);
    }

    @Override
    public int getColor(ItemStack stack) {
        CompoundTag nbtCompound = stack.getTagElement("display");
        return nbtCompound != null && nbtCompound.contains("color", 99) ? nbtCompound.getInt("color") : block.additional_information.defaultColor;
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        DyeableLeatherItem.super.setColor(stack, color);
        if (color == block.additional_information.defaultColor) {
            stack.hideTooltipPart(ItemStack.TooltipPart.DYE);
        } else {
            CompoundTag nbtCompound = stack.getTag();
            nbtCompound.putInt("HideFlags", nbtCompound.getInt("HideFlags") |~ ItemStack.TooltipPart.DYE.getMask());
        }
    }

}

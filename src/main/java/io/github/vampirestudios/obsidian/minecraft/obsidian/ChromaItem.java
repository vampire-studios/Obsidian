//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class ChromaItem extends CustomBlockItem implements DyeableItem {
    public ChromaItem(Block block, net.minecraft.block.Block blockImpl, Settings settings) {
        super(block, blockImpl, settings);
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        DyeableItem.super.setColor(stack, color);
        if (color == 16777215) {
            stack.addHideFlag(ItemStack.TooltipSection.DYE);
        } else {
            NbtCompound nbtCompound = stack.getOrCreateNbt();
            nbtCompound.putInt("HideFlags", nbtCompound.getInt("HideFlags") |~ ItemStack.TooltipSection.DYE.getFlag());
        }
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (this.isIn(group)) {
            ItemStack stack = new ItemStack(this);
            this.setColor(stack, 16777215);
            stacks.add(stack);
        }
    }
}

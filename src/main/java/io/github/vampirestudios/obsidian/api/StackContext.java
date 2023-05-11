package io.github.vampirestudios.obsidian.api;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class StackContext {
    public final ResourceLocation item;
    public CompoundTag tag = null;
    public int count = 1;

    private Item theItem;

    public StackContext(@Nullable ResourceLocation item) {
        this.item = item;
    }

    public StackContext withTag(CompoundTag tag) {
        this.tag = tag;
        return this;
    }

    public StackContext withCount(int count) {
        this.count = count;
        return this;
    }

    public ItemStack toStack(Item self) {
        if (theItem == null) {
            if (this.item != null) {
                theItem = BuiltInRegistries.ITEM.get(this.item);
                if (theItem == null)
                    throw new RuntimeException(String.format("The item '%s' is not registered.", this.item));
            } else {
                theItem = self;
            }
        }
        ItemStack stack = new ItemStack(theItem, count);
        if (tag != null)
            stack.setTag(tag);
        return stack;
    }
}
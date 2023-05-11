package io.github.vampirestudios.obsidian.api;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public record FlexCreativeModeTab(String name, Supplier<Item> icon) {
    public static FlexCreativeModeTab create(String name, Supplier<Item> icon) {
        return new FlexCreativeModeTab(name, icon);
    }
}
package io.github.vampirestudios.obsidian.minecraft.obsidian;

public class DyeableItemImpl extends ItemImpl {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

    public DyeableItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Properties settings) {
        super(item, settings);
        this.item = item;
    }
}

package io.github.vampirestudios.obsidian.minecraft.bedrock;

import net.minecraft.world.item.Item;

public class ItemImpl extends Item {

    public io.github.vampirestudios.obsidian.api.bedrock.item.Item item;

    public ItemImpl(io.github.vampirestudios.obsidian.api.bedrock.item.Item item, Properties settings) {
        super(settings);
        this.item = item;
    }

}

package io.github.vampirestudios.obsidian.minecraft.bedrock;

import net.minecraft.item.Item;

public class ItemImpl extends Item {

	public io.github.vampirestudios.obsidian.api.bedrock.item.Item item;

	public ItemImpl(io.github.vampirestudios.obsidian.api.bedrock.item.Item item, Settings settings) {
		super(settings);
		this.item = item;
	}

}

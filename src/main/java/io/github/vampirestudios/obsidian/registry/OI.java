package io.github.vampirestudios.obsidian.registry;

import io.github.vampirestudios.obsidian.Const;
import io.github.vampirestudios.obsidian.minecraft.ColorItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class OI {
	public static Item COLOR_ITEM;

	public static void init() {
		COLOR_ITEM = Registry.register(BuiltInRegistries.ITEM, Const.id("color_item"), new ColorItem(new Item.Properties()
				.stacksTo(1)
				.setId(ResourceKey.create(Registries.ITEM, Const.id("color_item")))
		));
	}
}

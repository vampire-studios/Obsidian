package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.RegistryHelper;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class RegistryHelperItemExpanded extends RegistryHelper.Items {

	public RegistryHelperItemExpanded(String modId) {
		super(modId);
	}

	public Item registerItem(Identifier name, Item item, ResourceKey<CreativeModeTab> creativeModeTab) {
		Item registeredItem = registerItem(name.getPath(), item);
		CreativeModeTabEvents.modifyOutputEvent(creativeModeTab).register(entries -> entries.accept(registeredItem));
		return registeredItem;
	}

	public Item registerItem(String name, Item item, ResourceKey<CreativeModeTab> creativeModeTab, Item vanillaItem) {
		Item registeredItem = registerItem(name, item);
		CreativeModeTabEvents.modifyOutputEvent(creativeModeTab).register(entries -> entries.insertAfter(vanillaItem, registeredItem));
		return registeredItem;
	}

}

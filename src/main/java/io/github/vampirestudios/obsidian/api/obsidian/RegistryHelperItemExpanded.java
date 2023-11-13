package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.minecraft.obsidian.CustomDyeableItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.DyeableItemImpl;
import io.github.vampirestudios.vampirelib.utils.registry.RegistryHelper;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RegistryHelperItemExpanded extends RegistryHelper.Items {

	public RegistryHelperItemExpanded(String modId) {
		super(modId);
	}

	public Item registerDyeableItem(CustomDyeableItem item, String name) {
		registerItem(name, item);
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
			ItemStack stack = new ItemStack(item);
			item.setColor(stack, item.block.additional_information.defaultColor);
			entries.accept(stack);
		});
		return item;
	}

	public Item registerDyeableItem(DyeableItemImpl item, String name) {
		registerItem(name, item);
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
			ItemStack stack = new ItemStack(item);
			item.setColor(stack, item.item.information.getItemSettings().defaultColor);
			entries.accept(stack);
		});
		return item;
	}

	public Item registerDyeableItem(CustomDyeableItem item, String name, ResourceKey<CreativeModeTab> itemGroup) {
		registerItem(name, item);
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> {
			ItemStack stack = new ItemStack(item);
			item.setColor(stack, item.block.additional_information.defaultColor);
			entries.accept(stack);
		});
		return item;
	}

	public Item registerDyeableItem(DyeableItemImpl item, String name, ResourceKey<CreativeModeTab> itemGroup) {
		registerItem(name, item);
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> {
			ItemStack stack = new ItemStack(item);
			item.setColor(stack, item.item.information.getItemSettings().defaultColor);
			entries.accept(stack);
		});
		return item;
	}

	public Item registerItem(String name, Item item, ResourceKey<CreativeModeTab> creativeModeTab) {
		Item registeredItem = registerItem(name, item);
		ItemGroupEvents.modifyEntriesEvent(creativeModeTab).register(entries -> entries.accept(registeredItem));
		return registeredItem;
	}

	public Item registerItem(String name, Item item, ResourceKey<CreativeModeTab> creativeModeTab, Item vanillaItem) {
		Item registeredItem = registerItem(name, item);
		ItemGroupEvents.modifyEntriesEvent(creativeModeTab).register(entries -> entries.addAfter(vanillaItem, registeredItem));
		return registeredItem;
	}

}

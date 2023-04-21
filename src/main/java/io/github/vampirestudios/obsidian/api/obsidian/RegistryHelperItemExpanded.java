package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.minecraft.obsidian.CustomDyeableItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.DyeableItemImpl;
import io.github.vampirestudios.vampirelib.utils.registry.RegistryHelper;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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
			item.setColor(stack, item.item.information.defaultColor);
			entries.accept(stack);
		});
		return item;
	}

	public Item registerDyeableItem(CustomDyeableItem item, String name, CreativeModeTab itemGroup) {
		registerItem(name, item);
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> {
			ItemStack stack = new ItemStack(item);
			item.setColor(stack, item.block.additional_information.defaultColor);
			entries.add(stack);
		});
		return item;
	}

	public Item registerDyeableItem(DyeableItemImpl item, String name, CreativeModeTab itemGroup) {
		registerItem(name, item);
		ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> {
			ItemStack stack = new ItemStack(item);
			item.setColor(stack, item.item.information.defaultColor);
			entries.add(stack);
		});
		return item;
	}

}

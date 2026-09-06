package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.FoodItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Food implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		FoodItem foodItem = AddonFormats.read(addon, file, FoodItem.class);
		try {
			if (foodItem == null) return;

			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			foodItem.information.id = identifier;

			Item.Properties settings = ItemModuleHelper.baseProperties(foodItem).setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));
			var creativeTab = ItemModuleHelper.getCreativeTab(foodItem);

			ItemModuleHelper.applyFood(settings, foodItem.food_information, foodItem.components);
			Item item = Registry.register(net.minecraft.core.registries.BuiltInRegistries.ITEM, identifier, new FoodItemImpl(foodItem, settings));
			CreativeModeTabEvents.modifyOutputEvent(creativeTab).register(entries -> entries.accept(item));
			register(ContentRegistries.FOODS, "food", identifier, foodItem);
		} catch (Exception e) {
			failedRegistering("food", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "item/food";
	}
}

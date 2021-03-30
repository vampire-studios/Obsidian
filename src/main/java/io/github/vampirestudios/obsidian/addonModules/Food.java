package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ItemImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class Food implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, ModIdAndAddonPath id) throws FileNotFoundException {
        File file = addon.getFile();
        FoodItem foodItem = Obsidian.GSON.fromJson(new FileReader(file), FoodItem.class);
        try {
            if (foodItem == null) return;
            FoodComponent foodComponent = foodItem.food_information.getBuilder().build();
            Registry.register(Registry.ITEM, foodItem.information.name.id, new ItemImpl(foodItem, new Item.Settings()
                    .group(foodItem.information.getItemGroup())
                    .maxCount(foodItem.information.max_count)
                    .maxDamage(foodItem.information.use_duration)
                    .food(foodComponent)));
            register(FOODS, "food", foodItem.information.name.id.toString(), foodItem);
        } catch (Exception e) {
            failedRegistering("food", foodItem.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/food";
    }
}

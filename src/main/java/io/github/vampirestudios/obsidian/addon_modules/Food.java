package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.FoodItemImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Food implements AddonModule {

    private File file;

    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        this.file = file;
        FoodItem foodItem = Obsidian.GSON.fromJson(new FileReader(file), FoodItem.class);
        try {
            if (foodItem == null) return;
            Item.Settings settings = new Item.Settings().group(foodItem.information.getItemGroup())
                    .maxCount(foodItem.information.max_count).rarity(foodItem.information.getRarity());
            FoodComponent foodComponent = Obsidian.FOOD_COMPONENTS.get(foodItem.food_information.foodComponent);
            Registry.register(Registry.ITEM, foodItem.information.name.id, new FoodItemImpl(foodItem, settings
                    .maxDamage(foodItem.information.use_duration)
                    .food(foodComponent)));
            register(FOODS, "food", foodItem.information.name.id, foodItem);
        } catch (Exception e) {
            failedRegistering("food", foodItem.information.name.id.toString(), e);
        }
    }

    @Override
    public void initMealApi() throws FileNotFoundException {
        FoodItem foodItem = Obsidian.GSON.fromJson(new FileReader(file), FoodItem.class);
        try {
            if (foodItem == null) return;
            Item item = Registry.ITEM.get(foodItem.information.name.id);
//            MealItemRegistry.instance().register(item, ((player, stack) -> foodItem.food_information.fullness));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initAppleSkin() throws FileNotFoundException {
        /*FoodItem foodItem = Obsidian.GSON.fromJson(new FileReader(file), FoodItem.class);
        try {
            if (foodItem == null) return;
            Item item = Registry.ITEM.get(foodItem.information.name.id);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public String getType() {
        return "items/food";
    }
}

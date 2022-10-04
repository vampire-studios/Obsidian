package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.FoodItemImpl;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Food implements AddonModule {

    private File file;

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        this.file = file;
        FoodItem foodItem = Obsidian.GSON.fromJson(new FileReader(file), FoodItem.class);
        try {
            if (foodItem == null) return;

            Identifier identifier = Objects.requireNonNullElseGet(
                    foodItem.information.name.id,
                    () -> new Identifier(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (foodItem.information.name.id == null) foodItem.information.name.id = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));

            Item.Settings settings = new Item.Settings().group(foodItem.information.getItemGroup())
                    .maxCount(foodItem.information.maxStackSize).rarity(foodItem.information.rarity);
            FoodComponent foodComponent = Registries.FOOD_COMPONENTS.get(foodItem.food_information.foodComponent);
            Registry.register(Registry.ITEM, identifier, new FoodItemImpl(foodItem, settings
                    .maxDamage(foodItem.information.useDuration)
                    .food(foodComponent)));
            register(ContentRegistries.FOODS, "food", identifier, foodItem);
        } catch (Exception e) {
            failedRegistering("food", file.getName(), e);
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

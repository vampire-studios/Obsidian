package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.FoodItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Food implements AddonModule {

    private File file;

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        this.file = file;
        FoodItem foodItem = Obsidian.GSON.fromJson(new FileReader(file), FoodItem.class);
        try {
            if (foodItem == null) return;

            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    foodItem.information.name.id,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (foodItem.information.name.id == null) foodItem.information.name.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));

            Item.Properties settings = new Item.Properties()
                    .stacksTo(foodItem.information.getItemSettings().maxStackSize)
                    .rarity(Rarity.valueOf(foodItem.information.getItemSettings().rarity.toUpperCase(Locale.ROOT)));
            FoodProperties foodComponent = Registries.FOODS.get(foodItem.food_information.foodComponent);
            Item item = Registry.register(net.minecraft.core.registries.BuiltInRegistries.ITEM, identifier, new FoodItemImpl(foodItem, settings
                    .durability(foodItem.information.getItemSettings().durability)
                    .food(foodComponent)));
            ItemGroupEvents.modifyEntriesEvent(foodItem.information.getItemSettings().getItemGroup()).register(entries -> entries.accept(item));
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
            Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(foodItem.information.name.id);
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

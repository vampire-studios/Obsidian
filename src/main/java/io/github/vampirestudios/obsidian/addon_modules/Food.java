package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.FoodItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Food implements AddonModule {

    private File file;

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        this.file = file;
        FoodItem foodItem = BaseGson.GSON.fromJson(new FileReader(file), FoodItem.class);
        try {
            if (foodItem == null) return;

            Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
            foodItem.information.id = identifier;

            Item.Properties settings = createItemProperties(foodItem).setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));
            ResourceKey<CreativeModeTab> creativeTab = getCreativeTab(foodItem);

            FoodProperties foodComponent = Registries.FOODS.getValue(foodItem.food_information.foodComponent);
            Item item = Registry.register(net.minecraft.core.registries.BuiltInRegistries.ITEM, identifier, new FoodItemImpl(foodItem, settings.food(foodComponent)));
            ItemGroupEvents.modifyEntriesEvent(creativeTab).register(entries -> entries.accept(item));
            register(ContentRegistries.FOODS, "food", identifier, foodItem);
        } catch (Exception e) {
            failedRegistering("food", file.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> void applyAllComponents(Item.Properties props, DataComponentPatch map) {
        for (var e : map.entrySet()) {
            var type = (DataComponentType<T>) e.getKey();
            var opt  = (Optional<T>) e.getValue();
            opt.ifPresent(v -> props.component(type, v));
        }
    }

    private Item.Properties createItemProperties(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
        Item.Properties props = new Item.Properties();

        var comps = item.components;
        if (comps == null) {
            return props;
        }

        applyAllComponents(props, comps);
        return props;
    }

    private ResourceKey<CreativeModeTab> getCreativeTab(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
        ResourceKey<CreativeModeTab> creativeTab;

        // Check for OItemComponents.CREATIVE_TAB first
        if (item.components != null && item.components.get(OItemComponents.CREATIVE_TAB) != null &&
                item.components.get(OItemComponents.CREATIVE_TAB).isPresent()) {
            Identifier tabLocation = (Identifier) Objects.requireNonNull(item.components.get(OItemComponents.CREATIVE_TAB)).orElseThrow();
            creativeTab = ResourceKey.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, tabLocation);
        } else if (item.information.getItemSettings().getItemGroup() != null) {
            creativeTab = item.information.getItemSettings().getItemGroup();
        } else if (item.information.getItemSettings().getParentSettings().getItemGroup() != null) {
            creativeTab = item.information.getItemSettings().getParentSettings().getItemGroup();
        } else {
            creativeTab = CreativeModeTabs.BUILDING_BLOCKS;
        }
        return creativeTab;
    }

    @Override
    public void initMealApi() throws FileNotFoundException {
        FoodItem foodItem = BaseGson.GSON.fromJson(new FileReader(file), FoodItem.class);
        try {
            if (foodItem == null) return;
            Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(foodItem.information.id);
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
            Item item = Registry.ITEM.get(foodItem.information.id);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public String getType() {
        return "item/food";
    }
}

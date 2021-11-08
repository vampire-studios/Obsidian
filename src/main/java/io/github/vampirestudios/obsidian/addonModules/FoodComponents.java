package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodComponent;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class FoodComponents implements AddonModule {

	@Override
	public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
		FoodComponent foodComponent1 = Obsidian.GSON.fromJson(new FileReader(file), FoodComponent.class);
		try {
			if (foodComponent1 == null) return;
			net.minecraft.item.FoodComponent foodComponent = foodComponent1.getBuilder().build();
			Registry.register(Obsidian.FOOD_COMPONENTS, foodComponent1.id, foodComponent);
			register(FOOD_COMPONENTS, "food_component", foodComponent1.id, foodComponent1);
		} catch (Exception e) {
			failedRegistering("food_component", foodComponent1.id.toString(), e);
		}
	}

	@Override
	public String getType() {
		return "items/food/food_components";
	}

}

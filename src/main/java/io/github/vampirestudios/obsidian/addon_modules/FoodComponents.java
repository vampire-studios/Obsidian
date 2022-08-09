package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodComponent;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class FoodComponents implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		FoodComponent foodComponent1 = Obsidian.GSON.fromJson(new FileReader(file), FoodComponent.class);
		try {
			if (foodComponent1 == null) return;
			net.minecraft.item.FoodComponent foodComponent = foodComponent1.getBuilder().build();
			Registry.register(Registries.FOOD_COMPONENTS, foodComponent1.id, foodComponent);
			register(ContentRegistries.FOOD_COMPONENTS, "food_component", foodComponent1.id, foodComponent1);
		} catch (Exception e) {
			failedRegistering("food_component", foodComponent1.id.toString(), e);
		}
	}

	@Override
	public String getType() {
		return "items/food/food_components";
	}

}

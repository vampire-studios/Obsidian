package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodComponent;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class FoodComponents implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		FoodComponent foodComponent = Obsidian.GSON.fromJson(new FileReader(file), FoodComponent.class);
		try {
			if (foodComponent == null) return;

			ResourceLocation identifier = Objects.requireNonNullElseGet(
					foodComponent.id,
					() -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
			);
			if (foodComponent.id == null) foodComponent.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));

			net.minecraft.world.food.FoodProperties foodComponent1 = foodComponent.getBuilder().build();
			Registry.register(Registries.FOOD_COMPONENTS, identifier, foodComponent1);
			register(ContentRegistries.FOOD_COMPONENTS, "food_component", identifier, foodComponent);
		} catch (Exception e) {
			failedRegistering("food_component", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "items/food/food_components";
	}

}

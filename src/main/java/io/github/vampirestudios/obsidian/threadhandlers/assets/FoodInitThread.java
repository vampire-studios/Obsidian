package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class FoodInitThread implements Runnable {

	private final FoodItem foodItem;

	public FoodInitThread(FoodItem foodItemIn) {
		foodItem = foodItemIn;
	}

	@Override
	public void run() {
		Identifier identifier = foodItem.information.name.id;
		Artifice.registerAssetPack(String.format("%s:%s_food_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
			if (foodItem.information.name.translated != null) {
				foodItem.information.name.translated.forEach((languageId, name) ->
						clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
								translationBuilder.entry(String.format("item.%s.%s", foodItem.information.name.id.getNamespace(), foodItem.information.name.id.getPath()), name)));
			}
			if (foodItem.display != null && foodItem.display.model != null) {
				clientResourcePackBuilder.addItemModel(foodItem.information.name.id, modelBuilder -> {
					modelBuilder.parent(foodItem.display.model.parent);
					foodItem.display.model.textures.forEach(modelBuilder::texture);
				});
			}
			if (foodItem.display != null && foodItem.display.lore.length != 0) {
				for (TooltipInformation lore : foodItem.display.lore) {
					if (lore.text.textType.equals("translatable")) {
						lore.text.translated.forEach((languageId, name) ->
								clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
										translationBuilder.entry(lore.text.text, name)));
					}
				}
			}
			try {
				if (FabricLoader.getInstance().isDevelopmentEnvironment())
					clientResourcePackBuilder.dumpResources("testing", "assets");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}

package io.github.vampirestudios.obsidian.threadhandlers.assets;

import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.assets.ModelBuilder;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import io.github.vampirestudios.obsidian.client.ClientInit;

public class FoodInitThread implements Runnable {

    private final FoodItem foodItem;
    private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

    public FoodInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, FoodItem foodItemIn) {
        foodItem = foodItemIn;
        this.clientResourcePackBuilder = clientResourcePackBuilder;
    }

    @Override
    public void run() {
        if (foodItem.information.name.translated != null) {
            foodItem.information.name.translated.forEach((languageId, name) -> ClientInit.addTranslation(
                    foodItem.information.name.id.getNamespace(), languageId,
                    String.format("item.%s.%s", foodItem.information.name.id.getNamespace(), foodItem.information.name.id.getPath()), name
            ));
        }
        if (foodItem.display != null && foodItem.display.model != null) {
            ModelBuilder modelBuilder = new ModelBuilder()
                    .parent(foodItem.display.model.parent);
            foodItem.display.model.textures.forEach(modelBuilder::texture);
            clientResourcePackBuilder.addItemModel(foodItem.information.name.id, modelBuilder);
        }
        if (foodItem.display != null && foodItem.display.lore.length != 0) {
            for (TooltipInformation lore : foodItem.display.lore) {
                if (lore.text.textType.equals("translatable")) {
                    lore.text.translated.forEach((languageId, name) -> ClientInit.addTranslation(
                            foodItem.information.name.id.getNamespace(), languageId, lore.text.text, name
                    ));
                }
            }
        }
    }
}

package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class ItemInitThread implements Runnable {

	private final Item item;
	public ItemInitThread(Item itemIn) {
		item = itemIn;
	}

	@Override
	public void run() {
		Identifier identifier = item.information.name.id;
		Artifice.registerAssetPack(String.format("%s:%s_item_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
			if (item.information.name.translated != null) {
				item.information.name.translated.forEach((languageId, name) ->
						clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
								translationBuilder.entry(String.format("item.%s.%s", item.information.name.id.getNamespace(), item.information.name.id.getPath()), name)));
			}
			if (item.display != null && item.display.model != null) {
				clientResourcePackBuilder.addItemModel(item.information.name.id, modelBuilder -> {
					modelBuilder.parent(item.display.model.parent);
					item.display.model.textures.forEach(modelBuilder::texture);
				});
			}
			if (item.display != null && item.display.lore.length != 0) {
				for (TooltipInformation lore : item.display.lore) {
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

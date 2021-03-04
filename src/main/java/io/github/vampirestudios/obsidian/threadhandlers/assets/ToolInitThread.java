package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class ToolInitThread implements Runnable {

	private final ToolItem tool;
	public ToolInitThread(ToolItem toolIn) {
		tool = toolIn;
	}

	@Override
	public void run() {
		Identifier identifier = tool.information.name.id;
		Artifice.registerAssetPack(String.format("%s:%s_armor_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
			if (tool.information.name.translated != null) {
				tool.information.name.translated.forEach((languageId, name) ->
						clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
								translationBuilder.entry(String.format("item.%s.%s", tool.information.name.id.getNamespace(), tool.information.name.id.getPath()), name)));
			}
			if (tool.display != null && tool.display.model != null) {
				clientResourcePackBuilder.addItemModel(tool.information.name.id, modelBuilder -> {
					modelBuilder.parent(tool.display.model.parent);
					tool.display.model.textures.forEach(modelBuilder::texture);
				});
			}
			if (tool.display != null && tool.display.lore.length != 0) {
				for (TooltipInformation lore : tool.display.lore) {
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

package io.github.vampirestudios.obsidian.threadhandlers;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ShieldItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class ShieldInitThread implements Runnable {

	private final ShieldItem shield;
	public ShieldInitThread(ShieldItem shieldIn) {
		shield = shieldIn;
	}

	@Override
	public void run() {
		Identifier identifier = shield.information.name.id;
		Artifice.registerAssetPack(String.format("%s:%s_shield_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
			if (shield.information.name.translated != null) {
				shield.information.name.translated.forEach((languageId, name) ->
						clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
								translationBuilder.entry(String.format("enchantment.%s.%s", shield.information.name.id.getNamespace(), shield.information.name.id.getPath()), name)));
			}
			if (shield.display != null && shield.display.lore.length != 0) {
				for (TooltipInformation lore : shield.display.lore) {
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

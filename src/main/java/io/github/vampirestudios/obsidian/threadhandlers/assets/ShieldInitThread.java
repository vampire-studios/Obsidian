package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ShieldItem;
import net.minecraft.util.Identifier;

public class ShieldInitThread implements Runnable {

	private final ShieldItem shield;
	private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

	public ShieldInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, ShieldItem shieldIn) {
		shield = shieldIn;
		this.clientResourcePackBuilder = clientResourcePackBuilder;
	}

	@Override
	public void run() {
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
	}
}

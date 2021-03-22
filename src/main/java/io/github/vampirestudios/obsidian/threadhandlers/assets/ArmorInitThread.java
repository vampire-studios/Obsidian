package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem;
import net.minecraft.util.Identifier;

public class ArmorInitThread implements Runnable {
	private final ArmorItem armor;
	private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

	public ArmorInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, ArmorItem armor_in) {
		armor = armor_in;
		this.clientResourcePackBuilder = clientResourcePackBuilder;
	}

	@Override
	public void run() {
		if (armor.information.name.translated != null)
			armor.information.name.translated.forEach((languageId, name) -> clientResourcePackBuilder.addTranslations(
					new Identifier(Obsidian.MOD_ID, languageId), translationBuilder -> translationBuilder.entry(
							String.format("item.%s.%s", armor.information.name.id.getNamespace(),
									armor.information.name.id.getPath()), name)));

		if (armor.display != null && armor.display.model != null)
			clientResourcePackBuilder.addItemModel(armor.information.name.id, modelBuilder ->
			{
				modelBuilder.parent(armor.display.model.parent);
				armor.display.model.textures.forEach(modelBuilder::texture);
			});

		if (armor.display != null && armor.display.lore.length != 0)
			for (TooltipInformation lore : armor.display.lore)
				if (lore.text.textType.equals("translatable")) lore.text.translated.forEach((languageId, name) ->
						clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId),
								translationBuilder -> translationBuilder.entry(lore.text.text, name)));
	}
}

package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import net.minecraft.util.Identifier;

public class EnchantmentInitThread implements Runnable {

	private final Enchantment enchantment;
	private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

	public EnchantmentInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Enchantment enchantmentIn) {
		enchantment = enchantmentIn;
		this.clientResourcePackBuilder = clientResourcePackBuilder;
	}

	@Override
	public void run() {
		if (enchantment.name.translated != null) {
			enchantment.name.translated.forEach((languageId, name) ->
					clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
							translationBuilder.entry(String.format("enchantment.%s.%s", enchantment.name.id.getNamespace(), enchantment.name.id.getPath()), name)));
		}
	}
}

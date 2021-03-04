package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class EnchantmentInitThread implements Runnable {

	private final Enchantment enchantment;
	public EnchantmentInitThread(Enchantment enchantmentIn) {
		enchantment = enchantmentIn;
	}

	@Override
	public void run() {
		Identifier identifier = enchantment.name.id;
		Artifice.registerAssetPack(String.format("%s:%s_armor_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
			if (enchantment.name.translated != null) {
				enchantment.name.translated.forEach((languageId, name) ->
						clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
								translationBuilder.entry(String.format("enchantment.%s.%s", enchantment.name.id.getNamespace(), enchantment.name.id.getPath()), name)));
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

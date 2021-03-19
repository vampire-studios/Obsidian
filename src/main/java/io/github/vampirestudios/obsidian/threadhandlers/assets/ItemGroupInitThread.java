package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class ItemGroupInitThread implements Runnable {
	private final ItemGroup itemGroup;

	public ItemGroupInitThread(ItemGroup itemGroupIn) {
		itemGroup = itemGroupIn;
	}

	@Override
	public void run() {
		Artifice.registerAssetPack(String.format("%s:%s_item_group_assets", itemGroup.name.id.getNamespace(),
				itemGroup.name.id.getPath()), clientResourcePackBuilder -> {
			itemGroup.name.translated.forEach((languageId, name) -> clientResourcePackBuilder.addTranslations(
					new Identifier(Obsidian.MOD_ID, languageId), translationBuilder -> translationBuilder.entry(
							String.format("itemGroup.%s.%s", itemGroup.name.id.getNamespace(),
									itemGroup.name.id.getPath()), name)));
			try {
				if (FabricLoader.getInstance().isDevelopmentEnvironment())
					clientResourcePackBuilder.dumpResources("testing", "assets");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}

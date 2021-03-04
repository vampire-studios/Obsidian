package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class WeaponInitThread implements Runnable {

	private final WeaponItem weapon;
	public WeaponInitThread(WeaponItem weaponIn) {
		weapon = weaponIn;
	}

	@Override
	public void run() {
		Identifier identifier = weapon.information.name.id;
		Artifice.registerAssetPack(String.format("%s:%s_weapon_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
			if (weapon.information.name.translated != null) {
				weapon.information.name.translated.forEach((languageId, name) ->
						clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
								translationBuilder.entry(String.format("item.%s.%s", weapon.information.name.id.getNamespace(), weapon.information.name.id.getPath()), name)));
			}
			if (weapon.display != null && weapon.display.model != null) {
				clientResourcePackBuilder.addItemModel(weapon.information.name.id, modelBuilder -> {
					modelBuilder.parent(weapon.display.model.parent);
					weapon.display.model.textures.forEach(modelBuilder::texture);
				});
			}
			if (weapon.display != null && weapon.display.lore.length != 0) {
				for (TooltipInformation lore : weapon.display.lore) {
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

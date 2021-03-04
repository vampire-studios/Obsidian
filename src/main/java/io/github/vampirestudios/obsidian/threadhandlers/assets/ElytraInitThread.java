package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.Elytra;
import io.github.vampirestudios.obsidian.client.CustomElytraFeatureRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;

public class ElytraInitThread implements Runnable {

	private final Elytra elytra;
	public ElytraInitThread(Elytra elytraIn) {
		elytra = elytraIn;
	}

	@Override
	public void run() {
		Identifier identifier = elytra.information.name.id;
		Artifice.registerAssetPack(String.format("%s:%s_elytra_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
			if (elytra.information.name.translated != null) {
				elytra.information.name.translated.forEach((languageId, name) ->
						clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
								translationBuilder.entry(String.format("item.%s.%s", elytra.information.name.id.getNamespace(), elytra.information.name.id.getPath()), name)));
			}
			if (elytra.display != null && elytra.display.model != null) {
				clientResourcePackBuilder.addItemModel(elytra.information.name.id, modelBuilder -> {
					modelBuilder.parent(elytra.display.model.parent);
					elytra.display.model.textures.forEach(modelBuilder::texture);
				});
			}
			if (elytra.display != null && elytra.display.lore.length != 0) {
				for (TooltipInformation lore : elytra.display.lore) {
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
		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, livingEntityRenderer, registrationHelper, context) -> {
			registrationHelper.register(new CustomElytraFeatureRenderer<>(Registry.ITEM.get(identifier), elytra, livingEntityRenderer, context.getModelLoader()));
		});
	}
}
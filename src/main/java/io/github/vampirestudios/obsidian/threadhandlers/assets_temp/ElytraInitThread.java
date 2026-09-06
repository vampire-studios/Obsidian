package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import io.github.vampirestudios.obsidian.api.obsidian.item.Elytra;
import io.github.vampirestudios.obsidian.client.ClientInit;

public class ElytraInitThread implements Runnable {

	private final Elytra elytra;

	public ElytraInitThread(Elytra elytraIn) {
		elytra = elytraIn;
	}

	@Override
	public void run() {
		if (elytra.information.name.translations != null) {
			elytra.information.name.translations.forEach((languageId, name) -> ClientInit.addTranslation(
					elytra.information.id.getNamespace(), languageId,
					"item." + elytra.information.id.getNamespace(),
					elytra.information.id.getPath()
			));
		}

//        if (elytra.display != null && elytra.display.model != null) {
//            ModelBuilder modelBuilder = new ModelBuilder()
//                    .parent(elytra.display.model.parent);
//            elytra.display.model.textures.forEach(modelBuilder::texture);
//            clientResourcePackBuilder.addItemModel(elytra.information.id, modelBuilder);
//        }
		if (elytra.lore != null) {
			for (SpecialText lore : elytra.getLore()) {
				if (lore.textType.equals("translatable")) {
					lore.translations.forEach((languageId, name) -> ClientInit.addTranslation(
							elytra.information.id.getNamespace(), languageId,
							lore.text, name
					));
				}
			}
		}
//        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, livingEntityRenderer, registrationHelper, context) ->
//                registrationHelper.register(new CustomElytraFeatureRenderer<>(elytra, livingEntityRenderer, context.getModelSet())));
	}
}

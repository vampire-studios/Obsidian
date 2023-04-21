package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.Elytra;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.client.renderer.CustomElytraFeatureRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;

public class ElytraInitThread implements Runnable {

    private final Elytra elytra;

    public ElytraInitThread(Elytra elytraIn) {
        elytra = elytraIn;
    }

    @Override
    public void run() {
        if (elytra.information.name.translations != null) {
            elytra.information.name.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                    elytra.information.name.id.getNamespace(), languageId,
                    "item." + elytra.information.name.id.getNamespace(),
                    elytra.information.name.id.getPath()
            ));
        }
//        if (elytra.display != null && elytra.display.model != null) {
//            ModelBuilder modelBuilder = new ModelBuilder()
//                    .parent(elytra.display.model.parent);
//            elytra.display.model.textures.forEach(modelBuilder::texture);
//            clientResourcePackBuilder.addItemModel(elytra.information.name.id, modelBuilder);
//        }
        if (elytra.display != null && elytra.display.lore.length != 0) {
            for (TooltipInformation lore : elytra.display.lore) {
                if (lore.text.textType.equals("translatable")) {
                    lore.text.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                            elytra.information.name.id.getNamespace(), languageId,
                            lore.text.text, name
                    ));
                }
            }
        }
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, livingEntityRenderer, registrationHelper, context) ->
                registrationHelper.register(new CustomElytraFeatureRenderer<>(elytra, livingEntityRenderer, context.getModelSet())));
    }
}

package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.google.common.collect.ImmutableMap;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.Elytra;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.client.CustomElytraFeatureRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.LivingEntityFeatureRendererRegistrationCallback;

public class ElytraInitThread implements Runnable {

    private final Elytra elytra;
    private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

    public ElytraInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Elytra elytraIn) {
        elytra = elytraIn;
        this.clientResourcePackBuilder = clientResourcePackBuilder;
    }

    @Override
    public void run() {
        if (elytra.information.name.translated != null) {
            elytra.information.name.translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                    elytra.information.name.id.getNamespace(),
                    ImmutableMap.of(
                            languageId,
                            ImmutableMap.of(
                                    String.format("item.%s.%s", elytra.information.name.id.getNamespace(), elytra.information.name.id.getPath()),
                                    name
                            )
                    )
            ));
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
                    lore.text.translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                            elytra.information.name.id.getNamespace(),
                            ImmutableMap.of(
                                    languageId,
                                    ImmutableMap.of(
                                            lore.text.text,
                                            name
                                    )
                            )
                    ));
                }
            }
        }
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, livingEntityRenderer, registrationHelper, context) ->
                registrationHelper.register(new CustomElytraFeatureRenderer<>(elytra, livingEntityRenderer, context.getModelLoader())));
    }
}

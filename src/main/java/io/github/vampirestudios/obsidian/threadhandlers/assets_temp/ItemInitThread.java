package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.client.ClientInit;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ItemInitThread implements Runnable {

    private final Item item;

    public ItemInitThread(Item itemIn) {
        item = itemIn;
    }

    @Override
    public void run() {
        if (item.information.name.translations != null) {
            item.information.name.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                    item.information.name.id.getNamespace(), languageId,
                    "item." + item.information.name.id.getNamespace() + "." + item.information.name.id.getPath(), name
            ));
        }
        if (item.information.dyeable) {
            net.minecraft.world.item.Item registeredItem = BuiltInRegistries.ITEM.get(item.information.name.id);
            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> stack.getOrCreateTagElement("display").contains("color") ?
                    stack.getOrCreateTagElement("display").getInt("color") : item.information.defaultColor, registeredItem);
        }
        /*if (item.information.wearable && item.information.wearableSlot != null && item.information.wearableSlot.equals("chest")) {
            LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
                if (entityRenderer instanceof PlayerEntityRenderer) {
                    for (ItemInformation.RenderModeModel renderModeModel : item.information.renderModeModels) {
                        registrationHelper.register(new BakedModelFeatureRenderer<>((PlayerEntityRenderer) entityRenderer,
                                BakedModelManagerHelper.getModel(MinecraftClient.getInstance().getBakedModelManager(),
                                        renderModeModel.model)));
                    }
                }
            });
        }*/
        /*if (item.display != null && item.display.model != null) {
            ModelBuilder modelBuilder = new ModelBuilder()
                    .parent(item.display.model.parent);
            item.display.model.textures.forEach(modelBuilder::texture);
            clientResourcePackBuilder.addItemModel(item.information.name.id, modelBuilder);
        }
        if (item.display != null && item.display.itemModel != null) {
            ModelBuilder modelBuilder = new ModelBuilder()
                    .parent(item.display.itemModel.parent);
            item.display.itemModel.textures.forEach(modelBuilder::texture);
            clientResourcePackBuilder.addItemModel(item.information.name.id, modelBuilder);
        }*/
        if (item.display != null && item.display.lore.length != 0) {
            for (TooltipInformation lore : item.display.lore) {
                if (lore.text.textType != null && lore.text.textType.equals("translatable")) {
                    lore.text.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                            item.information.name.id.getNamespace(), languageId, lore.text.text, name
                    ));
                }
            }
        }
    }
}

package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.client.renderer.CustomRenderModeItemRenderer;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class ItemInitThread implements Runnable {

    private final Item item;
    private final RuntimeResourcePack resourcePack;

    public ItemInitThread(RuntimeResourcePack resourcePack, Item item) {
        this.item = item;
        this.resourcePack = resourcePack;
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
        if (item.information.renderModeModels != null && item.information.customRenderMode) {
            ResourceLocation normalModel;
            if (item.rendering != null) {
                if(item.rendering.model != null)
                    normalModel = item.rendering.model.parent;
                else if (item.rendering.itemModel != null)
                    normalModel = item.rendering.itemModel.parent;
                else normalModel = item.information.name.id;
            } else normalModel = item.information.name.id;
            CustomRenderModeItemRenderer customRenderModeItemRenderer = new CustomRenderModeItemRenderer(item.information.name.id, item.information.renderModeModels,
                    normalModel);
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(customRenderModeItemRenderer);
            BuiltinItemRendererRegistry.INSTANCE.register(BuiltInRegistries.ITEM.get(item.information.name.id), customRenderModeItemRenderer);
        }
        if (item.rendering != null && item.rendering.model != null) {
            if (resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(item.information.name.id, "item/")) != null) return;
            ARRPGenerationHelper.generateItemModel(resourcePack, item.information.name.id, item.rendering.model.parent, item.rendering.model.textures);
        }
        if (item.rendering != null && item.rendering.itemModel != null) {
            if (resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(item.information.name.id, "item/")) != null) return;
            ARRPGenerationHelper.generateItemModel(resourcePack, item.information.name.id, item.rendering.itemModel.parent, item.rendering.itemModel.textures);
        }
        if (item.lore != null) {
            for (TooltipInformation lore : item.lore) {
                if (lore.text.textType != null && lore.text.textType.equals("translatable")) {
                    lore.text.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                            item.information.name.id.getNamespace(), languageId, lore.text.text, name
                    ));
                }
            }
        }
    }
}

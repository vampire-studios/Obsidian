package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.RenderModeModel;
import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.iteminfo.JItemInfo;
import net.devtech.arrp.json.iteminfo.model.JItemModel;
import net.devtech.arrp.json.iteminfo.model.JModelBasic;
import net.devtech.arrp.json.iteminfo.model.JModelSelect;
import net.devtech.arrp.json.iteminfo.model.JSelectCase;
import net.devtech.arrp.json.iteminfo.property.JPropertyDisplayContext;
import net.devtech.arrp.json.iteminfo.tint.JTintDye;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
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
        JItemInfo itemInfo = new JItemInfo();
        JModelBasic fallbackModel = JModelBasic.model(Utils.prependToPath(item.information.name.id, "item/").toString());
        JItemModel model = fallbackModel;
        if (item.information != null && item.information.getItemSettings() != null &&
                item.information.getItemSettings().renderModeModels != null &&
                item.information.getItemSettings().customRenderMode) {
            JModelSelect select = new JModelSelect().property(JPropertyDisplayContext.displayContext());
            for (RenderModeModel renderModeModel : item.information.getItemSettings().renderModeModels) {
                JSelectCase caseX = JSelectCase.of(
                        renderModeModel.modes,
                        JItemModel.model(renderModeModel.model.toString())
                );
                select.addCase(caseX);
            }
            select.fallback(fallbackModel);
            model = select;
        }
        if (item.rendering != null && item.rendering.model != null) {
            if (resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(item.information.name.id, "item/")) != null) return;
            ARRPGenerationHelper.generateItemModel(resourcePack, item.information.name.id, item.rendering.model.parent, item.rendering.model.textures);
        }
        if (item.rendering != null && item.rendering.getItemModel().isPresent()) {
            if (item.information.name.id == null) return;
            if (resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(item.information.name.id, "item/")) != null) return;
            ARRPGenerationHelper.generateItemModel(resourcePack, item.information.name.id, item.rendering.getItemModel().get().getParent(), item.rendering.getItemModel().get().getTextures());
        }
        if (item.information.getItemSettings().dyeable) {
            net.minecraft.world.item.Item registeredItem = BuiltInRegistries.ITEM.getValue(item.information.name.id);
//            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> stack.getOrCreateTagElement("display").contains("color") ?
//                    stack.getOrCreateTagElement("display").getInt("color") : item.information.getItemSettings().defaultColor, registeredItem);
//            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : stack.get(DataComponents.DYED_COLOR).rgb(), registeredItem);
            model.tint(new JTintDye(item.information.getItemSettings().defaultColor));
        }
        /*if (item.rendering != null && item.rendering.blockingModel != null) {
            if (resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(item.information.name.id, "item/")) != null) return;
            if (item.rendering.getItemModel() != null)
                ARRPGenerationHelper.generateItemModel(resourcePack, item.information.name.id, item.rendering.getBlockingModel().parent, item.rendering.getBlockingModel().textures);
        }*/
        if (item.lore != null) {
            for (SpecialText lore : item.getLore()) {
                if (lore.textType != null && lore.textType.equals("translatable")) {
                    lore.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                            item.information.name.id.getNamespace(), languageId, lore.text, name
                    ));
                }
            }
        }
        if (item.components == null || item.components.get(DataComponents.ITEM_MODEL) == null) {
            itemInfo.model(model);
            resourcePack.addItemModelInfo(itemInfo, item.information.name.id);
        }
    }
}

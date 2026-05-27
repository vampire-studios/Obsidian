package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.RenderModeModel;
import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.arrp.assets.item.ItemModelDefinition;
import net.vampirestudios.arrp.assets.item.ItemModel;
import net.vampirestudios.arrp.assets.item.models.ModelBasic;
import net.vampirestudios.arrp.assets.item.models.ModelSelect;
import net.vampirestudios.arrp.assets.item.SelectCase;
import net.vampirestudios.arrp.assets.item.properties.PropertyDisplayContext;
import net.vampirestudios.arrp.assets.item.tints.TintDye;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public class ArmorInitThread implements Runnable {
    private final ArmorItem armor;
    private final RuntimeResourcePack resourcePack;

    public ArmorInitThread(RuntimeResourcePack resourcePack, ArmorItem item) {
        this.armor = item;
        this.resourcePack = resourcePack;
    }

    @Override
    public void run() {
        if (armor.information.name.translations != null)
            armor.information.name.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                    armor.information.id.getNamespace(), languageId,
                    "item." + armor.information.id.getNamespace() + "." + armor.information.id.getPath(),
                    name
            ));

        ItemModelDefinition itemInfo = new ItemModelDefinition();

        var itemId = armor.information.id;

        Identifier defModelId = armor.rendering != null
                ? armor.rendering.resolveItemDefinitionModelId(itemId)
                : Utils.prependToPath(itemId, "item/");

        ModelBasic fallbackModel = ModelBasic.model(defModelId);
        ItemModel model = fallbackModel;
        if (armor.information != null && armor.information.getItemSettings() != null &&
                armor.information.getItemSettings().renderModeModels != null &&
                armor.information.getItemSettings().customRenderMode) {
            ModelSelect select = new ModelSelect().property(PropertyDisplayContext.displayContext());
            for (RenderModeModel renderModeModel : armor.information.getItemSettings().renderModeModels) {
                SelectCase caseX = SelectCase.of(
                        renderModeModel.modes,
                        ItemModel.model(renderModeModel.model)
                );
                select.addCase(caseX);
            }
            select.fallback(fallbackModel);
            model = select;
        }
        if (armor.information.getItemSettings().dyeable) {
            model.tint(new TintDye(armor.information.getItemSettings().defaultColor));
        }
        if (armor.rendering != null) {
            boolean hasOutItemModel =
                    resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(itemId, "item/")) != null;

            if (!hasOutItemModel && armor.rendering.hasItemModelObject()) {
                var info = armor.rendering.getItemModel();
                ARRPGenerationHelper.generateItemModel(resourcePack, itemId, info.parent, info.textures);
            }
        }
        if (armor.lore != null) {
            for (SpecialText lore : armor.getLore()) {
                if (lore.textType != null && lore.textType.equals("translatable")) {
                    lore.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                            armor.information.id.getNamespace(), languageId, lore.text, name
                    ));
                }
            }
        }
        boolean dyeable = armor.information.getItemSettings().dyeable;

        // Always emit items/<id>.json — see ItemInitThread for explanation.
        itemInfo.model(model);
        resourcePack.addItemModelInfo(itemInfo, itemId);
    }
}

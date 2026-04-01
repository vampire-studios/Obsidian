package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.RenderModeModel;
import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import io.github.vampirestudios.obsidian.api.obsidian.TextureAndModelInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.iteminfo.JItemInfo;
import net.devtech.arrp.json.iteminfo.model.*;
import net.devtech.arrp.json.iteminfo.property.*;
import net.devtech.arrp.json.iteminfo.tint.JTintDye;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
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
                    item.information.id.getNamespace(), languageId,
                    "item." + item.information.id.getNamespace() + "." + item.information.id.getPath(), name
            ));
        }
        JItemInfo itemInfo = new JItemInfo();

        var itemId = item.information.id;

        Identifier defModelId = item.rendering != null
                ? item.rendering.resolveItemDefinitionModelId(itemId)
                : Utils.prependToPath(itemId, "item/");

        JModelBasic fallbackModel = JModelBasic.model(defModelId.toString());
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
        if (item.rendering != null && item.rendering.hasItemModelObject()) {
            Identifier baseOutId = Utils.prependToPath(itemId, "item/");
            boolean hasBaseAlready = resourcePack.getResource(PackType.CLIENT_RESOURCES, baseOutId) != null;
            if (!hasBaseAlready) {
                var info = item.rendering.getItemModel();
                ARRPGenerationHelper.generateItemModel1(resourcePack, baseOutId, info.parent, info.textures);
            }
        }
        if (item.rendering != null) {
            var di = item.rendering; // ItemDisplayInformation

            // --- CROSSBOW (wins over generic pulling) ---
            boolean isCrossbow =
                    di.chargedModel != null ||
                            di.fireworkModel != null ||
                            di.getArrowModel() != null;

            TextureAndModelInformation[] pulls = di.getPullingModels();

            if (isCrossbow) {
                // Models for charge_type cases
                TextureAndModelInformation arrowInfo = di.getArrowModel() != null ? di.getArrowModel() : di.getChargedModel();
                TextureAndModelInformation rocketInfo = di.getFireworkModel();

                Identifier arrowId = di.variantModelId(itemId, "_arrow");
                Identifier rocketId = di.variantModelId(itemId, "_firework");

                if (arrowInfo != null) generateIfObject(arrowId, arrowInfo);
                if (rocketInfo != null) generateIfObject(rocketId, rocketInfo);

                // Pulling stage models
                TextureAndModelInformation p0 = (di.getPullingModels() != null && di.getPullingModels().length > 0) ? di.getPullingModels()[0] : null;
                TextureAndModelInformation p1 = (di.getPullingModels() != null && di.getPullingModels().length > 1) ? di.getPullingModels()[1] : p0;
                TextureAndModelInformation p2 = (di.getPullingModels() != null && di.getPullingModels().length > 2) ? di.getPullingModels()[2] : p1;

                Identifier id0 = di.variantModelId(itemId, "_pulling_0");
                Identifier id1 = di.variantModelId(itemId, "_pulling_1");
                Identifier id2 = di.variantModelId(itemId, "_pulling_2");

                if (p0 != null) generateIfObject(id0, p0);
                if (p1 != null) generateIfObject(id1, p1);
                if (p2 != null) generateIfObject(id2, p2);

                // range_dispatch over crossbow pull (vanilla thresholds: 0.58 and 1.0, fallback pulling_0)
                JModelRangeDispatch pullDispatch = (JModelRangeDispatch) new JModelRangeDispatch()
                        .property(JPropertyCrossbowPull.crossbowPull()) // <- matches "minecraft:crossbow/pull"
                        .entry(JRangeEntry.of(0.58f, JItemModel.model(id1.toString())))
                        .entry(JRangeEntry.of(1.0f, JItemModel.model(id2.toString())))
                        .fallback(JItemModel.model(id0.toString()));

                // condition on using_item (vanilla: on_true = pullDispatch, on_false = base crossbow model)
                JModelCondition using = new JModelCondition()
                        .property(new JPropertyUsingItem())
                        .onTrue(pullDispatch)
                        .onFalse(model);

                // select on charge_type (vanilla: arrow/rocket cases, fallback = using condition)
                JModelSelect chargeType = new JModelSelect()
                        .property(JPropertyChargeType.chargeType()); // <- matches "minecraft:charge_type"

                // "when": "arrow"
                if (arrowInfo != null) {
                    chargeType.addCase(JSelectCase.of(new String[]{"arrow"}, JItemModel.model(arrowId.toString())));
                }

                // "when": "rocket"
                if (rocketInfo != null) {
                    chargeType.addCase(JSelectCase.of(new String[]{"rocket"}, JItemModel.model(rocketId.toString())));
                }

                chargeType.fallback(using);

                model = chargeType;
            } else if (pulls != null && pulls.length > 0) {
                JItemModel dispatch = buildBowUseDurationDispatch(itemId, di);

				model = new JModelCondition()
						.property(new JPropertyUsingItem())
						.onTrue(dispatch)
						.onFalse(model);
            }

            // 1) Shield blocking
            TextureAndModelInformation blockingInfo = di.getBlockingModel();
            if (blockingInfo != null) {
                Identifier blockingId = di.variantModelId(itemId, "_blocking");
                generateIfObject(blockingId, blockingInfo);

                JModelCondition blockingSelect = new JModelCondition().property(new JPropertyUsingItem());
                blockingSelect.onTrue(JItemModel.model(blockingId.toString()));
                blockingSelect.onFalse(model);
                model = blockingSelect;
            }

            // 4) Fishing rod cast (optional)
            TextureAndModelInformation castInfo = di.getCastModel();
            if (castInfo != null) {
                Identifier castId = di.variantModelId(itemId, "_cast");
                generateIfObject(castId, castInfo);

                JModelSelect castSelect = new JModelSelect().property(JPropertyFishingRodCast.fishingRodCast());
                castSelect.addCase(JSelectCase.of(new float[]{1.0f}, JItemModel.model(castId.toString())));
                castSelect.fallback(model);
                model = castSelect;
            }

            // 5) Trident throwing (optional)
            TextureAndModelInformation throwingInfo = di.getThrowingModel();
            if (throwingInfo != null) {
                Identifier throwingId = di.variantModelId(itemId, "_throwing");
                generateIfObject(throwingId, throwingInfo);

                JModelSelect throwingSelect = new JModelSelect().property(net.devtech.arrp.json.iteminfo.property.JPropertyThrowing.throwing());
                throwingSelect.addCase(JSelectCase.of(new float[]{1.0f}, JItemModel.model(throwingId.toString())));
                throwingSelect.fallback(model);
                model = throwingSelect;
            }
        }

        boolean hasItemModelComponent = item.components != null && item.components.get(DataComponents.ITEM_MODEL) != null;

        boolean dyeable = item.information.getItemSettings().dyeable;

        if (dyeable) {
            int defaultColor = item.information.getItemSettings().getDefaultColor();
            model.tint(new JTintDye(defaultColor));
        }
        if (item.lore != null) {
            for (SpecialText lore : item.getLore()) {
                if (lore.textType != null && lore.textType.equals("translatable")) {
                    lore.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                            item.information.id.getNamespace(), languageId, lore.text, name
                    ));
                }
            }
        }

        if (!hasItemModelComponent || dyeable) {
            itemInfo.model(model);
            resourcePack.addItemModelInfo(itemInfo, itemId);
        }
    }

    private static float[] vanillaDistributedThresholds(int stages) {
        if (stages <= 1) return new float[0];
        if (stages == 2) return new float[]{0.65f};
        if (stages == 3) return new float[]{0.65f, 0.90f};

        float start = 0.65f;
        float end = 0.90f;
        int count = stages - 1;
        float[] t = new float[count];

        for (int i = 1; i <= count; i++) {
            float alpha = (float) (i - 1) / (float) (count - 1); // 0..1
            t[i - 1] = start + (end - start) * alpha;
        }
        return t;
    }

    private JItemModel buildBowUseDurationDispatch(Identifier itemId, io.github.vampirestudios.obsidian.api.obsidian.ItemDisplayInformation di) {
        TextureAndModelInformation[] pulls = di.getPullingModels();
        if (pulls == null || pulls.length == 0) return null;

        int n = pulls.length;

        Identifier[] ids = new Identifier[n];
        for (int i = 0; i < n; i++) {
            ids[i] = di.variantModelId(itemId, "_pulling_" + i);
            generateIfObject(ids[i], pulls[i]);
        }

        float[] thresholds = vanillaDistributedThresholds(n);

        JModelRangeDispatch dispatch = (JModelRangeDispatch) new JModelRangeDispatch()
                .property(JPropertyUseDuration.useDuration())
                .scale(0.05f)
                .fallback(JItemModel.model(ids[0].toString()));

        for (int i = 1; i < n; i++) {
            dispatch.entry(JRangeEntry.of(thresholds[i - 1], JItemModel.model(ids[i].toString())));
        }

        return dispatch;
    }

    private void generateIfObject(Identifier targetModelId, TextureAndModelInformation info) {
        if (info == null) return;
        // targetModelId is where we generate (e.g. item/<id>_blocking)
        ARRPGenerationHelper.generateItemModel1(resourcePack, targetModelId, info.parent, info.textures);
    }
}

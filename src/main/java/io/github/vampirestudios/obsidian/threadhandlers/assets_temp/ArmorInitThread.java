package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.server.packs.PackType;

public class ArmorInitThread implements Runnable {
    private final ArmorItem armor;
    private HumanoidModel<PlayerRenderState> armorModel;
    private final RuntimeResourcePack resourcePack;

    public ArmorInitThread(RuntimeResourcePack resourcePack, ArmorItem item) {
        this.armor = item;
        this.resourcePack = resourcePack;
    }

    @Override
    public void run() {
        if (armor.information.name.translations != null)
            armor.information.name.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                    armor.information.name.id.getNamespace(), languageId,
                    "item." + armor.information.name.id.getNamespace() + "." + armor.information.name.id.getPath(),
                    name
            ));

        if (armor.information.getItemSettings().renderModeModels != null && armor.information.getItemSettings().customRenderMode) {
//            CustomRenderModeItemRenderer customRenderModeItemRenderer = getCustomRenderModeItemRenderer();
//            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(customRenderModeItemRenderer);
//            BuiltinItemRendererRegistry.INSTANCE.register(BuiltInRegistries.ITEM.getValue(armor.information.name.id), customRenderModeItemRenderer);
        }
        if (armor.rendering != null && armor.rendering.model != null) {
            if (resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(armor.information.name.id, "item/")) != null) return;
            ARRPGenerationHelper.generateItemModel(resourcePack, armor.information.name.id, armor.rendering.model.parent, armor.rendering.model.textures);
        }
        if (armor.rendering != null && armor.rendering.getItemModel().isPresent()) {
            if (resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(armor.information.name.id, "item/")) != null) return;
            ARRPGenerationHelper.generateItemModel(resourcePack, armor.information.name.id, armor.rendering.getItemModel().get().getParent(), armor.rendering.getItemModel().get().getTextures());
        }
        if (armor.lore != null) {
            for (SpecialText lore : armor.getLore()) {
                if (lore.textType != null && lore.textType.equals("translatable")) {
                    lore.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                            armor.information.name.id.getNamespace(), languageId, lore.text, name
                    ));
                }
            }
        }
        /*if (ArmorRendererRegistryImpl.get(BuiltInRegistries.ITEM.get(armor.information.name.id)) == null ) {
            ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
                boolean slim = false;
                if (entity instanceof AbstractClientPlayer player) {
                    slim = player.getModelName().equals("slim");
                }
                if (armorModel == null) {
                    armorModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR));
                }
//                contextModel.setAttributes(armorModel);
                armorModel.setAllVisible(false);
                switch (slot) {
                    case HEAD -> {
                        armorModel.head.visible = true;
                        armorModel.hat.visible = true;
                    }
                    case CHEST -> {
                        armorModel.body.visible = true;
                        armorModel.rightArm.visible = true;
                        armorModel.leftArm.visible = true;
                    }
                    case LEGS -> {
                        armorModel.body.visible = true;
                        armorModel.rightLeg.visible = true;
                        armorModel.leftLeg.visible = true;
                    }
                    case FEET -> {
                        armorModel.rightLeg.visible = true;
                        armorModel.leftLeg.visible = true;
                    }
                }
                ResourceLocation texture;
                if (slot == EquipmentSlot.LEGS) texture = armor.material.texture2;
                else texture = armor.material.texture1;
                ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, armorModel, texture);
            }, BuiltInRegistries.ITEM.get(armor.information.name.id));
        }*/

//        ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
//            Optional<ArmorModel> model = ContentRegistries.ARMOR_MODELS.getOrEmpty(armor.material.customArmorModel);
//            if (model.isPresent()) {
//                BipedEntityModel<LivingEntity> entityModel = new ArmorModelImpl<>(model.get());
//                contextModel.setAttributes(entityModel);
//                ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, entityModel, armor.material.texture1);
//            }
//        }, Registries.ITEM.get(armor.information.name.id));

//        ArmorRenderingRegistry.registerModel((entity, stack, slot, defaultModel) -> {
//            BipedEntityModel<LivingEntity> entityModel;
//            Optional<ArmorModel> model = ObsidianAddonLoader.ARMOR_MODELS.getOrEmpty(armor.material.customArmorModel);
//            if(model.isPresent()) {
//                entityModel = new ArmorModelImpl<>(model.get());
//            } else {
//                entityModel = defaultModel;
//            }
//            return entityModel;
//        }, );
//        ArmorRenderingRegistry.registerTexture((entity, stack, slot, secondLayer, suffix, defaultTexture) -> armor.material.texture, Registry.ITEM.get(armor.information.name.id));
    }
}

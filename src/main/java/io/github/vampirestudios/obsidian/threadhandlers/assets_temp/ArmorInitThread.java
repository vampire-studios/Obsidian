package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.client.renderer.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class ArmorInitThread implements Runnable {
    private final ArmorItem armor;
    private HumanoidModel<LivingEntity> armorModel;

    public ArmorInitThread(ArmorItem armor_in) {
        armor = armor_in;
    }

    @Override
    public void run() {
        if (armor.information.name.translations != null)
            armor.information.name.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                    armor.information.name.id.getNamespace(), languageId,
                    "item." + armor.information.name.id.getNamespace() + "." + armor.information.name.id.getPath(),
                    name
            ));

        /*if (armor.display != null && armor.display.model != null) {
            ModelBuilder modelBuilder = new ModelBuilder()
                    .parent(armor.display.model.parent);
            armor.display.model.textures.forEach(modelBuilder::texture);
            clientResourcePackBuilder.addItemModel(armor.information.name.id, modelBuilder);
        }*/

        if (armor.display != null && armor.display.lore.length != 0) {
            for (TooltipInformation lore : armor.display.lore) {
                if (lore.text.textType.equals("translatable")) {
                    lore.text.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                            armor.information.name.id.getNamespace(), languageId,
                            lore.text.text, name
                    ));
                }
            }
        }
        if (ArmorRenderer.getRenderer(BuiltInRegistries.ITEM.get(armor.information.name.id)) == null) {
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
        }
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

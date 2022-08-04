package io.github.vampirestudios.obsidian.threadhandlers.assets;

import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.assets.ModelBuilder;
import io.github.vampirestudios.obsidian.ArmorRenderer;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem;
import io.github.vampirestudios.obsidian.client.ClientInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ArmorInitThread implements Runnable {
    private final ArmorItem armor;
    private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;
    private BipedEntityModel<LivingEntity> armorModel;

    public ArmorInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, ArmorItem armor_in) {
        armor = armor_in;
        this.clientResourcePackBuilder = clientResourcePackBuilder;
    }

    @Override
    public void run() {
        if (armor.information.name.translated != null)
            armor.information.name.translated.forEach((languageId, name) -> ClientInit.addTranslation(
                    armor.information.name.id.getNamespace(), languageId,
                    "item." + armor.information.name.id.getNamespace() + "." + armor.information.name.id.getPath(),
                    name
            ));

        if (armor.display != null && armor.display.model != null) {
            ModelBuilder modelBuilder = new ModelBuilder()
                    .parent(armor.display.model.parent);
            armor.display.model.textures.forEach(modelBuilder::texture);
            clientResourcePackBuilder.addItemModel(armor.information.name.id, modelBuilder);
        }

        if (armor.display != null && armor.display.lore.length != 0) {
            for (TooltipInformation lore : armor.display.lore) {
                if (lore.text.textType.equals("translatable")) {
                    lore.text.translated.forEach((languageId, name) -> ClientInit.addTranslation(
                            armor.information.name.id.getNamespace(), languageId,
                            lore.text.text, name
                    ));
                }
            }
        }
        if (ArmorRenderer.getRenderer(Registry.ITEM.get(armor.information.name.id)) == null) {
            ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
                boolean slim = false;
                if (entity instanceof AbstractClientPlayerEntity player) {
                    slim = player.getModel().equals("slim");
                }
                if (armorModel == null) {
                    armorModel = new BipedEntityModel<>(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(slim ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR));
                }
                contextModel.setAttributes(armorModel);
                armorModel.setVisible(false);
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
                Identifier texture;
                if (slot == EquipmentSlot.LEGS) texture = armor.material.texture2;
                else texture = armor.material.texture1;
                ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, armorModel, texture);
            }, Registry.ITEM.get(armor.information.name.id));
        }
        /*ArmorRenderer.register(new ArmorRenderer() {
            @Override
            public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
                Optional<ArmorModel> model = ObsidianAddonLoader.ARMOR_MODELS.getOrEmpty(armor.material.customArmorModel);
                if(model.isPresent()) {
                    BipedEntityModel<LivingEntity> entityModel = new ArmorModelImpl<>(model.get());
                    contextModel.setAttributes(entityModel);
                    ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, entityModel, armor.material.texture);
                }
            }
        }, Registry.ITEM.get(armor.information.name.id));*/
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

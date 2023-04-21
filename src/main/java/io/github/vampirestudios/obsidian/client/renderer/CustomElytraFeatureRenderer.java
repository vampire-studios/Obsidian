package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.vampirestudios.obsidian.api.obsidian.item.Elytra;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.elytra.FabricElytraExtensions;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class CustomElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final ElytraModel<T> elytraEntityModel;
    private final Elytra elytra;

    public CustomElytraFeatureRenderer(Elytra elytra, RenderLayerParent<T, M> context, EntityModelSet entityModelLoader) {
        super(context);
        this.elytra = elytra;
        this.elytraEntityModel = new ElytraModel<>(entityModelLoader.bakeLayer(ModelLayers.ELYTRA));
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        if (itemStack.getItem() instanceof FabricElytraExtensions) {
            ResourceLocation identifier4;
            if (livingEntity instanceof AbstractClientPlayer abstractClientPlayerEntity) {
                if (abstractClientPlayerEntity.isElytraLoaded() && abstractClientPlayerEntity.getElytraTextureLocation() != null) {
                    identifier4 = abstractClientPlayerEntity.getElytraTextureLocation();
                } else if (abstractClientPlayerEntity.isCapeLoaded() && abstractClientPlayerEntity.getCloakTextureLocation() != null && abstractClientPlayerEntity.isModelPartShown(PlayerModelPart.CAPE)) {
                    identifier4 = abstractClientPlayerEntity.getCloakTextureLocation();
                } else {
                    identifier4 = elytra.texture;
                }
            } else {
                identifier4 = elytra.texture;
            }

            matrixStack.pushPose();
            matrixStack.translate(0.0D, 0.0D, 0.125D);
            this.getParentModel().copyPropertiesTo(this.elytraEntityModel);
            this.elytraEntityModel.setupAnim(livingEntity, f, g, j, k, l);
            VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(vertexConsumerProvider, this.elytraEntityModel.renderType(identifier4), false, itemStack.hasFoil());
            this.elytraEntityModel.renderToBuffer(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
        }
    }

}
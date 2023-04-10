package io.github.vampirestudios.obsidian.client.renderer;

import io.github.vampirestudios.obsidian.minecraft.obsidian.WearableAndDyeableItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.WearableItemImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class BackToolFeatureRenderer extends HeldItemFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private final HeldItemRenderer heldItemRenderer;

    public BackToolFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext, HeldItemRenderer heldItemRenderer) {
        super(featureRendererContext, heldItemRenderer);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack backSlotStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
        if (!backSlotStack.isEmpty() && (backSlotStack.getItem() instanceof WearableItemImpl || backSlotStack.getItem() instanceof WearableAndDyeableItemImpl)) {
            matrixStack.push();
            ModelPart modelPart = this.getContextModel().body;
            modelPart.rotate(matrixStack);
            matrixStack.translate(0.0D, -1.0D, 0D);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
            heldItemRenderer.renderItem(livingEntity, backSlotStack, ModelTransformationMode.HEAD, false, matrixStack, vertexConsumerProvider, i);
            matrixStack.pop();
        }
    }

}
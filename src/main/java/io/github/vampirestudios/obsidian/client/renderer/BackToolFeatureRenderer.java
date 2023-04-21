package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.vampirestudios.obsidian.minecraft.obsidian.WearableAndDyeableItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.WearableItemImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BackToolFeatureRenderer extends ItemInHandLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private final ItemInHandRenderer heldItemRenderer;

    public BackToolFeatureRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> featureRendererContext, ItemInHandRenderer heldItemRenderer) {
        super(featureRendererContext, heldItemRenderer);
        this.heldItemRenderer = heldItemRenderer;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, AbstractClientPlayer livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack backSlotStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        if (!backSlotStack.isEmpty() && (backSlotStack.getItem() instanceof WearableItemImpl || backSlotStack.getItem() instanceof WearableAndDyeableItemImpl)) {
            matrixStack.pushPose();
            ModelPart modelPart = this.getParentModel().body;
            modelPart.translateAndRotate(matrixStack);
            matrixStack.translate(0.0D, -1.0D, 0D);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            heldItemRenderer.renderItem(livingEntity, backSlotStack, ItemDisplayContext.HEAD, false, matrixStack, vertexConsumerProvider, i);
            matrixStack.popPose();
        }
    }

}
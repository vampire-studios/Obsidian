package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.vampirestudios.obsidian.minecraft.obsidian.WearableAndDyeableItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.WearableItemImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BackToolFeatureRenderer extends ItemInHandLayer<PlayerRenderState, PlayerModel> {

    public BackToolFeatureRenderer(RenderLayerParent<PlayerRenderState, PlayerModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, PlayerRenderState livingEntity, float f, float g) {
        ItemStack backSlotStack = livingEntity.chestEquipment;
        if (!backSlotStack.isEmpty() && (backSlotStack.getItem() instanceof WearableItemImpl || backSlotStack.getItem() instanceof WearableAndDyeableItemImpl)) {
            matrixStack.pushPose();
            ModelPart modelPart = this.getParentModel().body;
            modelPart.translateAndRotate(matrixStack);
            matrixStack.translate(0D, -1.8D, 0D);
            matrixStack.scale(0.7F, 0.7F, 0.7F);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
//            heldItemRenderer.renderStatic(livingEntity., backSlotStack, ItemDisplayContext.HEAD, false, matrixStack, vertexConsumerProvider, i);
            matrixStack.popPose();
        }
    }

}
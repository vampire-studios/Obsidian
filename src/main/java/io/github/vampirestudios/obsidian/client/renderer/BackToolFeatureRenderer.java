package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.vampirestudios.obsidian.minecraft.obsidian.WearableAndDyeableItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.WearableItemImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BackToolFeatureRenderer extends ItemInHandLayer<AvatarRenderState, PlayerModel> {

    public BackToolFeatureRenderer(RenderLayerParent<AvatarRenderState, PlayerModel> featureRendererContext) {
        super(featureRendererContext);
    }

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, AvatarRenderState armedEntityRenderState, float f, float g) {
		super.submit(poseStack, submitNodeCollector, i, armedEntityRenderState, f, g);
		ItemStack backSlotStack = armedEntityRenderState.chestEquipment;
		if (!backSlotStack.isEmpty() && (backSlotStack.getItem() instanceof WearableItemImpl || backSlotStack.getItem() instanceof WearableAndDyeableItemImpl)) {
			poseStack.pushPose();
			ModelPart modelPart = this.getParentModel().body;
			modelPart.translateAndRotate(poseStack);
			poseStack.translate(0D, -1.8D, 0D);
			poseStack.scale(0.7F, 0.7F, 0.7F);
			poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
//			submitNodeCollector.submitItem(poseStack, ItemDisplayContext.HEAD);
//            heldItemRenderer.renderStatic(livingEntity., backSlotStack, ItemDisplayContext.HEAD, false, matrixStack, vertexConsumerProvider, i);
			poseStack.popPose();
		}
	}

}
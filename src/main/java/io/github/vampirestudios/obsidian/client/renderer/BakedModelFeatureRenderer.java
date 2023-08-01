package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import java.util.function.Supplier;

public class BakedModelFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
	private final Supplier<BakedModel> modelSupplier;

	public BakedModelFeatureRenderer(RenderLayerParent<T, M> context, Supplier<BakedModel> model) {
		super(context);
		this.modelSupplier = model;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		BakedModel model = modelSupplier.get();
		VertexConsumer vertices = buffer.getBuffer(Sheets.cutoutBlockSheet());
		poseStack.pushPose();
		poseStack.mulPose(new Quaternionf(new AxisAngle4f(ageInTicks * 0.07F, 0, 1, 0)));
		poseStack.scale(-0.75F, -0.75F, 0.75F);
		float aboveHead = (float) (Math.sin(ageInTicks * 0.08F)) * 0.5F + 0.5F;
		poseStack.translate(-0.5F, 0.75F + aboveHead, -0.5F);
		BakedModelRenderer.renderBakedModel(model, vertices, poseStack.last(), packedLight);
		poseStack.popPose();
	}
}
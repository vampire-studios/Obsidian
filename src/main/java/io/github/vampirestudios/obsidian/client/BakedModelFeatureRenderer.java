package io.github.vampirestudios.obsidian.client;

import net.minecraft.client.render.TexturedRenderLayers;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3f;

public class BakedModelFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private final BakedModel model;

	public BakedModelFeatureRenderer(FeatureRendererContext<T, M> context, BakedModel model) {
		super(context);
		this.model = model;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		VertexConsumer vertices = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());
		matrices.push();
		matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(animationProgress * 0.07F));
		matrices.scale(-0.75F, -0.75F, 0.75F);
		float aboveHead = (float) (Math.sin(animationProgress * 0.08F)) * 0.5F + 0.5F;
		matrices.translate(-0.5F, 0.75F + aboveHead, -0.5F);
		BakedModelRenderer.renderBakedModel(model, vertices, matrices.peek(), light);
		matrices.pop();
	}
}
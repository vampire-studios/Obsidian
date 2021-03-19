package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class JsonEntityRenderer extends GeoEntityRenderer<EntityImpl> {

	public JsonEntityRenderer(EntityRendererFactory.Context context, Entity entity) {
		super(context, new EntityJsonModel(entity));
	}

	@Override
	public RenderLayer getRenderType(EntityImpl animatable, float partialTicks, MatrixStack stack,
									 VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
									 int packedLightIn, Identifier textureLocation) {
		return RenderLayer.getEntityTranslucent(this.getTextureLocation(animatable));
	}

}
package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.entity.Entity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.geo.GeoEntityRenderer;

public class BedrockEntityImplRenderer extends GeoEntityRenderer<EntityImpl> {

    public BedrockEntityImplRenderer(EntityRenderDispatcher entityRenderDispatcher, Entity entity) {
        super(entityRenderDispatcher, new GeckoEntityModel<>(entity));
    }

    @Override
    public RenderLayer getRenderType(EntityImpl animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }

}
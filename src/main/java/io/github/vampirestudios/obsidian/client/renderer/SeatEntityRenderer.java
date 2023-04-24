package io.github.vampirestudios.obsidian.client.renderer;

import io.github.vampirestudios.obsidian.minecraft.obsidian.SeatEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SeatEntityRenderer extends EntityRenderer<SeatEntity> {

	public SeatEntityRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public ResourceLocation getTextureLocation(SeatEntity entity) {
		return null;
	}

	@Override
	public boolean shouldRender(SeatEntity entity, Frustum frustum, double x, double y, double z) {
		return false;
	}
}
package io.github.vampirestudios.obsidian.client.renderer;

import io.github.vampirestudios.obsidian.minecraft.obsidian.SeatEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class SeatEntityRenderer extends EntityRenderer<SeatEntity> {

	public SeatEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Override
	public Identifier getTexture(SeatEntity entity) {
		return null;
	}

	@Override
	public boolean shouldRender(SeatEntity entity, Frustum frustum, double x, double y, double z) {
		return false;
	}
}
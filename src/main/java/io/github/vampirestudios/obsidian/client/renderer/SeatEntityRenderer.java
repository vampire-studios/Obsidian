package io.github.vampirestudios.obsidian.client.renderer;

import io.github.vampirestudios.obsidian.minecraft.obsidian.SeatEntity;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SeatEntityRenderer extends EntityRenderer<SeatEntity, SeatEntityRenderState> {

	public SeatEntityRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public boolean shouldRender(SeatEntity entity, Frustum culler, double camX, double camY, double camZ, float partialTicks) {
		return false;
	}

	@Override
	public SeatEntityRenderState createRenderState() {
		return new SeatEntityRenderState();
	}
}
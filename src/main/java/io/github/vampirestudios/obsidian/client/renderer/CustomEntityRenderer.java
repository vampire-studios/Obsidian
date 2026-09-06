package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImplRenderState;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityModelImpl;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class CustomEntityRenderer extends MobRenderer<EntityImpl, EntityImplRenderState, EntityModelImpl<EntityImplRenderState>> {

	private final Entity entity;

	public CustomEntityRenderer(EntityRendererProvider.Context context, Entity entity) {
		super(context, entity.description.getNewEntityModel(context), entity.shadowSize);
		this.entity = entity;
	}

	@Override
	public Identifier getTextureLocation(EntityImplRenderState state) {
		return entity.description.getEntityTexture();
	}

	@Override
	protected boolean shouldShowName(EntityImpl entity, double distanceToCameraSq) {
		return false;
	}

	@Override
	public EntityImplRenderState createRenderState() {
		return new EntityImplRenderState();
	}

	@Override
	public void extractRenderState(EntityImpl entity, EntityImplRenderState state, float tickDelta) {
		super.extractRenderState(entity, state, tickDelta);
		if (entity.animationStates != null)
			state.animationStates = entity.animationStates;
	}

	@Override
	protected void scale(EntityImplRenderState state, PoseStack poseStack) {
		poseStack.scale(0.9375F, 0.9375F, 0.9375F);
	}
}

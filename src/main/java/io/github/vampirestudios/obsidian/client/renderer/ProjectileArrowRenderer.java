package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ArrowEntityImpl;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Draws a custom arrow as its own item model, turned to face the way it is flying.
 *
 * <p>Vanilla's arrow renderer draws a fixed cross of two quads and its trident renderer needs a
 * hand-written {@code Model}; neither is something a pack can supply. Submitting the item model
 * instead means a pack gets a real 3D projectile out of the model it already ships for the item — the
 * same route the trident's own model takes — with no entity model or layer registration involved.
 */
public class ProjectileArrowRenderer extends EntityRenderer<ArrowEntityImpl, ProjectileArrowRenderState> {

	private final ItemModelResolver itemModelResolver;

	public ProjectileArrowRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.itemModelResolver = context.getItemModelResolver();
	}

	@Override
	public ProjectileArrowRenderState createRenderState() {
		return new ProjectileArrowRenderState();
	}

	@Override
	public void extractRenderState(ArrowEntityImpl arrow, ProjectileArrowRenderState state, float partialTick) {
		super.extractRenderState(arrow, state, partialTick);

		state.xRot = arrow.getXRot(partialTick);
		state.yRot = arrow.getYRot(partialTick);
		state.shake = arrow.shakeTime - partialTick;

		// GROUND is the display context a dropped item uses, which is the one packs already tune for a
		// model meant to be seen in the world rather than in a hand.
		this.itemModelResolver.updateForNonLiving(state.item, arrow.getPickupItemStackOrigin(),
				ItemDisplayContext.GROUND, arrow);
	}

	@Override
	public void submit(ProjectileArrowRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
	                   CameraRenderState camera) {
		poseStack.pushPose();

		poseStack.rotateDegrees(Axis.YP, state.yRot - 90.0F);
		poseStack.rotateDegrees(Axis.ZP, state.xRot);

		// The wobble a freshly landed arrow has, damped as the shake runs out.
		if (state.shake > 0.0F) {
			float wobble = -Mth.sin(state.shake * 3.0F) * state.shake;
			poseStack.rotateDegrees(Axis.XP, wobble);
		}

		state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);

		poseStack.popPose();
		super.submit(state, poseStack, collector, camera);
	}
}

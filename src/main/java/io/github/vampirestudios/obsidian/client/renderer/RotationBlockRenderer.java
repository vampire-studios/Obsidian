package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.vampirestudios.obsidian.block.entity.RotationBlockEntity;
import io.github.vampirestudios.obsidian.minecraft.obsidian.RotationBlockImpl;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;

/** Draws a rotation block at its real angle, which a baked model cannot be turned to. */
public class RotationBlockRenderer implements BlockEntityRenderer<RotationBlockEntity, RotationBlockRenderState> {

	private final BlockModelResolver modelResolver;
	private final BlockModelRenderState modelRenderState = new BlockModelRenderState();
	private final BlockDisplayContext displayContext = BlockDisplayContext.create();

	public RotationBlockRenderer(BlockEntityRendererProvider.Context context) {
		this.modelResolver = context.blockModelResolver();
	}

	@Override
	public RotationBlockRenderState createRenderState() {
		return new RotationBlockRenderState();
	}

	@Override
	public void extractRenderState(RotationBlockEntity blockEntity, RotationBlockRenderState state, float partialTick,
	                               Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);

		BlockState blockState = blockEntity.getBlockState();
		state.renderedState = blockState;
		state.degrees = angleOf(blockState);
	}

	@Override
	public void submit(RotationBlockRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
	                   CameraRenderState cameraRenderState) {
		if (state.renderedState == null) return;

		poseStack.pushPose();
		poseStack.translate(0.5F, 0.5F, 0.5F);
		poseStack.rotateDegrees(Axis.YP, -state.degrees);
		poseStack.translate(-0.5F, -0.5F, -0.5F);

		modelResolver.update(modelRenderState, state.renderedState, displayContext);
		modelRenderState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

		poseStack.popPose();
	}

	private static float angleOf(BlockState state) {
		if (!(state.getBlock() instanceof RotationBlockImpl rotationBlock)) return 0.0F;

		IntegerProperty property = rotationBlock.rotationProperty();
		if (!state.hasProperty(property)) return 0.0F;

		float offset = rotationBlock.block.rendering != null ? rotationBlock.block.rendering.model_rotation_offset : 0.0F;
		return state.getValue(property) * (360.0F / rotationBlock.segments()) + offset;
	}
}

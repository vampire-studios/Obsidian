package io.github.vampirestudios.obsidian.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.ArrayUtils;

public class BakedModelRenderer {
	private static final Direction[] CULL_FACES = ArrayUtils.add(Direction.values(), null);
	private static final RandomSource RANDOM = RandomSource.create();

	public static void renderBakedModel(BakedModel model, VertexConsumer vertices, PoseStack.Pose entry, int light) {
		for (Direction cullFace : CULL_FACES) {
			RANDOM.setSeed(42L);

			for (BakedQuad quad : model.getQuads(null, cullFace, RANDOM)) {
				vertices.putBulkData(entry, quad, 1.0F, 1.0F, 1.0F, light, OverlayTexture.NO_OVERLAY);
			}
		}
	}
}
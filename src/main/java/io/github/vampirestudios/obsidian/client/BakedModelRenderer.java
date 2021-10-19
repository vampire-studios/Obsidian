package io.github.vampirestudios.obsidian.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

public class BakedModelRenderer {
	private static final Direction[] CULL_FACES = ArrayUtils.add(Direction.values(), null);
	private static final Random RANDOM = new Random();

	public static void renderBakedModel(BakedModel model, VertexConsumer vertices, MatrixStack.Entry entry, int light) {
		for (Direction cullFace : CULL_FACES) {
			RANDOM.setSeed(42L);

			for (BakedQuad quad : model.getQuads(null, cullFace, RANDOM)) {
				vertices.quad(entry, quad, 1.0F, 1.0F, 1.0F, light, OverlayTexture.DEFAULT_UV);
			}
		}
	}
}
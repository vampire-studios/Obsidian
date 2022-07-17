package io.github.vampirestudios.obsidian.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import org.apache.commons.lang3.ArrayUtils;

public class BakedModelRenderer {
	private static final Direction[] CULL_FACES = ArrayUtils.add(Direction.values(), null);
	private static final RandomGenerator RANDOM = RandomGenerator.createLegacy();

	public static void renderBakedModel(BakedModel model, VertexConsumer vertices, MatrixStack.Entry entry, int light) {
		for (Direction cullFace : CULL_FACES) {
			RANDOM.setSeed(42L);

			for (BakedQuad quad : model.getQuads(null, cullFace, RANDOM)) {
				vertices.bakedQuad(entry, quad, 1.0F, 1.0F, 1.0F, light, OverlayTexture.DEFAULT_UV);
			}
		}
	}
}
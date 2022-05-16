package io.github.vampirestudios.obsidian;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class KeyframeAnimations {
	public static void animate(SinglePartEntityModel<?> hierarchicalModel, AnimationDefinition animationDefinition, long l, float f, Vec3f vector3f) {
		float g = getElapsedSeconds(animationDefinition, l);

		for(Entry<String, List<AnimationChannel>> entry : animationDefinition.boneAnimations().entrySet()) {
			Optional<ModelPart> optional = ((ExpandedSinglePartEntityModel)hierarchicalModel).getAnyDescendantWithName(entry.getKey());
			List<AnimationChannel> list = entry.getValue();
			optional.ifPresent(modelPart -> list.forEach(animationChannel -> {
					Keyframe[] keyframes = animationChannel.keyframes();
					int i = Math.max(0, MathHelper.binarySearch(0, keyframes.length, ix -> g <= keyframes[ix].timestamp()) - 1);
					int j = Math.min(keyframes.length - 1, i + 1);
					Keyframe keyframe = keyframes[i];
					Keyframe keyframe2 = keyframes[j];
					float h = g - keyframe.timestamp();
					float k = MathHelper.clamp(h / (keyframe2.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
					keyframe2.interpolation().apply(vector3f, k, keyframes, i, j, f);
					animationChannel.target().apply(modelPart, vector3f);
				}));
		}

	}

	private static float getElapsedSeconds(AnimationDefinition animationDefinition, long l) {
		float f = (float)l / 1000.0F;
		return animationDefinition.looping() ? f % animationDefinition.lengthInSeconds() : f;
	}

	public static Vec3f posVec(float f, float g, float h) {
		return new Vec3f(f, -g, h);
	}

	public static Vec3f degreeVec(float f, float g, float h) {
		return new Vec3f(f * (float) (Math.PI / 180.0), g * (float) (Math.PI / 180.0), h * (float) (Math.PI / 180.0));
	}

	public static Vec3f scaleVec(double d, double e, double f) {
		return new Vec3f((float)(d - 1.0), (float)(e - 1.0), (float)(f - 1.0));
	}
}

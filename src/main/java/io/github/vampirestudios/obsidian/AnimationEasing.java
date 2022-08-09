package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.MathHelper;
import net.minecraft.client.render.animation.AnimationKeyframe;
import net.minecraft.client.render.animation.PartAnimation;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Function;

public class AnimationEasing {

	public static PartAnimation.Interpolator interpolation(EasingCategories easingCategory, Type type) {
		EasingTypes easing = easingCategory.getEasingType(type);
		return Obsidian.registerInRegistryVanilla(Registries.ANIMATION_CHANNEL_INTERPOLATIONS, easing.getName(),
				(vector3f, f, keyframes, i, j, g) -> easing(vector3f, easing.apply(f), keyframes, i, j, g));
	}

	public static Vec3f easing(Vec3f vec3f, float f, AnimationKeyframe[] keyframes, int i, int j, float g) {
		Vec3f vector3f2 = keyframes[i].transformation();
		Vec3f vector3f3 = keyframes[j].transformation();
		vec3f.set(
				net.minecraft.util.math.MathHelper.lerp(f, vector3f2.getX(), vector3f3.getX()) * g,
				net.minecraft.util.math.MathHelper.lerp(f, vector3f2.getY(), vector3f3.getY()) * g,
				net.minecraft.util.math.MathHelper.lerp(f, vector3f2.getZ(), vector3f3.getZ()) * g
		);
		return vec3f;
	}

	public static float easeInSine(float f) {
		return 1F - net.minecraft.util.math.MathHelper.cos((f * net.minecraft.util.math.MathHelper.PI) / 2F);
	}

	public static float easeOutSine(float f) {
		return net.minecraft.util.math.MathHelper.sin((f * net.minecraft.util.math.MathHelper.PI) / 2F);
	}

	public static float easeInOutSine(float f) {
		return -(net.minecraft.util.math.MathHelper.cos(f * net.minecraft.util.math.MathHelper.PI) - 1F) / 2F;
	}

	public static float easeInQuad(float f) {
		return MathHelper.pow(f, 2);
	}

	public static float easeOutQuad(float f) {
		return 1F - (1F - f) * (1 - f);
	}

	public static float easeInOutQuad(float f) {
		return f < 0.5 ? 2 * f * f : 1 - MathHelper.pow(-2 * f + 2, 2) / 2;
	}

	public static float easeInCubic(float f) {
		return MathHelper.pow(f, 3);
	}

	public static float easeOutCubic(float f) {
		return 1F - MathHelper.pow(1F - f, 3F);
	}

	public static float easeInOutCubic(float f) {
		return f < 0.5 ? 4 * f * f * f : 1 - MathHelper.pow(-2 * f + 2, 3) / 2;
	}

	public static float easeInQuart(float f) {
		return MathHelper.pow(f, 4);
	}

	public static float easeOutQuart(float f) {
		return 1F - MathHelper.pow(1F - f, 4);
	}

	public static float easeInOutQuart(float f) {
		return f < 0.5 ? 8 * f * f * f * f : 1 - MathHelper.pow(-2 * f + 2, 4) / 2;
	}

	public static float easeInQuint(float f) {
		return MathHelper.pow(f, 5);
	}

	public static float easeOutQuint(float f) {
		return 1F - MathHelper.pow(1F - f, 5);
	}

	public static float easeInOutQuint(float f) {
		return f < 0.5 ? 16 * f * f * f * f * f : 1 - MathHelper.pow(-2 * f + 2, 5) / 2;
	}

	public static float easeInExpo(float f) {
		return f == 0 ? 0 : MathHelper.pow(2, 10 * f - 10);
	}

	public static float easeOutExpo(float f) {
		return f == 0 ? 0 : MathHelper.pow(2, 10 * f - 10);
	}

	public static float easeInOutExpo(float f) {
		return f == 0 ? 0 : f == 1 ? 1 : f < 0.5 ? MathHelper.pow(2, 20 * f - 10) / 2
				: (2 - MathHelper.pow(2, -20 * f + 10)) / 2;
	}

	public static float easeInCirc(float f) {
		return 1 - net.minecraft.util.math.MathHelper.sqrt(1F - MathHelper.pow(f, 2));
	}

	public static float easeOutCirc(float f) {
		return net.minecraft.util.math.MathHelper.sqrt(1 - MathHelper.pow(f - 1, 2));
	}

	public static float easeInOutCirc(float f) {
		return f < 0.5 ? (1 - net.minecraft.util.math.MathHelper.sqrt(1 - MathHelper.pow(2 * f, 2))) / 2
				: (net.minecraft.util.math.MathHelper.sqrt(1 - MathHelper.pow(-2 * f + 2, 2)) + 1) / 2;
	}

	public static float easeInBack(float f) {
		float c1 = 1.70158F;
		float c3 = c1 + 1F;
		return c3 * MathHelper.pow(f, 3) - c1 * MathHelper.pow(f, 2);
	}

	public static float easeOutBack(float f) {
		float c1 = 1.70158F;
		float c3 = c1 + 1F;
		return 1F + c3 * MathHelper.pow(f - 1F, 3) + c1 * MathHelper.pow(f - 1, 2);
	}

	public static float easeInOutBack(float f) {
		float c1 = 1.70158F;
		float c2 = c1 + 1F;
		return f < 0.5 ? (MathHelper.pow(2 * f, 2) * ((c2 + 1) * 2 * f - c2)) / 2
				: (MathHelper.pow(2 * f - 2, 2) * ((c2 + 1) * (f * 2 - 2) + c2) + 2) / 2;
	}

	public static float easeInElastic(float f) {
		float c4 = (2F * net.minecraft.util.math.MathHelper.PI) / 3;
		return f == 0 ? 0 : f == 1 ? 1 : -MathHelper.pow(2F, 10F * f - 10F) *
				net.minecraft.util.math.MathHelper.sin((f * 10F - 10.75F) * c4);
	}

	public static float easeOutElastic(float f) {
		float c4 = (2F * net.minecraft.util.math.MathHelper.PI) / 3;
		return f == 0 ? 0 : f == 1 ? 1 : MathHelper.pow(2F, -10F * f) *
				net.minecraft.util.math.MathHelper.sin((f * 10F - 0.75F) * c4) + 1;
	}

	public static float easeInOutElastic(float f) {
		float c5 = (2F * net.minecraft.util.math.MathHelper.PI) / 4.5F;
		return f == 0 ? 0 : f == 1 ? 1 : f < 0.5F ?
				-(MathHelper.pow(2F, 20F * f - 10F) * net.minecraft.util.math.MathHelper.sin((f * 10F - 11.125F) * c5)) / 2
				: (MathHelper.pow(2, -20F * f + 10) * net.minecraft.util.math.MathHelper.sin((20F * f - 11.125F) * c5)) / 2 + 1;
	}

	public static float easeInBounce(float f) {
		return 1 - AnimationEasing.easeOutBounce(1 - f);
	}

	public static float easeOutBounce(float f) {
		float n1 = 7.5625F;
		float d1 = 2.75F;

		if (f < 1 / d1) {
			return n1 * f * f;
		} else if (f < 2 / d1) {
			return n1 * (f -= 1.5F / d1) * f + 0.75F;
		} else if (f < 2.5 / d1) {
			return n1 * (f -= 2.25F / d1) * f + 0.9375F;
		} else {
			return n1 * (f -= 2.625F / d1) * f + 0.984375F;
		}
	}

	public static float easeInOutBounce(float f) {
		return f < 0.5F ? (1 - AnimationEasing.easeOutBounce(1 - 2 * f)) / 2
				: (1 + AnimationEasing.easeOutBounce(2 * f - 1)) / 2;
	}

	public enum Type {
		IN,
		OUT,
		IN_OUT
	}

	public enum EasingTypes {
		//Sine Easings
		EASE_IN_SINE("ease_in_sine", AnimationEasing::easeInSine),
		EASE_OUT_SINE("ease_out_sine", AnimationEasing::easeOutSine),
		EASE_IN_OUT_SINE("ease_in_out_sine", AnimationEasing::easeInOutSine),

		//Quad Easings
		EASE_IN_QUAD("ease_in_quad", AnimationEasing::easeInQuad),
		EASE_OUT_QUAD("ease_out_quad", AnimationEasing::easeOutQuad),
		EASE_IN_OUT_QUAD("ease_in_out_quad", AnimationEasing::easeInOutQuad),

		//Cubic Easings
		EASE_IN_CUBIC("ease_in_cubic", AnimationEasing::easeInCubic),
		EASE_OUT_CUBIC("ease_out_cubic", AnimationEasing::easeOutCubic),
		EASE_IN_OUT_CUBIC("ease_in_out_cubic", AnimationEasing::easeInOutCubic),

		//Quart Easings
		EASE_IN_QUART("ease_in_quart", AnimationEasing::easeInQuart),
		EASE_OUT_QUART("ease_out_quart", AnimationEasing::easeOutQuart),
		EASE_IN_OUT_QUART("ease_in_out_quart", AnimationEasing::easeInOutQuart),

		//Quint Easings
		EASE_IN_QUINT("ease_in_quint", AnimationEasing::easeInQuint),
		EASE_OUT_QUINT("ease_out_quint", AnimationEasing::easeOutQuint),
		EASE_IN_OUT_QUINT("ease_in_out_quint", AnimationEasing::easeInOutQuint),

		//Expo Easings
		EASE_IN_EXPO("ease_in_expo", AnimationEasing::easeInExpo),
		EASE_OUT_EXPO("ease_out_expo", AnimationEasing::easeOutExpo),
		EASE_IN_OUT_EXPO("ease_in_out_expo", AnimationEasing::easeInOutExpo),

		//Circ Easings
		EASE_IN_CIRC("ease_in_circ", AnimationEasing::easeInCirc),
		EASE_OUT_CIRC("ease_out_circ", AnimationEasing::easeOutCirc),
		EASE_IN_OUT_CIRC("ease_in_out_circ", AnimationEasing::easeInOutCirc),

		//Back Easings
		EASE_IN_BACK("ease_in_back", AnimationEasing::easeInBack),
		EASE_OUT_BACK("ease_out_back", AnimationEasing::easeOutBack),
		EASE_IN_OUT_BACK("ease_in_out_back", AnimationEasing::easeInOutBack),

		//Elastic Easings
		EASE_IN_ELASTIC("ease_in_elastic", AnimationEasing::easeInElastic),
		EASE_OUT_ELASTIC("ease_out_elastic", AnimationEasing::easeOutElastic),
		EASE_IN_OUT_ELASTIC("ease_in_out_elastic", AnimationEasing::easeInOutElastic),

		//Bouncing Easings
		EASE_IN_BOUNCE("ease_in_bounce", AnimationEasing::easeInBounce),
		EASE_OUT_BOUNCE("ease_out_bounce", AnimationEasing::easeOutBounce),
		EASE_IN_OUT_BOUNCE("ease_in_out_bounce", AnimationEasing::easeInOutBounce);

		private final Function<Float, Float> easeMethod;
		private final String name;

		EasingTypes(String name, Function<Float, Float> easeMethod) {
			this.name = name;
			this.easeMethod = easeMethod;
		}

		public String getName() {
			return name;
		}

		public float apply(float f) {
			return easeMethod.apply(f);
		}
	}

	public enum EasingCategories {
		SINE(Triple.of(EasingTypes.EASE_IN_SINE, EasingTypes.EASE_OUT_SINE, EasingTypes.EASE_IN_OUT_SINE)),
		QUAD(Triple.of(EasingTypes.EASE_IN_QUAD, EasingTypes.EASE_OUT_QUAD, EasingTypes.EASE_IN_OUT_QUAD)),
		CUBIC(Triple.of(EasingTypes.EASE_IN_CUBIC, EasingTypes.EASE_OUT_CUBIC, EasingTypes.EASE_IN_OUT_CUBIC)),
		QUART(Triple.of(EasingTypes.EASE_IN_QUART, EasingTypes.EASE_OUT_QUART, EasingTypes.EASE_IN_OUT_QUART)),
		QUINT(Triple.of(EasingTypes.EASE_IN_QUINT, EasingTypes.EASE_OUT_QUINT, EasingTypes.EASE_IN_OUT_QUINT)),
		EXPO(Triple.of(EasingTypes.EASE_IN_EXPO, EasingTypes.EASE_OUT_EXPO, EasingTypes.EASE_IN_OUT_EXPO)),
		CIRC(Triple.of(EasingTypes.EASE_IN_CIRC, EasingTypes.EASE_OUT_CIRC, EasingTypes.EASE_IN_OUT_CIRC)),
		BACK(Triple.of(EasingTypes.EASE_IN_BACK, EasingTypes.EASE_OUT_BACK, EasingTypes.EASE_IN_OUT_BACK)),
		ELASTIC(Triple.of(EasingTypes.EASE_IN_ELASTIC, EasingTypes.EASE_OUT_ELASTIC, EasingTypes.EASE_IN_OUT_ELASTIC)),
		BOUNCE(Triple.of(EasingTypes.EASE_IN_BOUNCE, EasingTypes.EASE_OUT_BOUNCE, EasingTypes.EASE_IN_OUT_BOUNCE));

		private final Triple<EasingTypes, EasingTypes, EasingTypes> easeMethod;

		EasingCategories(Triple<EasingTypes, EasingTypes, EasingTypes> easeMethod) {
			this.easeMethod = easeMethod;
		}

		public EasingTypes getEasingType(Type easingType) {
			return switch(easingType) {
				case IN -> easeMethod.getLeft();
				case OUT -> easeMethod.getMiddle();
				case IN_OUT -> easeMethod.getRight();
			};
		}

		public float get(Type easingType, float f) {
			return getEasingType(easingType).apply(f);
		}
	}

}

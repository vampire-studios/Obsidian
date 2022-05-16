package io.github.vampirestudios.obsidian;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.vampirestudios.obsidian.AnimationEasing.EasingCategories;
import io.github.vampirestudios.obsidian.AnimationEasing.Type;
import io.github.vampirestudios.obsidian.utils.MHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Vec3f;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A GUI interface which handles keyboard and mouse callbacks for child GUI elements.
 * The implementation of a parent element can decide whether a child element receives keyboard and mouse callbacks.
 */
@Environment(EnvType.CLIENT)
public record AnimationChannel(AnimationChannel.Target target, Keyframe... keyframes) {
	public static final Codec<AnimationChannel> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Target.CODEC.fieldOf("target").forGetter(AnimationChannel::target),
					Keyframe.CODEC.listOf().fieldOf("target").forGetter(o -> {
						Keyframe[] keyframes = o.keyframes();
						return Arrays.stream(keyframes).collect(Collectors.toList());
					})
			).apply(instance, AnimationChannel::new)
	);

	public AnimationChannel(AnimationChannel.Target target, List<Keyframe> keyframes) {
		this(target, keyframes.toArray(new Keyframe[]{}));
	}

	@Environment(EnvType.CLIENT)
	public interface Interpolation {
		Codec<Interpolation> CODEC = Codec.unit(() -> (vector3f, f, keyframes, i, j, g) -> null);
		Vec3f apply(Vec3f vector3f, float f, Keyframe[] keyframes, int i, int j, float g);
	}

	@Environment(EnvType.CLIENT)
	public static class Interpolations {
		public static final AnimationChannel.Interpolation LINEAR = AnimationEasing::easing;
		public static final AnimationChannel.Interpolation CATMULLROM = Obsidian.registerInRegistryVanilla(
				Obsidian.ANIMATION_CHANNEL_INTERPOLATIONS,
				"catmullrom",
				(vector3f, f, keyframes, i, j, g) -> {
					Vec3f vector3f2 = keyframes[Math.max(0, i - 1)].target();
					Vec3f vector3f3 = keyframes[i].target();
					Vec3f vector3f4 = keyframes[j].target();
					Vec3f vector3f5 = keyframes[Math.min(keyframes.length - 1, j + 1)].target();
					vector3f.set(
							MHelper.catmullrom(f, vector3f2.getX(), vector3f3.getX(), vector3f4.getX(), vector3f5.getX()) * g,
							MHelper.catmullrom(f, vector3f2.getY(), vector3f3.getY(), vector3f4.getY(), vector3f5.getY()) * g,
							MHelper.catmullrom(f, vector3f2.getZ(), vector3f3.getZ(), vector3f4.getZ(), vector3f5.getZ()) * g
					);
					return vector3f;
				}
		);
		public static final Interpolation EASE_IN_SINE = AnimationEasing.interpolation(EasingCategories.SINE, Type.IN);
		public static final Interpolation EASE_OUT_SINE = AnimationEasing.interpolation(EasingCategories.SINE, Type.OUT);
		public static final Interpolation EASE_IN_OUT_SINE = AnimationEasing.interpolation(EasingCategories.SINE, Type.IN_OUT);
		public static final Interpolation EASE_IN_QUAD = AnimationEasing.interpolation(EasingCategories.QUAD, Type.IN);
		public static final Interpolation EASE_OUT_QUAD = AnimationEasing.interpolation(EasingCategories.QUAD, Type.OUT);
		public static final Interpolation EASE_IN_OUT_QUAD = AnimationEasing.interpolation(EasingCategories.QUAD, Type.IN_OUT);
		public static final Interpolation EASE_IN_CUBIC = AnimationEasing.interpolation(EasingCategories.CUBIC, Type.IN);
		public static final Interpolation EASE_OUT_CUBIC = AnimationEasing.interpolation(EasingCategories.CUBIC, Type.OUT);
		public static final Interpolation EASE_IN_OUT_CUBIC = AnimationEasing.interpolation(EasingCategories.CUBIC, Type.IN_OUT);
		public static final Interpolation EASE_IN_QUART = AnimationEasing.interpolation(EasingCategories.QUART, Type.IN);
		public static final Interpolation EASE_OUT_QUART = AnimationEasing.interpolation(EasingCategories.QUART, Type.OUT);
		public static final Interpolation EASE_IN_OUT_QUART = AnimationEasing.interpolation(EasingCategories.QUART, Type.IN_OUT);
		public static final Interpolation EASE_IN_QUINT = AnimationEasing.interpolation(EasingCategories.QUINT, Type.IN);
		public static final Interpolation EASE_OUT_QUINT = AnimationEasing.interpolation(EasingCategories.QUINT, Type.OUT);
		public static final Interpolation EASE_IN_OUT_QUINT = AnimationEasing.interpolation(EasingCategories.QUINT, Type.IN_OUT);
		public static final Interpolation EASE_IN_EXPO = AnimationEasing.interpolation(EasingCategories.EXPO, Type.IN);
		public static final Interpolation EASE_OUT_EXPO = AnimationEasing.interpolation(EasingCategories.EXPO, Type.OUT);
		public static final Interpolation EASE_IN_OUT_EXPO = AnimationEasing.interpolation(EasingCategories.EXPO, Type.IN_OUT);
		public static final Interpolation EASE_IN_CIRC = AnimationEasing.interpolation(EasingCategories.CIRC, Type.IN);
		public static final Interpolation EASE_OUT_CIRC = AnimationEasing.interpolation(EasingCategories.CIRC, Type.OUT);
		public static final Interpolation EASE_IN_OUT_CIRC = AnimationEasing.interpolation(EasingCategories.CIRC, Type.IN_OUT);
		public static final Interpolation EASE_IN_BACK = AnimationEasing.interpolation(EasingCategories.BACK, Type.IN);
		public static final Interpolation EASE_OUT_BACK = AnimationEasing.interpolation(EasingCategories.BACK, Type.OUT);
		public static final Interpolation EASE_IN_OUT_BACK = AnimationEasing.interpolation(EasingCategories.BACK, Type.IN_OUT);
		public static final Interpolation EASE_IN_ELASTIC = AnimationEasing.interpolation(EasingCategories.ELASTIC, Type.IN);
		public static final Interpolation EASE_OUT_ELASTIC = AnimationEasing.interpolation(EasingCategories.ELASTIC, Type.OUT);
		public static final Interpolation EASE_IN_OUT_ELASTIC = AnimationEasing.interpolation(EasingCategories.ELASTIC, Type.IN_OUT);
		public static final Interpolation EASE_IN_BOUNCE = AnimationEasing.interpolation(EasingCategories.BOUNCE, Type.IN);
		public static final Interpolation EASE_OUT_BOUNCE = AnimationEasing.interpolation(EasingCategories.BOUNCE, Type.OUT);
		public static final Interpolation EASE_IN_OUT_BOUNCE = AnimationEasing.interpolation(EasingCategories.BOUNCE, Type.IN_OUT);

		public static void init() {}
	}

	@Environment(EnvType.CLIENT)
	public interface Target {
		Codec<Target> CODEC = Codec.unit(() -> (modelPart, vec3f) ->{});
		void apply(ModelPart modelPart, Vec3f vector3f);
	}

	@Environment(EnvType.CLIENT)
	public static class Targets {
		public static final AnimationChannel.Target POSITION = Obsidian.registerInRegistryVanilla(
				Obsidian.ANIMATION_CHANNEL_TARGETS,
				"position",
				(modelPart, vector3f) -> ((ExpandedModelPart)modelPart).offsetPos(vector3f)
		);
		public static final AnimationChannel.Target ROTATION = Obsidian.registerInRegistryVanilla(
				Obsidian.ANIMATION_CHANNEL_TARGETS,
				"rotation",
				(modelPart, vector3f) -> ((ExpandedModelPart)modelPart).offsetRotation(vector3f)
		);
		public static final AnimationChannel.Target SCALE = Obsidian.registerInRegistryVanilla(
				Obsidian.ANIMATION_CHANNEL_TARGETS,
				"scale",
				(modelPart, vector3f) -> ((ExpandedModelPart)modelPart).offsetScale(vector3f)
		);

		public static void init() {}
	}
}

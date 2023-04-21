package io.github.vampirestudios.obsidian.animation;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.animation.AnimationChannel;

public class AnimationUtils {
	private static final Map<String, AnimationChannel.Interpolation> INTERPOLATORS = new Object2ObjectLinkedOpenHashMap<>();
	private static final Map<AnimationChannel.Interpolation, String> INVERSE_INTERPOLATORS = new Object2ObjectLinkedOpenHashMap<>();

	static {
		registerInterpolation("LINEAR", AnimationChannel.Interpolations.LINEAR);
		registerInterpolation("SPLINE", AnimationChannel.Interpolations.CATMULLROM);
	}

	public static void registerInterpolation(String name, AnimationChannel.Interpolation interpolator) {
		if (INTERPOLATORS.containsKey(name)) {
			throw new IllegalArgumentException(name + " already used as name");
		} else if (INVERSE_INTERPOLATORS.containsKey(interpolator)) {
			throw new IllegalArgumentException("Interpolator already assigned to " + INVERSE_INTERPOLATORS.get(interpolator));
		}

		INTERPOLATORS.put(name, interpolator);
		INVERSE_INTERPOLATORS.put(interpolator, name);
	}

	public static Optional<AnimationChannel.Interpolation> getInterpolatorFromName(String name) {
		return Optional.ofNullable(INTERPOLATORS.get(name));
	}

	public static Optional<String> getNameForInterpolator(AnimationChannel.Interpolation interpolator) {
		return Optional.ofNullable(INVERSE_INTERPOLATORS.get(interpolator));
	}
}
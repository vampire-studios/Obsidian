package io.github.vampirestudios.obsidian.animation;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.entity.animation.Transformation;

import java.util.Map;
import java.util.Optional;

public class AnimationUtils {
	private static final Map<String, Transformation.Interpolation> INTERPOLATORS = new Object2ObjectLinkedOpenHashMap<>();
	private static final Map<Transformation.Interpolation, String> INVERSE_INTERPOLATORS = new Object2ObjectLinkedOpenHashMap<>();

	static {
		registerInterpolation("LINEAR", Transformation.Interpolations.LINEAR);
		registerInterpolation("SPLINE", Transformation.Interpolations.CUBIC);
	}

	public static void registerInterpolation(String name, Transformation.Interpolation interpolator) {
		if (INTERPOLATORS.containsKey(name)) {
			throw new IllegalArgumentException(name + " already used as name");
		} else if (INVERSE_INTERPOLATORS.containsKey(interpolator)) {
			throw new IllegalArgumentException("Interpolator already assigned to " + INVERSE_INTERPOLATORS.get(interpolator));
		}

		INTERPOLATORS.put(name, interpolator);
		INVERSE_INTERPOLATORS.put(interpolator, name);
	}

	public static Optional<Transformation.Interpolation> getInterpolatorFromName(String name) {
		return Optional.ofNullable(INTERPOLATORS.get(name));
	}

	public static Optional<String> getNameForInterpolator(Transformation.Interpolation interpolator) {
		return Optional.ofNullable(INVERSE_INTERPOLATORS.get(interpolator));
	}
}
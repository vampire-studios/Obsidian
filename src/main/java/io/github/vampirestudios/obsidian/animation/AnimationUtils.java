package io.github.vampirestudios.obsidian.animation;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.animation.PartAnimation;

import java.util.Map;
import java.util.Optional;

public class AnimationUtils {
	private static final Map<String, PartAnimation.Interpolator> INTERPOLATORS = new Object2ObjectLinkedOpenHashMap<>();
	private static final Map<PartAnimation.Interpolator, String> INVERSE_INTERPOLATORS = new Object2ObjectLinkedOpenHashMap<>();

	static {
		registerInterpolation("LINEAR", PartAnimation.Interpolators.LINEAR);
		registerInterpolation("SPLINE", PartAnimation.Interpolators.SPLINE);
	}

	public static void registerInterpolation(String name, PartAnimation.Interpolator interpolator) {
		if (INTERPOLATORS.containsKey(name)) {
			throw new IllegalArgumentException(name + " already used as name");
		} else if (INVERSE_INTERPOLATORS.containsKey(interpolator)) {
			throw new IllegalArgumentException("Interpolator already assigned to " + INVERSE_INTERPOLATORS.get(interpolator));
		}

		INTERPOLATORS.put(name, interpolator);
		INVERSE_INTERPOLATORS.put(interpolator, name);
	}

	public static Optional<PartAnimation.Interpolator> getInterpolatorFromName(String name) {
		return Optional.ofNullable(INTERPOLATORS.get(name));
	}

	public static Optional<String> getNameForInterpolator(PartAnimation.Interpolator interpolator) {
		return Optional.ofNullable(INVERSE_INTERPOLATORS.get(interpolator));
	}
}
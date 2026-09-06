package io.github.vampirestudios.obsidian.minecraft;

public final class ChargeMath {
	private ChargeMath() {
	}

	public static float powerMultiplier(ChargeComponent cfg, int chargeTicks) {
		if (cfg == null) return 1.0f;

		int max = Math.max(1, cfg.maxChargeTicks());
		float t = clamp01(chargeTicks / (float) max);

		float shaped = switch (cfg.curve()) {
			case LINEAR -> t;
			case QUADRATIC -> t * t;
			case CUBIC -> t * t * t;
		};

		return lerp(cfg.minPowerMultiplier(), cfg.maxPowerMultiplier(), shaped);
	}

	private static float clamp01(float v) {
		return v < 0 ? 0 : (v > 1 ? 1 : v);
	}

	private static float lerp(float a, float b, float t) {
		return a + (b - a) * t;
	}
}

package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

import java.util.ArrayList;
import java.util.List;

public class AnimationManager {
	private final List<AnimatedParticleEffect> scheduledEffects = new ArrayList<>();

	public void addAnimation(AnimatedParticleEffect effect) {
		scheduledEffects.add(effect);
	}

	public void onTick() {
		scheduledEffects.removeIf(AnimatedParticleEffect::update);
	}

	// Singleton pattern for easy access
	private static final AnimationManager INSTANCE = new AnimationManager();

	public static AnimationManager getInstance() {
		return INSTANCE;
	}
}

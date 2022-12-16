package io.github.vampirestudios.obsidian.animation;

public interface HasAnimationManager {
	default AnimationManager getAnimationManager() {
		return null;
	}
}
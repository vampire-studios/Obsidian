package io.github.vampirestudios.obsidian;

import net.minecraft.util.Util;

import java.util.function.Consumer;

public class AnimationState {
	private static final long STOPPED = Long.MAX_VALUE;
	private long startTime = Long.MAX_VALUE;

	public void start() {
		this.startTime = Util.getMeasuringTimeMs();
	}

	public void startIfStopped() {
		if (!this.isStarted()) this.start();
	}

	public void stop() {
		this.startTime = STOPPED;
	}

	public long startTime() {
		return this.startTime;
	}

	public void ifStarted(Consumer<AnimationState> consumer) {
		if (this.isStarted()) consumer.accept(this);
	}

	private boolean isStarted() {
		return this.startTime != STOPPED;
	}
}

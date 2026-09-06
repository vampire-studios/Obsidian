package io.github.vampirestudios.obsidian;

public final class MultiAtlasState {
	private MultiAtlasState() {
	}

	public static final ThreadLocal<Boolean> MIXED = ThreadLocal.withInitial(() -> false);

	public static void reset() {
		MIXED.set(false);
	}

	public static void markMixed() {
		MIXED.set(true);
	}

	public static boolean isMixed() {
		return MIXED.get();
	}
}

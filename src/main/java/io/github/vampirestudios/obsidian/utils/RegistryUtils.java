package io.github.vampirestudios.obsidian.utils;

import io.github.vampirestudios.obsidian.api.ExtendedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class RegistryUtils {

	private RegistryUtils() {
	}

	// ---- REGISTRY MANIPULATION ----

	public static void unfreeze(Registry<?> registry) {
		((ExtendedRegistry<?>) registry).obsidian$unfreeze();
	}

	@SuppressWarnings("unchecked")
	public static <T> void remove(Registry<T> registry, RegistryKey<T> key) {
		((ExtendedRegistry<T>) registry).obsidian$remove(key);
	}

	@SuppressWarnings("unchecked")
	public static void remove(Registry<?> registry, Identifier id) {
		remove((Registry<Object>) registry, (RegistryKey<Object>) RegistryKey.of(registry.getKey(), id));
	}

}

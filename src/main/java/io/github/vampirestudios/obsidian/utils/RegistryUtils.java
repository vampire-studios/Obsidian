package io.github.vampirestudios.obsidian.utils;

import io.github.vampirestudios.obsidian.api.ExtendedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class RegistryUtils {

	private RegistryUtils() {
	}

	// ---- REGISTRY MANIPULATION ----

	public static void unfreeze(Registry<?> registry) {
		((ExtendedRegistry<?>) registry).obsidian$unfreeze();
	}

	@SuppressWarnings("unchecked")
	public static <T> void remove(Registry<T> registry, ResourceKey<T> key) {
		((ExtendedRegistry<T>) registry).obsidian$remove(key);
	}

	@SuppressWarnings("unchecked")
	public static void remove(Registry<?> registry, ResourceLocation id) {
		remove((Registry<Object>) registry, (ResourceKey<Object>) ResourceKey.create(registry.key(), id));
	}

}

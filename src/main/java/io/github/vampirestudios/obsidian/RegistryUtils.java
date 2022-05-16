package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.api.ExtendedRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public final class RegistryUtils {
    private RegistryUtils() {

    }

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
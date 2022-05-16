package io.github.vampirestudios.obsidian;

import java.util.Map;

public final class ClearUtils {
    private ClearUtils() {

    }

    @SafeVarargs
    public static <T> void clearMaps(T entry, Map<? extends T, ? extends T>... maps) {
        for (var map : maps) {
            map.remove(entry);
            map.entrySet().removeIf(x -> x.getValue() == entry);
        }
    }

    @SafeVarargs
    public static <T> void clearMapKeys(T entry, Map<? extends T, ?>... maps) {
        for (var map : maps) {
            map.remove(entry);
        }
    }

    @SafeVarargs
    public static <T> void clearMapValues(T entry, Map<?, ? extends T>... maps) {
        for (var map : maps) {
            map.entrySet().removeIf(x -> x.getValue() == entry);
        }
    }
}
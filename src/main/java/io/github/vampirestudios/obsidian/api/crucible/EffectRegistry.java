package io.github.vampirestudios.obsidian.api.crucible;

import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;

import java.util.HashMap;
import java.util.Map;

public class EffectRegistry {
    private static final Map<String, Effect> effects = new HashMap<>();

    public static void registerEffect(String id, Effect effect) {
        effects.put(id, effect);
    }

    public static Effect getEffectById(String id) {
        return effects.get(id);
    }

    public static boolean hasEffect(String id) {
        return effects.containsKey(id);
    }
}

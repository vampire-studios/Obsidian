package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

import java.util.Optional;

/**
 * @param time  Time in ticks at which this keyframe should apply */
public record Keyframe(int time, Optional<Double> radius, Optional<Integer> color, Optional<Double> expandRadius,
                       Optional<Boolean> fadeOut) {
}

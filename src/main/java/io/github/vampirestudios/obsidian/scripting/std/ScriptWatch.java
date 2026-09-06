package io.github.vampirestudios.obsidian.scripting.std;

import java.util.List;

/**
 * Represents a watch block:
 *   watch(myVar) { ... }
 *
 * Fires whenever the observable's value changes.
 */
public record ScriptWatch(
		String name,     // observable being watched
		List<String> body, // raw script lines (to be parsed into a Block at runtime)
		ScriptObservable.Scope scope, // GLOBAL or PLAYER
		String when                    // nullable; only meaningful for player scope
) {
}
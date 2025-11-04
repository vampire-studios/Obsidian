package io.github.vampirestudios.obsidian.scripting.std;

/**
 * Represents an observable variable defined in script:
 *   observable myVar = 100;
 */
public record ScriptObservable(
		String name,     // variable name
		String initExpr,  // raw expression string for the initial value
        Scope scope // GLOBAL or PLAYER
) {
	public enum Scope { GLOBAL, PLAYER }
}
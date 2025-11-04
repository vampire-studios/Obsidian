package io.github.vampirestudios.obsidian.scripting.std;

import java.util.List;

public record ScriptRule(
		String when,              // predicate string (e.g., biome.is("x") && ...)
		List<String> body,        // raw action lines (parsed by ObsInterpreter.execA)
		int cadenceTicks,         // 0 = edge-triggered; >0 = paced repeat while predicate true
		int cooldownTicks         // minimum ticks between fires (applies to both modes)
) {
	public static ScriptRule edge(String when, List<String> body, int cooldownTicks) {
		return new ScriptRule(when, body, 0, cooldownTicks);
	}
	public static ScriptRule paced(String when, List<String> body, int cadenceTicks, int cooldownTicks) {
		return new ScriptRule(when, body, Math.max(1, cadenceTicks), cooldownTicks);
	}
}

package io.github.vampirestudios.obsidian;

import java.util.HashMap;
import java.util.Map;

public record SpreaderType(String name) {
	private static final Map<String, SpreaderType> TYPES = new HashMap<>();

	public static final SpreaderType GRASS = get("grass");
	public static final SpreaderType MYCELIUM = get("mycelium");
	public static final SpreaderType CRIMSON = get("crimson");
	public static final SpreaderType WARPED = get("warped");
	public static final SpreaderType REVERT = get("revert");

	public static SpreaderType get(String name) {
		return TYPES.computeIfAbsent(name, SpreaderType::new);
	}

	public String getName() {
		return name;
	}
}
package io.github.vampirestudios.obsidian.scripting.std;

import java.util.List;
import java.util.Map;

public record ScriptCommand(
		String name,
		List<Param> params,
		List<String> actions,
		List<String> aliases,
		Map<String, String> suggests,
		int cooldownTicks,
		String description,
		String permission
) {
	public record Param(String name, Kind kind, boolean required, String defaultLiteral) {
	}

	public enum Kind {
		INTEGER, FLOAT, BOOL, WORD, TEXT, DURATION, TIME, TEAM_COLOR,
		HEX_COLOR, ENTITY, ENTITIES, PLAYER, PLAYERS, GAME_MODE,
		BLOCK_POS, UUID, ROTATION, ANGLE, SWIZZLE, VEC2, VEC3, ENUM
	}
}

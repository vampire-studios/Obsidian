package io.github.vampirestudios.obsidian.api.crucible.skills;

public enum SkillResult {
	SUCCESS,
	ERROR,
	REQUIRES_PREMIUM,
	INVALID_VERSION,
	INVALID_TARGET,
	INVALID_CONFIG,
	MISSING_COMPATIBILITY,
	CONDITION_FAILED;

	private SkillResult() {
	}
}

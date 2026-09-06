package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

public enum EffectType {
	PARTICLE,
	SOUND,
	DAMAGE,
	HEAL,
	BUFF,
	DEBUFF;

	public static EffectType fromString(String type) {
		return switch (type.toLowerCase()) {
			case "particle", "particles" -> PARTICLE;
			case "sound" -> SOUND;
			case "damage" -> DAMAGE;
			case "heal" -> HEAL;
			case "buff" -> BUFF;
			case "debuff" -> DEBUFF;
			default -> throw new IllegalArgumentException("Unknown effect type: " + type);
		};
	}
}

package io.github.vampirestudios.obsidian.api.crucible.skills.effects;

public enum EffectType {
    PARTICLE,
    SOUND;
    // Add more types as needed

    public static EffectType fromString(String type) {
        return switch (type.toLowerCase()) {
            case "particle" -> PARTICLE;
            case "sound" -> SOUND;
            default -> throw new IllegalArgumentException("Unknown effect type: " + type);
        };
    }
}

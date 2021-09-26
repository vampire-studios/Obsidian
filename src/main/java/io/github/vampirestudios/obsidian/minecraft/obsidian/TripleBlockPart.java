package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.util.StringIdentifiable;

public enum TripleBlockPart implements StringIdentifiable {
    UPPER,
    MIDDLE,
    LOWER;

    @Override
    public String toString() {
        return this.asString();
    }

    @Override
    public String asString() {
        return switch (this) {
            case UPPER -> "upper";
            case MIDDLE -> "middle";
            default -> "lower";
        };
    }
}
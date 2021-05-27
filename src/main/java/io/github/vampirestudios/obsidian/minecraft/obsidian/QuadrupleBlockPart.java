package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.util.StringIdentifiable;

public enum QuadrupleBlockPart implements StringIdentifiable {
    UPPER,
    UPPER_MIDDLE,
    LOWER_MIDDLE,
    LOWER;

    @Override
    public String toString() {
        return this.asString();
    }

    @Override
    public String asString() {
        return switch (this) {
            case UPPER -> "upper";
            case UPPER_MIDDLE -> "upper_middle";
            case LOWER_MIDDLE -> "lower_middle";
            default -> "lower";
        };
    }
}
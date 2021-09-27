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
        switch (this) {
            case UPPER: return "upper";
            case UPPER_MIDDLE: return "upper_middle";
            case LOWER_MIDDLE: return "lower_middle";
            default: return "lower";
        }
    }
}
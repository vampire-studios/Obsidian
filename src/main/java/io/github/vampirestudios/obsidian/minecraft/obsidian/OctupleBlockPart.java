package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.util.StringIdentifiable;

public enum OctupleBlockPart implements StringIdentifiable {
    TOP,
    UPPER,
    UPPER_MIDDLE,
    MIDDLE_UPPER,
    MIDDLE_LOWER,
    LOWER_MIDDLE,
    LOWER,
    BOTTOM;

    @Override
    public String toString() {
        return this.asString();
    }

    @Override
    public String asString() {
        switch (this) {
            case TOP: return "top";
            case UPPER: return "upper";
            case UPPER_MIDDLE: return "upper_middle";
            case MIDDLE_UPPER: return "middle_upper";
            case MIDDLE_LOWER: return "middle_lower";
            case LOWER_MIDDLE: return "lower_middle";
            case LOWER: return "lower";
            default: return "bottom";
        }
    }
}
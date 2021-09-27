package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.util.StringIdentifiable;

public enum UndecupleBlockPart implements StringIdentifiable {
    TOP,
    UPPER,
    UPPER_MIDDLE,
    MIDDLE_TOP,
    MIDDLE_UPPER,
    MIDDLE,
    MIDDLE_LOWER,
    MIDDLE_BOTTOM,
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
            case MIDDLE_TOP: return "middle_top";
            case MIDDLE_UPPER: return "middle_upper";
            case MIDDLE: return "middle";
            case MIDDLE_LOWER: return "middle_lower";
            case MIDDLE_BOTTOM: return "middle_bottom";
            case LOWER_MIDDLE: return "lower_middle";
            case LOWER: return "lower";
            default: return "bottom";
        }
    }
}
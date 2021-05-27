package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.util.StringIdentifiable;

public enum QuattuordecupleBlockPart implements StringIdentifiable {
    TOP,
    UPPER,
    UPPER_MIDDLE,
    MIDDLE_TOP,
    MIDDLE_UPPER,
    CENTER_TOP,
    CENTER_UPPER,
    CENTER_LOWER,
    CENTER_BOTTOM,
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
        return switch (this) {
            case TOP -> "top";
            case UPPER -> "upper";
            case UPPER_MIDDLE -> "upper_middle";
            case MIDDLE_TOP -> "middle_top";
            case MIDDLE_UPPER -> "middle_upper";
            case CENTER_TOP -> "center_top";
            case CENTER_UPPER -> "center_upper";
            case CENTER_LOWER -> "center_lower";
            case CENTER_BOTTOM -> "center_bottom";
            case MIDDLE_LOWER -> "middle_lower";
            case MIDDLE_BOTTOM -> "middle_bottom";
            case LOWER_MIDDLE -> "lower_middle";
            case LOWER -> "lower";
            default -> "bottom";
        };
    }
}
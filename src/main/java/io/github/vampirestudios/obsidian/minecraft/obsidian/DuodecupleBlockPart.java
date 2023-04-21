package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.util.StringRepresentable;

public enum DuodecupleBlockPart implements StringRepresentable {
    TOP,
    UPPER,
    UPPER_MIDDLE,
    MIDDLE_TOP,
    MIDDLE_UPPER,
    CENTER_TOP,
    CENTER_BOTTOM,
    MIDDLE_LOWER,
    MIDDLE_BOTTOM,
    LOWER_MIDDLE,
    LOWER,
    BOTTOM;

    @Override
    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return switch (this) {
            case TOP -> "top";
            case UPPER -> "upper";
            case UPPER_MIDDLE -> "upper_middle";
            case MIDDLE_TOP -> "middle_top";
            case MIDDLE_UPPER -> "middle_upper";
            case CENTER_TOP -> "center_top";
            case CENTER_BOTTOM -> "center_bottom";
            case MIDDLE_LOWER -> "middle_lower";
            case MIDDLE_BOTTOM -> "middle_bottom";
            case LOWER_MIDDLE -> "lower_middle";
            case LOWER -> "lower";
            default -> "bottom";
        };
    }
}
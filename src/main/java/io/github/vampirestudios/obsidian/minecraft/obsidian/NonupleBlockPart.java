package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.util.StringRepresentable;

public enum NonupleBlockPart implements StringRepresentable {
    TOP,
    UPPER,
    UPPER_MIDDLE,
    MIDDLE_UPPER,
    MIDDLE,
    MIDDLE_LOWER,
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
            case MIDDLE_UPPER -> "middle_upper";
            case MIDDLE -> "middle";
            case MIDDLE_LOWER -> "middle_lower";
            case LOWER_MIDDLE -> "lower_middle";
            case LOWER -> "lower";
            default -> "bottom";
        };
    }
}
package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.util.StringRepresentable;

public enum SextupleBlockPart implements StringRepresentable {
    TOP,
    UPPER,
    UPPER_MIDDLE,
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
            case LOWER_MIDDLE -> "lower_middle";
            case LOWER -> "lower";
            default -> "bottom";
        };
    }
}
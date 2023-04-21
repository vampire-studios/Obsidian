package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.util.StringRepresentable;

public enum QuintupleBlockPart implements StringRepresentable {
    UPPER,
    UPPER_MIDDLE,
    MIDDLE,
    LOWER_MIDDLE,
    LOWER;

    @Override
    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return switch (this) {
            case UPPER -> "upper";
            case UPPER_MIDDLE -> "upper_middle";
            case MIDDLE -> "middle";
            case LOWER_MIDDLE -> "lower_middle";
            default -> "lower";
        };
    }
}
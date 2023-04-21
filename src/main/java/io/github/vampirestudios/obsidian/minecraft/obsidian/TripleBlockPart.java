package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.util.StringRepresentable;

public enum TripleBlockPart implements StringRepresentable {
    UPPER,
    MIDDLE,
    LOWER;

    @Override
    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return switch (this) {
            case UPPER -> "upper";
            case MIDDLE -> "middle";
            default -> "lower";
        };
    }
}
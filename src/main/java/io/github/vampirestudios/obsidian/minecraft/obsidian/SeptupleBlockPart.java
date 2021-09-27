package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.util.StringIdentifiable;

public enum SeptupleBlockPart implements StringIdentifiable {
    TOP,
    UPPER,
    UPPER_MIDDLE,
    MIDDLE,
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
            case MIDDLE: return "middle";
            case LOWER_MIDDLE: return "lower_middle";
            case LOWER: return "lower";
            default: return "bottom";
        }
    }
}
package io.github.vampirestudios.obsidian.utils;

import net.minecraft.util.Mth;

/**
 * An immutable vector composed of 2 ints.
 */
public record Vec2i(int x, int y) {
    public static final Vec2i ZERO = new Vec2i(0, 0);
    public static final Vec2i SOUTH_EAST_UNIT = new Vec2i(1, 1);
    public static final Vec2i EAST_UNIT = new Vec2i(1, 0);
    public static final Vec2i WEST_UNIT = new Vec2i(-1, 0);
    public static final Vec2i SOUTH_UNIT = new Vec2i(0, 1);
    public static final Vec2i NORTH_UNIT = new Vec2i(0, -1);
    public static final Vec2i MAX_SOUTH_EAST = new Vec2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final Vec2i MIN_SOUTH_EAST = new Vec2i(Integer.MIN_VALUE, Integer.MIN_VALUE);

    public Vec2i multiply(int value) {
        return new Vec2i(this.x * value, this.y * value);
    }

    public float dot(Vec2i vec) {
        return this.x * vec.x + this.y * vec.y;
    }

    public Vec2i add(Vec2i vec) {
        return new Vec2i(this.x + vec.x, this.y + vec.y);
    }

    public Vec2i add(int value) {
        return new Vec2i(this.x + value, this.y + value);
    }

    public boolean equals(Vec2i other) {
        return this.x == other.x && this.y == other.y;
    }

    public float length() {
        return Mth.sqrt(this.x * this.x + this.y * this.y);
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public float distanceSquared(Vec2i vec) {
        float f = vec.x - this.x;
        float g = vec.y - this.y;
        return f * f + g * g;
    }

    public Vec2i negate() {
        return new Vec2i(-this.x, -this.y);
    }

    public Vec2i copy() {
        return new Vec2i(this.x, this.y);
    }
}

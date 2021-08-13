package io.github.vampirestudios.obsidian.utils;

public class Triple <A, B, C> extends Pair<A, B>{
    public final C third;

    public Triple(A first, B second, C third) {
        super(first, second);
        this.third = third;
    }
}
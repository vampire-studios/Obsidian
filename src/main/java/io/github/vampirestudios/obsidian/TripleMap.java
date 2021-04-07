package io.github.vampirestudios.obsidian;

public class TripleMap<A, B, C> {

    private final A one;
    private final B two;
    private final C three;

    public TripleMap(A oneIn, B twoIn, C threeIn) {
        one = oneIn;
        two = twoIn;
        three = threeIn;
    }

    public A getOne() {
        return one;
    }

    public B getTwo() {
        return two;
    }

    public C getThree() {
        return three;
    }

}
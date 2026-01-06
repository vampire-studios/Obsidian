package io.github.vampirestudios.obsidian;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.github.vampirestudios.obsidian.mixins.ExtraCodecsAccessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItematicCodecs {
    public static final Codec<Float> NON_NEGATIVE_FLOAT = Codec.FLOAT.validate(value -> {
        if (value >= 0 && value <= Float.MAX_VALUE) {
            return DataResult.success(value);
        }
        return DataResult.error(() -> "Value must be non-negative: " + value);
    });
    public static final Codec<Double> NON_NEGATIVE_DOUBLE = Codec.DOUBLE.validate(value -> {
        if (value >= 0 && value <= Double.MAX_VALUE) {
            return DataResult.success(value);
        }
        return DataResult.error(() -> "Value must be non-negative: " + value);
    });

    private ItematicCodecs() {}

    public static Codec<Integer> index(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive: " + size);
        }
        return Codec.INT.validate(i -> {
            if (i >= 0 && i <= size) {
                return DataResult.success(i);
            }
            return DataResult.error(() -> "Index must be non-negative and less than " + size + ": " + i);
        });
    }

    public static <T> Codec<Set<T>> setCodec(Codec<T> codec) {
        return new SetCodec<>(codec);
    }

    public static Codec<Float> positiveFloat(float maxInclusive) {
        if (maxInclusive <= 0.0f) {
            throw new IllegalArgumentException("maxInclusive must be positive, got " + maxInclusive + " instead");
        }
        return ExtraCodecsAccessor.callFloatRangeMinInclusiveWithMessage(0.0f, maxInclusive, value -> "Value must be positive and at most " + maxInclusive + ": " + value);
    }

    private static class SetCodec<E> implements Codec<Set<E>> {
        private final Codec<List<E>> listCodec;

        private SetCodec(Codec<E> codec) {
            this.listCodec = codec.listOf();
        }

        @Override
        public <T> DataResult<Pair<Set<E>, T>> decode(DynamicOps<T> ops, T input) {
            return this.listCodec.decode(ops, input)
                .flatMap(pair -> {
                    List<E> elements = pair.getFirst();
                    Set<E> set = new HashSet<>();
                    Set<E> duplicates = new HashSet<>();
                    for (E element : elements) {
                        if (!set.add(element)) {
                            duplicates.add(element);
                        }
                    }
                    if (!duplicates.isEmpty()) {
                        return DataResult.error(() -> "Set contained duplicate entries: " + duplicates.stream().map(E::toString).collect(Collectors.joining(", ")));
                    }
                    return DataResult.success(Pair.of(set, pair.getSecond()));
                });
        }

        @Override
        public <T> DataResult<T> encode(Set<E> input, DynamicOps<T> ops, T prefix) {
            return this.listCodec.encode(new ArrayList<>(input), ops, prefix);
        }
    }
}
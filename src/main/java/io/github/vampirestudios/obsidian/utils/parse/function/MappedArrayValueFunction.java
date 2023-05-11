package io.github.vampirestudios.obsidian.utils.parse.function;

import io.github.vampirestudios.obsidian.utils.parse.value.MappedArrayValue;

import java.util.function.Function;

@FunctionalInterface
public interface MappedArrayValueFunction<V, T> extends Function<MappedArrayValue<V>, T>
{
}
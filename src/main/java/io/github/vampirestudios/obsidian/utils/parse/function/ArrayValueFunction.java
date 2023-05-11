package io.github.vampirestudios.obsidian.utils.parse.function;

import io.github.vampirestudios.obsidian.utils.parse.value.ArrayValue;

import java.util.function.Function;

@FunctionalInterface
public interface ArrayValueFunction<T> extends Function<ArrayValue, T>
{
}
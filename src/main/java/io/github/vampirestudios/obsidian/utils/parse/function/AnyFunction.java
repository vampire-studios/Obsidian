package io.github.vampirestudios.obsidian.utils.parse.function;

import io.github.vampirestudios.obsidian.utils.parse.value.Any;

import java.util.function.Function;

@FunctionalInterface
public interface AnyFunction<T> extends Function<Any, T>
{
}
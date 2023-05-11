package io.github.vampirestudios.obsidian.utils.parse.function;

import java.util.function.Function;

@FunctionalInterface
public interface StringFunction<T> extends Function<String, T>
{
}
package io.github.vampirestudios.obsidian.utils.parse.function;

import io.github.vampirestudios.obsidian.utils.parse.value.ObjValue;

import java.util.function.Function;

@FunctionalInterface
public interface ObjValueFunction<T> extends Function<ObjValue, T>
{
}
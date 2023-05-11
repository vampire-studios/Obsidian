package io.github.vampirestudios.obsidian.utils.parse.function;

import com.google.gson.JsonArray;

import java.util.function.Function;

@FunctionalInterface
public interface JsonArrayFunction<T> extends Function<JsonArray, T>
{
}
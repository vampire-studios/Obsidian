package io.github.vampirestudios.obsidian.utils.parse.function;

import com.google.gson.JsonObject;

import java.util.function.Function;

@FunctionalInterface
public interface JsonObjectFunction<T> extends Function<JsonObject, T>
{
}
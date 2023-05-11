package io.github.vampirestudios.obsidian.utils.parse.function;

import com.google.gson.JsonElement;

import java.util.function.Function;

@FunctionalInterface
public
interface JsonElementFunction<T> extends Function<JsonElement, T>
{
}
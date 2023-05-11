package io.github.vampirestudios.obsidian.utils.parse.function;

import com.google.gson.JsonElement;

import java.util.function.Consumer;

@FunctionalInterface
public
interface JsonElementConsumer extends Consumer<JsonElement>
{
}
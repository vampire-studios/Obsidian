package io.github.vampirestudios.obsidian.utils.parse.function;

import com.google.gson.JsonObject;

import java.util.function.Consumer;

@FunctionalInterface
public interface JsonObjectConsumer extends Consumer<JsonObject>
{
}
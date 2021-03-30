package io.github.vampirestudios.obsidian.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.function.Function;

@FunctionalInterface
public interface SimpleStringDeserializer<T> extends Function<String, T>, JsonDeserializer<T> {
    default T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return this.apply(json.getAsString());
    }
}
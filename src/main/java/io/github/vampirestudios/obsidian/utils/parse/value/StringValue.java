package io.github.vampirestudios.obsidian.utils.parse.value;

import io.github.vampirestudios.obsidian.utils.parse.function.StringFunction;

import java.util.function.Consumer;

public interface StringValue
{
    void handle(Consumer<String> value);

    String getAsString();

    default <T> MappedValue<T> map(StringFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(getAsString()));
    }
}
package io.github.vampirestudios.obsidian.utils.parse.value;

import io.github.vampirestudios.obsidian.utils.parse.function.BooleanFunction;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

public interface BooleanValue
{
    void handle(BooleanConsumer value);

    boolean getAsBoolean();

    default <T> MappedValue<T> map(BooleanFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(getAsBoolean()));
    }
}
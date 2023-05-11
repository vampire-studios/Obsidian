package io.github.vampirestudios.obsidian.utils.parse.value;

import io.github.vampirestudios.obsidian.utils.parse.function.FloatFunction;
import it.unimi.dsi.fastutil.floats.FloatConsumer;

public interface FloatValue
{
    void handle(FloatConsumer value);

    FloatValue min(float min);

    FloatValue range(float min, float maxExclusive);

    float getAsFloat();

    default <T> MappedValue<T> map(FloatFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(getAsFloat()));
    }
}
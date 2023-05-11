package io.github.vampirestudios.obsidian.utils.parse.value;

import com.google.gson.JsonElement;
import io.github.vampirestudios.obsidian.utils.parse.function.AnyFunction;
import io.github.vampirestudios.obsidian.utils.parse.function.JsonElementConsumer;
import io.github.vampirestudios.obsidian.utils.parse.function.JsonElementFunction;

import java.util.function.Consumer;

public interface Any
{
    ObjValue obj();

    ArrayValue array();

    StringValue string();

    IntValue intValue();

    LongValue longValue();

    FloatValue floatValue();

    DoubleValue doubleValue();

    BooleanValue bool();

    Any ifObj(Consumer<ObjValue> visitor);

    Any ifArray(Consumer<ArrayValue> visitor);

    Any ifString(Consumer<StringValue> visitor);

    Any ifInteger(Consumer<IntValue> visitor);

    Any ifLong(Consumer<LongValue> visitor);

    Any ifFloat(Consumer<FloatValue> visitor);

    Any ifDouble(Consumer<DoubleValue> visitor);

    Any ifBool(Consumer<BooleanValue> visitor);

    void typeError();

    void raw(JsonElementConsumer visitor);

    JsonElement get();

    default <T> MappedValue<T> map(JsonElementFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(get()));
    }

    default <T> MappedValue<T> map(AnyFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(this));
    }
}
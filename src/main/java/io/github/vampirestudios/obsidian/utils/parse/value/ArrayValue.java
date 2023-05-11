package io.github.vampirestudios.obsidian.utils.parse.value;

import com.google.gson.JsonArray;
import io.github.vampirestudios.obsidian.utils.parse.function.ArrayValueFunction;
import io.github.vampirestudios.obsidian.utils.parse.function.IntObjBiConsumer;
import io.github.vampirestudios.obsidian.utils.parse.function.JsonArrayConsumer;
import io.github.vampirestudios.obsidian.utils.parse.function.JsonArrayFunction;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public interface ArrayValue
{
    void forEach(IntObjBiConsumer<Any> visitor);

    void collect(Consumer<Stream<Any>> collector);

    <T> MappedArrayValue<T> map(Function<Any, T> mapping);

    <T> T flatMap(Function<Stream<Any>, T> collector);

    <T> MappedValue<T[]> flatten(Function<Any, T> mapping, IntFunction<T[]> factory);

    ArrayValue notEmpty();

    ArrayValue atLeast(int min);

    ArrayValue between(int min, int maxExclusive);

    JsonArray getAsJsonArray();

    void raw(JsonArrayConsumer value);

    default <T> MappedValue<T> unwrapRaw(JsonArrayFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(getAsJsonArray()));
    }

    default <T> MappedValue<T> mapWhole(ArrayValueFunction<T> mapping)
    {
        return MappedValue.of(mapping.apply(this));
    }

    default MappedArrayValue<IntValue> ints()
    {
        return this.map(Any::intValue);
    }

    default MappedArrayValue<LongValue> longs()
    {
        return this.map(Any::longValue);
    }

    default MappedArrayValue<FloatValue> floats()
    {
        return this.map(Any::floatValue);
    }

    default MappedArrayValue<DoubleValue> doubles()
    {
        return this.map(Any::doubleValue);
    }

    default MappedArrayValue<BooleanValue> booleans()
    {
        return this.map(Any::bool);
    }

    default MappedArrayValue<StringValue> strings()
    {
        return this.map(Any::string);
    }
}

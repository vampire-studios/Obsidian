package io.github.vampirestudios.obsidian.api_new;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.SimpleMapCodec;
import net.minecraft.block.BlockState;

import java.util.stream.Stream;

public class TranslatableName {

    public static final PrimitiveCodec<TranslatableName> codec = new PrimitiveCodec<TranslatableName>() {
        @Override
        public <T> DataResult<TranslatableName> read(DynamicOps<T> ops, T input) {
            return DataResult.success(new TranslatableName(ops.getStringValue(input).get().orThrow(), ops.getStringValue(input).get().orThrow()));
        }

        @Override
        public <T> T write(DynamicOps<T> ops, TranslatableName value) {
            ops.createString(value.language_id);
            ops.createString(value.translated_name);
            return value;
        }
    };
    public static final MapCodec<TranslatableName> MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codec.STRING.fieldOf("language_id").forGetter((weather) -> {
            return weather.language_id;
        }), Codec.STRING.fieldOf("translated_name").forGetter((weather) -> {
            return weather.translated_name;
        })).apply(instance, TranslatableName::new);
    });

    public static final SimpleMapCodec<String, String> map = Codec.simpleMap(
            Codec.STRING,
            Codec.STRING,
            Keyable.forStrings(() -> Stream.of(Codec.STRING.fieldOf("")))
    );

    public String language_id;
    public String translated_name;

    public TranslatableName(String language_id, String translated_name) {
        this.language_id = language_id;
        this.translated_name = translated_name;
    }

}

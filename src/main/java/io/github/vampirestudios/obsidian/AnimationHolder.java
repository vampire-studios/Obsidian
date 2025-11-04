package io.github.vampirestudios.obsidian;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

import java.util.Map;

public record AnimationHolder(float length, boolean looping, Map<String, Map<TargetType, Map<String, KeyframeHolder>>> bones) {

    public static final Codec<String> TIMESTAMP_CODEC = Codec.STRING.validate(value -> {
        try {
            float parsedValue = Float.parseFloat(value);
            return DataResult.success(Float.toString(parsedValue));
        }
        catch (NumberFormatException exception) {
            return DataResult.error(() -> STR."Timestamp must be a float: \{value}");
        }
    });

    public static final Codec<AnimationHolder> CODEC = RecordCodecBuilder.create(instance -> {
        var lengthFloat = ExtraCodecs.POSITIVE_FLOAT.fieldOf("length").forGetter(AnimationHolder::length);
        var loopingBool = Codec.BOOL.fieldOf("looping").forGetter(AnimationHolder::looping);

        var timestamps = Codec.unboundedMap(TIMESTAMP_CODEC, KeyframeHolder.CODEC);
        var targets = Codec.simpleMap(TargetType.CODEC, timestamps, StringRepresentable.keys(TargetType.values()));
        var bones = Codec.unboundedMap(Codec.STRING, targets.codec()).fieldOf("bones").forGetter(AnimationHolder::bones);

        var group = instance.group(lengthFloat, loopingBool, bones);
        return group.apply(instance, AnimationHolder::new);
    });

    public AnimationHolder(float length, boolean looping, Map<String, Map<TargetType, Map<String, KeyframeHolder>>> bones) {
        this.length = length;
        this.looping = looping;
        this.bones = ImmutableMap.copyOf(bones);
    }
}
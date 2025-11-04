package io.github.vampirestudios.obsidian;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public record KeyframeHolder(InterpolationType interpolation, Vector3f transformation) {

    public static final Codec<KeyframeHolder> CODEC = RecordCodecBuilder.create(instance -> {
        var interpolation = InterpolationType.CODEC.fieldOf("interpolation").forGetter(KeyframeHolder::interpolation);
        var transformation = ExtraCodecs.VECTOR3F.fieldOf("value").forGetter(KeyframeHolder::transformation);

        return instance.group(interpolation, transformation).apply(instance, KeyframeHolder::new);
    });

    public KeyframeHolder(InterpolationType interpolation, Vector3f transformation) {
        this.interpolation = interpolation;
        this.transformation = transformation;
    }
}
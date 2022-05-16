package io.github.vampirestudios.obsidian;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public record Keyframe(float timestamp, Vec3f target, AnimationChannel.Interpolation interpolation) {
	public static final Codec<Keyframe> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.FLOAT.fieldOf("timestamp").forGetter(Keyframe::timestamp),
					Vec3f.CODEC.fieldOf("target").forGetter(Keyframe::target),
					AnimationChannel.Interpolation.CODEC.fieldOf("interpolation").forGetter(Keyframe::interpolation)
			).apply(instance, Keyframe::new)
	);
}

package io.github.vampirestudios.obsidian;

import com.mojang.serialization.Codec;
import net.minecraft.client.animation.AnimationChannel.Interpolation;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.util.StringRepresentable;

public enum InterpolationType implements StringRepresentable {
	LINEAR("linear", Interpolations.LINEAR),
	CATMULLROM("catmullrom", Interpolations.CATMULLROM);

	public static final Codec<InterpolationType> CODEC = StringRepresentable.fromEnum(InterpolationType::values);

	private final String name;
	private final Interpolation interpolation;

	InterpolationType(String name, Interpolation interpolation) {
		this.name = name;
		this.interpolation = interpolation;
	}

	public Interpolation getInterpolationFromType() {
		return this.interpolation;
	}

	public String getSerializedName() {
		return this.name;
	}
}
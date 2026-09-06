package io.github.vampirestudios.obsidian.registry.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.vampirestudios.obsidian.registry.components.actionPos.ActionPosition;
import net.minecraft.resources.Identifier;

public record Attraction(Identifier target, ActionPosition towards, int radius, float speed, boolean enabled) {
	public static final Codec<Attraction> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.fieldOf("target").forGetter(Attraction::target),
			ActionPosition.CODEC.fieldOf("towards").forGetter(Attraction::towards),
			Codec.INT.fieldOf("radius").forGetter(Attraction::radius),
			Codec.FLOAT.fieldOf("speed").forGetter(Attraction::speed),
			Codec.BOOL.optionalFieldOf("enabled", true).forGetter(Attraction::enabled)
	).apply(i, Attraction::new));
}
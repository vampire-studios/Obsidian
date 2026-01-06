package io.github.vampirestudios.obsidian.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record ScopeComponent(
		float zoomMultiplier,          // multiply FOV by this while scoping (e.g. 0.10–0.25)
		Identifier overlayTexture, // e.g. "minecraft:textures/misc/spyglass_scope.png"
		int useDurationTicks
) {
	public static final Codec<ScopeComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.FLOAT.optionalFieldOf("zoom_multiplier", 0.1f).forGetter(ScopeComponent::zoomMultiplier),
			Identifier.CODEC.optionalFieldOf(
					"overlay_texture",
					Identifier.withDefaultNamespace("textures/misc/spyglass_scope.png")
			).forGetter(ScopeComponent::overlayTexture),
			Codec.INT.optionalFieldOf("use_duration_ticks", 72000).forGetter(ScopeComponent::useDurationTicks)
	).apply(i, ScopeComponent::new));
	public static final StreamCodec<ByteBuf, ScopeComponent> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT,
			ScopeComponent::zoomMultiplier,
			Identifier.STREAM_CODEC,
			ScopeComponent::overlayTexture,
			ByteBufCodecs.VAR_INT,
			ScopeComponent::useDurationTicks,
			ScopeComponent::new
	);
}

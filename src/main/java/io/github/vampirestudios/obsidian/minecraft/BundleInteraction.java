package io.github.vampirestudios.obsidian.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BundleInteraction(int numViewableSlots) {
	public static final BundleInteraction DEFAULT = new BundleInteraction(12);

	public static final Codec<BundleInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("num_viewable_slots", 12).forGetter(s -> s.numViewableSlots)
	).apply(instance, BundleInteraction::new));

	public static final StreamCodec<ByteBuf, BundleInteraction> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			BundleInteraction::numViewableSlots,
			BundleInteraction::new
	);
}

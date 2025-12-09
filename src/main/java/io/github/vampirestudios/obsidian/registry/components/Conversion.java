package io.github.vampirestudios.obsidian.registry.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;

public record Conversion(List<Identifier> from, Identifier to) {
	public static final Codec<Conversion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.listOf().fieldOf("from").forGetter(Conversion::from),
			Identifier.CODEC.fieldOf("to").forGetter(Conversion::to)
	).apply(instance, Conversion::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, Conversion> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
			Conversion::from,
			Identifier.STREAM_CODEC,
			Conversion::to,
			Conversion::new
	);
}

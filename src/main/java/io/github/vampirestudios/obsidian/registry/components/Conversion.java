package io.github.vampirestudios.obsidian.registry.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record Conversion(List<ResourceLocation> from, ResourceLocation to) {
	public static final Codec<Conversion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.listOf().fieldOf("from").forGetter(Conversion::from),
			ResourceLocation.CODEC.fieldOf("to").forGetter(Conversion::to)
	).apply(instance, Conversion::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, Conversion> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
			Conversion::from,
			ResourceLocation.STREAM_CODEC,
			Conversion::to,
			Conversion::new
	);
}

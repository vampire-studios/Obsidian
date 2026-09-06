package io.github.vampirestudios.obsidian.api.crucible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record AugmentSlotEntry(String type, Optional<String> augmentId) {

	public static final Codec<AugmentSlotEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.STRING.fieldOf("type").forGetter(AugmentSlotEntry::type),
			Codec.STRING.optionalFieldOf("augment").forGetter(AugmentSlotEntry::augmentId)
	).apply(i, AugmentSlotEntry::new));

	public static final StreamCodec<FriendlyByteBuf, AugmentSlotEntry> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, AugmentSlotEntry::type,
			ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), AugmentSlotEntry::augmentId,
			AugmentSlotEntry::new
	);

	public boolean isEmpty() {
		return augmentId.isEmpty();
	}

	public boolean isFilled() {
		return augmentId.isPresent();
	}

	public AugmentSlotEntry withAugment(String id) {
		return new AugmentSlotEntry(type, Optional.of(id));
	}

	public AugmentSlotEntry cleared() {
		return new AugmentSlotEntry(type, Optional.empty());
	}
}

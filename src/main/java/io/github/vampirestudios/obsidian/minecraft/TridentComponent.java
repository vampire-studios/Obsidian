package io.github.vampirestudios.obsidian.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record TridentComponent(
		float throwPower,       // velocity when thrown, vanilla default is 2.5
		float damage,           // base damage on hit, vanilla default is 8.0
		int piercing,           // how many entities to pierce through, 0 = none
		boolean noGravity,      // whether the thrown trident ignores gravity
		boolean disappearsOnHit, // consumed on entity hit instead of being pickable
		Optional<Identifier> throwSound // custom throw sound, empty = vanilla trident throw
) {
	public static final Codec<TridentComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.FLOAT.optionalFieldOf("throw_power", 2.5f).forGetter(TridentComponent::throwPower),
			Codec.FLOAT.optionalFieldOf("damage", 8.0f).forGetter(TridentComponent::damage),
			Codec.INT.optionalFieldOf("piercing", 0).forGetter(TridentComponent::piercing),
			Codec.BOOL.optionalFieldOf("no_gravity", false).forGetter(TridentComponent::noGravity),
			Codec.BOOL.optionalFieldOf("disappears_on_hit", false).forGetter(TridentComponent::disappearsOnHit),
			Identifier.CODEC.optionalFieldOf("throw_sound").forGetter(TridentComponent::throwSound)
	).apply(i, TridentComponent::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, TridentComponent> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT, TridentComponent::throwPower,
			ByteBufCodecs.FLOAT, TridentComponent::damage,
			ByteBufCodecs.VAR_INT, TridentComponent::piercing,
			ByteBufCodecs.BOOL, TridentComponent::noGravity,
			ByteBufCodecs.BOOL, TridentComponent::disappearsOnHit,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TridentComponent::throwSound,
			TridentComponent::new
	);
}

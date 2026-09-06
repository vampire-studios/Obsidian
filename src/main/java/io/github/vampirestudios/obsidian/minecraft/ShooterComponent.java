package io.github.vampirestudios.obsidian.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record ShooterComponent(
		Identifier projectileEntity, // entity type id to spawn
		float power,                       // base velocity multiplier
		float inaccuracy,
		int cooldownTicks,
		boolean requiresAmmo,
		AmmoSpec ammo,                     // item-or-tag ammo selector
		boolean consumeAmmo,
		Identifier shootSound         // sound event id
) {
	public static final Codec<ShooterComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.fieldOf("projectile_entity").forGetter(ShooterComponent::projectileEntity),
			Codec.FLOAT.optionalFieldOf("power", 3.0f).forGetter(ShooterComponent::power),
			Codec.FLOAT.optionalFieldOf("inaccuracy", 0.0f).forGetter(ShooterComponent::inaccuracy),
			Codec.INT.optionalFieldOf("cooldown_ticks", 10).forGetter(ShooterComponent::cooldownTicks),

			Codec.BOOL.optionalFieldOf("requires_ammo", false).forGetter(ShooterComponent::requiresAmmo),
			AmmoSpec.CODEC.optionalFieldOf("ammo", new AmmoSpec(Identifier.withDefaultNamespace("arrow")))
					.forGetter(ShooterComponent::ammo),
			Codec.BOOL.optionalFieldOf("consume_ammo", true).forGetter(ShooterComponent::consumeAmmo),

			Identifier.CODEC.optionalFieldOf("shoot_sound", Identifier.withDefaultNamespace("entity.arrow.shoot"))
					.forGetter(ShooterComponent::shootSound)
	).apply(i, ShooterComponent::new));

	public static final StreamCodec<ByteBuf, ShooterComponent> STREAM_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC, ShooterComponent::projectileEntity,
			ByteBufCodecs.FLOAT, ShooterComponent::power,
			ByteBufCodecs.FLOAT, ShooterComponent::inaccuracy,
			ByteBufCodecs.VAR_INT, ShooterComponent::cooldownTicks,
			ByteBufCodecs.BOOL, ShooterComponent::requiresAmmo,
			AmmoSpec.STREAM_CODEC, ShooterComponent::ammo,
			ByteBufCodecs.BOOL, ShooterComponent::consumeAmmo,
			Identifier.STREAM_CODEC, ShooterComponent::shootSound,
			ShooterComponent::new
	);
}

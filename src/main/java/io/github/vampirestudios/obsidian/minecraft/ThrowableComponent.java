package io.github.vampirestudios.obsidian.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record ThrowableComponent(
    Identifier projectileEntity, // e.g. "minecraft:snowball" or your own entity type id
    float power,                       // velocity multiplier
    float inaccuracy,
    int cooldownTicks,
    boolean consume,
    Identifier throwSound         // e.g. "minecraft:entity.snowball.throw"
) {
    public static final Codec<ThrowableComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        Identifier.CODEC.fieldOf("projectile_entity").forGetter(ThrowableComponent::projectileEntity),
        Codec.FLOAT.optionalFieldOf("power", 1.5f).forGetter(ThrowableComponent::power),
        Codec.FLOAT.optionalFieldOf("inaccuracy", 1.0f).forGetter(ThrowableComponent::inaccuracy),
        Codec.INT.optionalFieldOf("cooldown_ticks", 10).forGetter(ThrowableComponent::cooldownTicks),
        Codec.BOOL.optionalFieldOf("consume", true).forGetter(ThrowableComponent::consume),
        Identifier.CODEC.optionalFieldOf(
            "throw_sound",
            Identifier.withDefaultNamespace("entity.snowball.throw")
        ).forGetter(ThrowableComponent::throwSound)
    ).apply(i, ThrowableComponent::new));
    public static final StreamCodec<ByteBuf, ThrowableComponent> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            ThrowableComponent::projectileEntity,
            ByteBufCodecs.FLOAT,
            ThrowableComponent::power,
            ByteBufCodecs.FLOAT,
            ThrowableComponent::inaccuracy,
            ByteBufCodecs.VAR_INT,
            ThrowableComponent::cooldownTicks,
            ByteBufCodecs.BOOL,
            ThrowableComponent::consume,
            Identifier.STREAM_CODEC,
            ThrowableComponent::throwSound,
            ThrowableComponent::new
    );
}

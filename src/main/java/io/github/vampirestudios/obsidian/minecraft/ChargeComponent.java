package io.github.vampirestudios.obsidian.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Locale;

public record ChargeComponent(
    int maxChargeTicks,          // how long it takes to reach 100%
    int minReleaseTicks,         // require holding at least this long to fire (0 = instant)
    ChargeCurve curve,           // how charge scales
    float minPowerMultiplier,    // power at 0% (or at min release) e.g. 0.3
    float maxPowerMultiplier,    // power at 100% e.g. 1.0 (or >1.0 to overcharge style)
    Identifier startSound,
    Identifier releaseSound
) {
    public static final Codec<ChargeComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.INT.optionalFieldOf("max_charge_ticks", 20).forGetter(ChargeComponent::maxChargeTicks),
        Codec.INT.optionalFieldOf("min_release_ticks", 0).forGetter(ChargeComponent::minReleaseTicks),

        ChargeCurve.CODEC.optionalFieldOf("curve", ChargeCurve.LINEAR).forGetter(ChargeComponent::curve),

        Codec.FLOAT.optionalFieldOf("min_power_multiplier", 0.3f).forGetter(ChargeComponent::minPowerMultiplier),
        Codec.FLOAT.optionalFieldOf("max_power_multiplier", 1.0f).forGetter(ChargeComponent::maxPowerMultiplier),

        Identifier.CODEC.optionalFieldOf("start_sound", Identifier.withDefaultNamespace("item.crossbow.loading_start"))
            .forGetter(ChargeComponent::startSound),
        Identifier.CODEC.optionalFieldOf("release_sound", Identifier.withDefaultNamespace("item.crossbow.shoot"))
            .forGetter(ChargeComponent::releaseSound)
    ).apply(i, ChargeComponent::new));

    public static final StreamCodec<ByteBuf, ChargeComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ChargeComponent::maxChargeTicks,
            ByteBufCodecs.VAR_INT, ChargeComponent::minReleaseTicks,
            ChargeCurve.STREAM_CODEC, ChargeComponent::curve,
            ByteBufCodecs.FLOAT, ChargeComponent::minPowerMultiplier,
            ByteBufCodecs.FLOAT, ChargeComponent::maxPowerMultiplier,
            Identifier.STREAM_CODEC, ChargeComponent::startSound,
            Identifier.STREAM_CODEC, ChargeComponent::releaseSound,
            ChargeComponent::new
    );

    public enum ChargeCurve {
        LINEAR,
        QUADRATIC,
        CUBIC;

        public static final Codec<ChargeCurve> CODEC =
            Codec.STRING.xmap(ChargeCurve::fromId, ChargeCurve::id);

        // Ordinal on wire (VAR_INT), clamped on read
        public static final StreamCodec<ByteBuf, ChargeCurve> STREAM_CODEC =
                ByteBufCodecs.VAR_INT.map(
                        ord -> {
                            ChargeCurve[] v = values();
                            return (ord >= 0 && ord < v.length) ? v[ord] : LINEAR;
                        },
						Enum::ordinal
                );

        public String id() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static ChargeCurve fromId(String s) {
            String k = s.toLowerCase(Locale.ROOT);
            return switch (k) {
				case "quadratic", "quad" -> QUADRATIC;
                case "cubic" -> CUBIC;
                default -> LINEAR;
            };
        }
    }
}

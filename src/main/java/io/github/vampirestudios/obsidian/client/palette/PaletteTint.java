package io.github.vampirestudios.obsidian.client.palette;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.vampirestudios.packwright.assets.item.tints.Tint;

import java.util.Optional;

/**
 * Packwright side of {@link PaletteTintSource}: the tint entry written into a generated
 * {@code items/<id>.json}, one per channel.
 */
public final class PaletteTint extends Tint {

	public static final String TYPE = "obsidian:palette";

	public static final Codec<PaletteTint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("channel").forGetter(PaletteTint::channel),
			Identifier.CODEC.optionalFieldOf("channels").forGetter(PaletteTint::channelsOptional),
			ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(PaletteTint::fallback)
	).apply(instance, (channel, channels, fallback) -> new PaletteTint(channel, channels.orElse(null), fallback)));

	static {
		Tint.register(TYPE, CODEC);
	}

	private final String channel;
	private final Identifier channels;
	private final int fallback;

	public PaletteTint(String channel, Identifier channels, int fallback) {
		super(TYPE);
		this.channel = channel;
		this.channels = channels;
		this.fallback = fallback & 0xFFFFFF;
	}

	public String channel() {
		return this.channel;
	}

	public Identifier channels() {
		return this.channels;
	}

	public Optional<Identifier> channelsOptional() {
		return Optional.ofNullable(this.channels);
	}

	public int fallback() {
		return this.fallback;
	}
}

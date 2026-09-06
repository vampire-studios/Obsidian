package io.github.vampirestudios.obsidian.registry.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import io.github.vampirestudios.obsidian.api.obsidian.palette.PaletteColor;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Which palette a stack is painted with, plus any per-channel overrides.
 *
 * <p>The item itself never changes: {@code example:longsword} with
 * {@code {"palette": "example:royal"}} and the same sword with {@code example:cursed} are the same
 * registered item, so they stack-swap, enchant and repair identically.</p>
 *
 * <p>Overrides are keyed by the asset-side channel name declared in the item's
 * {@link io.github.vampirestudios.obsidian.api.obsidian.palette.Palette}, and win over
 * the palette. They are what a dye station or a colour picker writes.</p>
 */
public record PaletteComponent(Optional<Identifier> palette, Map<String, Integer> overrides) {

	public static final PaletteComponent EMPTY = new PaletteComponent(Optional.empty(), Map.of());

	public static final Codec<PaletteComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.optionalFieldOf("palette").forGetter(PaletteComponent::palette),
			Codec.unboundedMap(Codec.STRING, PaletteColor.ARGB_CODEC)
					.optionalFieldOf("overrides", Map.of())
					.forGetter(PaletteComponent::overrides)
	).apply(instance, PaletteComponent::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PaletteComponent> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), PaletteComponent::palette,
			ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT), PaletteComponent::overrides,
			PaletteComponent::new
	);

	public static PaletteComponent of(Identifier palette) {
		return new PaletteComponent(Optional.ofNullable(palette), Map.of());
	}

	public PaletteComponent withPalette(Identifier palette) {
		return new PaletteComponent(Optional.ofNullable(palette), this.overrides);
	}

	/** Sets one channel override; a {@code null} colour clears it. */
	public PaletteComponent withOverride(String channel, Integer color) {
		Map<String, Integer> copy = new LinkedHashMap<>(this.overrides);
		if (color == null) copy.remove(channel);
		else copy.put(channel, color);
		return new PaletteComponent(this.palette, Map.copyOf(copy));
	}

	public PaletteComponent withoutOverrides() {
		return new PaletteComponent(this.palette, Map.of());
	}

	public boolean isEmpty() {
		return this.palette.isEmpty() && this.overrides.isEmpty();
	}
}

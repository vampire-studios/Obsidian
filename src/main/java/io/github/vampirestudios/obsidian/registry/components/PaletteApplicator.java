package io.github.vampirestudios.obsidian.registry.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Marks an item as something that paints other things — a dye kit, a spray can, a "chroma set".
 *
 * <p>The applicator carries a {@link PaletteComponent} of its own, so it can hand over a registered
 * palette, a set of ad-hoc colours, or both. {@link #channels} narrows what it is allowed to touch:
 * empty means "repaint the whole thing", while {@code ["detail"]} makes an item that only ever
 * changes the trim.</p>
 *
 * <p>By default an applicator is infinitely reusable. {@link #uses} gives it durability that runs
 * out, {@link #damage} spends the durability the item already has, and {@link #consumes} spends the
 * item itself.</p>
 *
 * <pre>{@code
 * "components": {
 *   "obsidian:palette_applicator": {
 *     "paint": { "palette": "examplepack:royal" },
 *     "uses": 8
 *   }
 * }
 * }</pre>
 */
public record PaletteApplicator(PaletteComponent paint, List<String> channels, boolean consumes, int damage,
                                Optional<Integer> uses) {

	public static final Codec<PaletteApplicator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PaletteComponent.CODEC.optionalFieldOf("paint", PaletteComponent.EMPTY).forGetter(PaletteApplicator::paint),
			Codec.STRING.listOf().optionalFieldOf("channels", List.of()).forGetter(PaletteApplicator::channels),
			Codec.BOOL.optionalFieldOf("consumes", false).forGetter(PaletteApplicator::consumes),
			Codec.INT.optionalFieldOf("damage", 0).forGetter(PaletteApplicator::damage),
			Codec.INT.optionalFieldOf("uses").forGetter(PaletteApplicator::uses)
	).apply(instance, PaletteApplicator::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PaletteApplicator> STREAM_CODEC = StreamCodec.composite(
			PaletteComponent.STREAM_CODEC, PaletteApplicator::paint,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), PaletteApplicator::channels,
			ByteBufCodecs.BOOL, PaletteApplicator::consumes,
			ByteBufCodecs.VAR_INT, PaletteApplicator::damage,
			ByteBufCodecs.optional(ByteBufCodecs.VAR_INT), PaletteApplicator::uses,
			PaletteApplicator::new
	);

	public static PaletteApplicator of(PaletteComponent paint) {
		return new PaletteApplicator(paint, List.of(), false, 0, Optional.empty());
	}

	/** Whether this applicator repaints everything rather than a named subset of channels. */
	public boolean isFullRepaint() {
		return this.channels.isEmpty();
	}

	/** The channels this applicator may write, given what the target actually has. */
	public List<String> targetChannels(Iterable<String> available) {
		if (!isFullRepaint()) return this.channels;

		List<String> all = new ArrayList<>();
		for (String channel : available) all.add(channel);
		return all;
	}

	/**
	 * Durability spent per application: {@code damage} if set, otherwise 1 when {@link #uses} gave
	 * the item durability of its own, otherwise none.
	 */
	public int durabilityCost() {
		if (this.damage > 0) return this.damage;
		return this.uses.isPresent() ? 1 : 0;
	}
}

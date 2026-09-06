package io.github.vampirestudios.obsidian.api.obsidian.palette;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import io.github.vampirestudios.obsidian.registry.components.PaletteComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Turns "which palette" plus "which channel" into an ARGB colour.
 *
 * <p>This is the one place that knows the lookup order, so items, blocks, entities, particles and
 * GUIs all resolve colours the same way:</p>
 * <ol>
 *   <li>a per-channel override stored on the thing itself ({@link PaletteComponent#overrides()})</li>
 *   <li>the palette it is painted with, following its {@code parent} chain</li>
 *   <li>the fallback colour on its own channel</li>
 *   <li>the caller's fallback</li>
 * </ol>
 *
 * <p>Derived colours ({@code {"from": "primary", "lighten": 0.3}}) are resolved here too, with a
 * cycle guard so a malformed addon cannot lock the render thread.</p>
 */
public final class PaletteResolver {

	public static final int WHITE = 0xFFFFFFFF;

	/** Resolved palettes, keyed by id. Addon content is immutable once loaded. */
	private static final Map<Identifier, Map<String, Integer>> CACHE = new ConcurrentHashMap<>();

	private PaletteResolver() {
	}

	/** Drops cached resolutions; call after addons are reloaded. */
	public static void invalidate() {
		CACHE.clear();
	}

	// -------------------------------------------------------------------------
	// Palette-level resolution
	// -------------------------------------------------------------------------

	public static Palette palette(Identifier id) {
		return id == null ? null : ContentRegistries.PALETTES.getValue(id);
	}

	/** Every channel {@code palette} resolves to, including inherited ones. */
	public static Map<String, Integer> resolveAll(Palette palette) {
		if (palette == null) return Map.of();
		if (palette.id != null) {
			Map<String, Integer> cached = CACHE.get(palette.id);
			if (cached != null) return cached;
		}

		Map<String, PaletteColor> raw = new LinkedHashMap<>();
		collectColors(palette, raw, new HashSet<>());

		Map<String, Integer> resolved = new LinkedHashMap<>();
		Deque<String> resolving = new ArrayDeque<>();
		for (String channel : raw.keySet()) {
			resolved.put(channel, resolveIn(raw, resolved, resolving, channel, WHITE));
		}

		Map<String, Integer> view = Map.copyOf(resolved);
		if (palette.id != null) CACHE.put(palette.id, view);
		return view;
	}

	private static void collectColors(Palette palette, Map<String, PaletteColor> out, Set<Identifier> seen) {
		if (palette == null) return;
		if (palette.id != null && !seen.add(palette.id)) return;

		if (palette.parent != null) collectColors(palette(palette.parent), out, seen);
		if (palette.colors != null) out.putAll(palette.colors);

		// Long-form channels can carry a colour too, and win over the plain map.
		if (palette.channels != null) {
			for (PaletteChannel channel : palette.channels) {
				if (channel != null && channel.name != null && channel.defaultColor != null) {
					out.put(channel.name, channel.defaultColor);
				}
			}
		}
	}

	private static int resolveIn(Map<String, PaletteColor> raw, Map<String, Integer> resolved,
	                             Deque<String> resolving, String channel, int fallback) {
		Integer done = resolved.get(channel);
		if (done != null) return done;

		PaletteColor color = raw.get(channel);
		if (color == null) return fallback;

		// A reference cycle (a → b → a) resolves to the fallback instead of recursing forever.
		if (resolving.contains(channel)) return fallback;

		resolving.push(channel);
		try {
			int value = color.resolve(reference -> resolveIn(raw, resolved, resolving, reference, fallback));
			resolved.put(channel, value);
			return value;
		} finally {
			resolving.pop();
		}
	}

	/** Resolved colour of {@code channel} in {@code palette}, or {@code fallback}. */
	public static int color(Palette palette, String channel, int fallback) {
		if (palette == null || channel == null) return fallback;
		Integer value = resolveAll(palette).get(channel);
		return value != null ? value : fallback;
	}

	/** Resolved colour of {@code channel} in the palette with the given id, or {@code fallback}. */
	public static int color(Identifier paletteId, String channel, int fallback) {
		return color(palette(paletteId), channel, fallback);
	}

	// -------------------------------------------------------------------------
	// Asset-level resolution
	// -------------------------------------------------------------------------

	/**
	 * Full lookup for one channel of a colourable asset.
	 *
	 * @param channels  the palette describing what the asset supports; may be {@code null}, in which
	 *                  case only the overrides and the painted palette are consulted
	 * @param selection the palette and per-channel overrides stored on the asset; may be {@code null}
	 * @param channel   the asset-side channel name
	 */
	public static int resolve(Palette channels, PaletteComponent selection, String channel, int fallback) {
		PaletteChannel definition = channels != null ? channels.channel(channel) : null;

		if (selection != null && (definition == null || !definition.locked)) {
			Integer override = selection.overrides().get(channel);
			if (override != null) return override;
		}

		Identifier paintId = selection != null && selection.palette().isPresent()
				? selection.palette().get()
				: (channels != null ? channels.defaultPalette : null);

		Palette paint = palette(paintId);
		if (paint != null) {
			String source = definition != null ? definition.sourceChannel() : channel;
			Integer value = resolveAll(paint).get(source);
			if (value != null) return value;
		}

		return definition != null ? definition.fallbackColor() : fallback;
	}

	/** As {@link #resolve}, defaulting to opaque white. */
	public static int resolve(Palette channels, PaletteComponent selection, String channel) {
		return resolve(channels, selection, channel, WHITE);
	}

	/** Colours for every tint layer of {@code channels}, ready to hand to a model renderer. */
	public static int[] resolveTints(Palette channels, PaletteComponent selection) {
		if (channels == null) return new int[0];

		var layers = channels.byTintIndex();
		int[] tints = new int[layers.size()];
		for (int i = 0; i < layers.size(); i++) {
			PaletteChannel channel = layers.get(i);
			tints[i] = channel == null ? WHITE : resolve(channels, selection, channel.name, WHITE);
		}
		return tints;
	}

	// -------------------------------------------------------------------------
	// Item stacks
	// -------------------------------------------------------------------------

	/** The palette selection stored on a stack, or an empty selection. */
	public static PaletteComponent selectionOf(ItemStack stack) {
		if (stack == null || stack.isEmpty()) return PaletteComponent.EMPTY;
		PaletteComponent component = stack.get(OItemComponents.PALETTE);
		return component != null ? component : PaletteComponent.EMPTY;
	}

	/**
	 * The palette describing a stack's channels, from its {@code obsidian:channels} component. Items
	 * get that component at registration, so anything holding the stack — a command, an applicator,
	 * a GUI — can tell what it supports without consulting the addon files.
	 */
	public static Identifier channelsOf(ItemStack stack) {
		return stack == null || stack.isEmpty() ? null : stack.get(OItemComponents.CHANNELS);
	}

	/**
	 * Resolves one channel for a stack. {@code channelsId} is what the caller already knows — a tint
	 * source knows the palette it was generated from — and the stack's own component fills in when
	 * it does not.
	 */
	public static int resolveForStack(ItemStack stack, Identifier channelsId, String channel, int fallback) {
		Identifier resolved = channelsId != null ? channelsId : channelsOf(stack);
		return resolve(palette(resolved), selectionOf(stack), channel, fallback);
	}

	// -------------------------------------------------------------------------
	// Colour expressions — for particles, GUIs, generated assets, …
	// -------------------------------------------------------------------------

	/**
	 * Parses a colour written either literally or as a palette reference, so any system that
	 * already accepts a colour string can accept a palette channel instead:
	 *
	 * <ul>
	 *   <li>{@code "#D8B24A"} — literal</li>
	 *   <li>{@code "palette:example:royal/accent"} — the {@code accent} channel of that palette</li>
	 * </ul>
	 */
	public static int fromExpression(String expression, int fallback) {
		if (expression == null || expression.isBlank()) return fallback;
		String value = expression.trim();

		if (value.startsWith("palette:")) {
			String reference = value.substring("palette:".length());
			int split = reference.lastIndexOf('/');
			if (split <= 0 || split == reference.length() - 1) return fallback;

			Identifier paletteId = Identifier.tryParse(reference.substring(0, split));
			return paletteId == null ? fallback : color(paletteId, reference.substring(split + 1), fallback);
		}

		return PaletteColor.parseString(value)
				.filter(PaletteColor::isLiteral)
				.map(color -> color.resolve(channel -> fallback))
				.orElse(fallback);
	}
}

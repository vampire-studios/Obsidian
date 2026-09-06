package io.github.vampirestudios.obsidian.api.obsidian.palette;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A named, ordered set of colours.
 *
 * <pre>{@code
 * {
 *   "colors": {
 *     "primary": "#A1744B",
 *     "secondary": "#4A4F67",
 *     "accent": "#EBE9DB"
 *   }
 * }
 * }</pre>
 *
 * <p>One type, two roles:</p>
 * <ul>
 *   <li><b>As an asset's channels</b> — the write order is tint-layer order, so the first entry
 *       drives {@code tintindex} 0, and the colours are the fallback when nothing repaints them.</li>
 *   <li><b>As paint</b> — a scheme applied to an asset. Colours land on the asset's channels by
 *       name, so a palette and the thing it paints never have to know about each other.</li>
 * </ul>
 *
 * <p>Everything past {@code colors} is optional and exists for cases the plain map cannot cover:
 * {@link #parent} for sharing, {@link #channels} for regions whose name differs from the colour
 * they read, and the whitelists for restricting what may paint an asset.</p>
 */
public class Palette {

	/** Filled in by the addon module from the file name, or by {@link #registerInline}. */
	public transient Identifier id;

	/** Optional palette to inherit from; this palette's own entries win. */
	public Identifier parent;

	/** Display name, used by tooltips and by anything that lets players pick a palette. */
	public NameInformation name;

	/** Channel name → colour, in tint-layer order when this palette describes an asset. */
	public Map<String, PaletteColor> colors = new LinkedHashMap<>();

	/**
	 * Long form, for channels needing a {@code source}, an explicit {@code tint_index} or
	 * {@code locked}. Applied after {@link #colors}; a same-named entry replaces it.
	 */
	public List<PaletteChannel> channels = new ArrayList<>();

	/** Palette an asset using these channels starts out painted with. */
	@SerializedName("default_palette")
	public Identifier defaultPalette;

	/** Free-form grouping labels, so a whitelist can name a group rather than every palette. */
	public List<String> tags = new ArrayList<>();

	/** Whitelist of palettes allowed to paint an asset using these channels; empty allows any. */
	public List<Identifier> palettes = new ArrayList<>();

	/** Whitelist by tag; a palette carrying any listed tag is allowed. */
	@SerializedName("palette_tags")
	public List<String> paletteTags = new ArrayList<>();

	/** Hidden palettes are not offered in pickers, but still resolve if referenced explicitly. */
	public boolean hidden = false;

	private transient Map<String, PaletteChannel> resolvedChannels;

	// -------------------------------------------------------------------------
	// Channels
	// -------------------------------------------------------------------------

	/**
	 * Every channel of this palette and of its parents, keyed by name and kept in write order.
	 * Cached, since addon content does not change once loaded.
	 */
	public Map<String, PaletteChannel> allChannels() {
		if (this.resolvedChannels != null) return this.resolvedChannels;

		Map<String, PaletteChannel> merged = new LinkedHashMap<>();
		collectInto(merged, new HashSet<>());
		this.resolvedChannels = merged;
		return merged;
	}

	private void collectInto(Map<String, PaletteChannel> out, Set<Identifier> seen) {
		if (this.id != null && !seen.add(this.id)) return;

		if (this.parent != null) {
			Palette parentPalette = ContentRegistries.PALETTES.getValue(this.parent);
			if (parentPalette != null) parentPalette.collectInto(out, seen);
		}

		if (this.colors != null) {
			for (Map.Entry<String, PaletteColor> entry : this.colors.entrySet()) {
				PaletteChannel channel = new PaletteChannel();
				channel.name = entry.getKey();
				channel.defaultColor = entry.getValue();
				out.put(channel.name, channel);
			}
		}

		if (this.channels != null) {
			for (PaletteChannel channel : this.channels) {
				if (channel != null && channel.name != null) out.put(channel.name, channel);
			}
		}
	}

	public PaletteChannel channel(String name) {
		return allChannels().get(name);
	}

	public boolean declares(String channel) {
		return allChannels().containsKey(channel);
	}

	/**
	 * Channels laid out by model tint layer: index {@code n} drives {@code tintindex} {@code n}.
	 * Gaps left by explicit {@code tint_index} values are {@code null}, for the caller to fill with
	 * a neutral tint.
	 */
	public List<PaletteChannel> byTintIndex() {
		List<PaletteChannel> ordered = new ArrayList<>(allChannels().values());
		int size = 0;

		for (int i = 0; i < ordered.size(); i++) {
			PaletteChannel channel = ordered.get(i);
			size = Math.max(size, (channel.tintIndex != null ? channel.tintIndex : i) + 1);
		}

		List<PaletteChannel> layers = new ArrayList<>(Collections.<PaletteChannel>nCopies(size, null));
		for (int i = 0; i < ordered.size(); i++) {
			PaletteChannel channel = ordered.get(i);
			layers.set(channel.tintIndex != null ? channel.tintIndex : i, channel);
		}
		return layers;
	}

	// -------------------------------------------------------------------------
	// Colours
	// -------------------------------------------------------------------------

	/** Resolved ARGB for a channel, or {@code fallback}. */
	public int color(String channel, int fallback) {
		return PaletteResolver.color(this, channel, fallback);
	}

	/** Every channel this palette resolves to, including inherited ones. */
	public Map<String, Integer> resolveAll() {
		return PaletteResolver.resolveAll(this);
	}

	// -------------------------------------------------------------------------
	// Whitelisting
	// -------------------------------------------------------------------------

	/** Whether {@code paint} may be applied to an asset using these channels. Empty allows any. */
	public boolean accepts(Palette paint) {
		if (paint == null) return false;
		boolean unrestricted = (this.palettes == null || this.palettes.isEmpty())
				&& (this.paletteTags == null || this.paletteTags.isEmpty());
		if (unrestricted) return true;

		if (this.palettes != null && paint.id != null && this.palettes.contains(paint.id)) return true;
		if (this.paletteTags != null && paint.tags != null) {
			for (String tag : this.paletteTags) if (paint.tags.contains(tag)) return true;
		}
		return false;
	}

	// -------------------------------------------------------------------------
	// Inline palettes
	// -------------------------------------------------------------------------

	/** Builds a palette from bare channel names, for {@code "channels": ["primary", "accent"]}. */
	public static Palette ofNames(List<String> names) {
		Palette palette = new Palette();
		for (String name : names) {
			if (name != null && !name.isBlank()) palette.colors.put(name, PaletteColor.WHITE);
		}
		return palette;
	}

	/**
	 * Registers a palette an asset wrote inline, under the asset's own id, and returns that id so
	 * components and tint sources have something to point at. Registering twice is a no-op: both
	 * the registration pass and the asset-generation pass ask for the same palette.
	 */
	public static Identifier registerInline(Identifier ownerId, Palette palette) {
		if (ContentRegistries.PALETTES.getValue(ownerId) != null) return ownerId;

		palette.id = ownerId;
		Registry.register(ContentRegistries.PALETTES, ownerId, palette);
		return ownerId;
	}
}

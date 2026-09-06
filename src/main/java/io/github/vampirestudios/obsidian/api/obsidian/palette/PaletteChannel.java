package io.github.vampirestudios.obsidian.api.obsidian.palette;

import com.google.gson.annotations.SerializedName;

/**
 * The long form of one entry in a {@link Palette}, for the cases the plain
 * {@code "colors": { "primary": "#…" }} map cannot express.
 *
 * <pre>{@code
 * { "name": "blade", "source": "metal", "default": "#B0C4DE", "tint_index": 0 }
 * }</pre>
 */
public class PaletteChannel {

	/** Channel name. When the palette describes an asset, this is what the asset calls the region. */
	public String name;

	/** Palette channel to read instead of {@link #name}, when the two vocabularies differ. */
	public String source;

	/** Colour used when no palette supplies this channel. */
	@SerializedName("default")
	public PaletteColor defaultColor;

	/**
	 * Model tint layer this channel drives, i.e. the {@code tintindex} in the block/item model.
	 * Defaults to the channel's position in the palette.
	 */
	@SerializedName("tint_index")
	public Integer tintIndex;

	/** Players may not recolour this channel directly; it only moves when the palette changes. */
	public boolean locked = false;

	public String sourceChannel() {
		return this.source != null && !this.source.isBlank() ? this.source : this.name;
	}

	public int fallbackColor() {
		return this.defaultColor == null
				? PaletteResolver.WHITE
				: this.defaultColor.resolve(channel -> PaletteResolver.WHITE);
	}
}

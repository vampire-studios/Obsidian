package io.github.vampirestudios.obsidian.api.obsidian.palette;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.ExtraCodecs;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Optional;
import java.util.function.ToIntFunction;

/**
 * A single colour entry inside a {@link Palette}.
 *
 * <p>A colour is either a <b>literal</b> or a <b>derived</b> value:</p>
 * <ul>
 *   <li>literal: {@code "#RRGGBB"}, {@code "#AARRGGBB"}, {@code "0x..."}, a raw int, or {@code [r, g, b]}</li>
 *   <li>derived: {@code { "from": "metal", "lighten": 0.25 }} — reads another channel of the same
 *       palette and applies modifiers to it</li>
 * </ul>
 *
 * <p>Derived colours are what keep palettes small: a palette usually defines two or three real
 * colours and derives its highlights, shadows and emissive tones from them.</p>
 *
 * <p>Supported modifiers, applied in this order: {@code mix} → {@code saturate} → {@code lighten}
 * → {@code darken} → {@code alpha}.</p>
 */
public final class PaletteColor {

	public static final PaletteColor WHITE = literal(0xFFFFFFFF);

	/**
	 * An ARGB colour written the way palettes write them: {@code "#RRGGBB"}, {@code "#AARRGGBB"},
	 * {@code "0x…"}, a raw int, or an {@code [r, g, b, a]} array.
	 *
	 * <p>Vanilla's own colour codecs take ints and arrays but not hex strings, which is the form
	 * every palette file uses — so components that store colours accept both through this.</p>
	 */
	public static final Codec<Integer> ARGB_CODEC = Codec.withAlternative(
			ExtraCodecs.ARGB_COLOR_CODEC,
			Codec.STRING.comapFlatMap(
					string -> parseString(string)
							.filter(PaletteColor::isLiteral)
							.map(color -> DataResult.success(color.literal))
							.orElseGet(() -> DataResult.error(() -> "Not a colour: " + string)),
					PaletteColor::toHex
			)
	);

	/** Literal ARGB value, or {@code 0} when this entry derives from another channel. */
	private int literal;
	/** Channel this colour derives from, or {@code null} for literals. */
	private String from;
	private String mix;
	private float mixAmount = 0.5F;
	private float lighten;
	private float darken;
	private float saturate;
	private float alpha = 1.0F;

	private PaletteColor() {
	}

	public static PaletteColor literal(int argb) {
		PaletteColor color = new PaletteColor();
		color.literal = argb;
		return color;
	}

	public static PaletteColor reference(String channel) {
		PaletteColor color = new PaletteColor();
		color.from = channel;
		return color;
	}

	public boolean isLiteral() {
		return this.from == null;
	}

	/** The channel this colour reads from, or {@code null} when it is a literal. */
	public String from() {
		return this.from;
	}

	/**
	 * Resolves this colour to an ARGB int.
	 *
	 * @param lookup resolves a sibling channel name to an ARGB int; only used by derived colours
	 */
	public int resolve(ToIntFunction<String> lookup) {
		int base = this.from == null ? this.literal : lookup.applyAsInt(this.from);

		if (this.mix != null) base = mix(base, lookup.applyAsInt(this.mix), clamp01(this.mixAmount));
		if (this.saturate != 0.0F) base = saturate(base, this.saturate);
		if (this.lighten != 0.0F) base = mix(base, 0xFFFFFFFF, clamp01(this.lighten));
		if (this.darken != 0.0F) base = mix(base, 0xFF000000, clamp01(this.darken));
		if (this.alpha != 1.0F) base = withAlpha(base, Math.round(alphaOf(base) * clamp01(this.alpha)));

		return base;
	}

	// -------------------------------------------------------------------------
	// Parsing
	// -------------------------------------------------------------------------

	/**
	 * Parses the string form of a colour: a hex literal, or {@code $channel} / {@code #channel}-free
	 * bare name referring to another channel of the same palette.
	 */
	public static Optional<PaletteColor> parseString(String raw) {
		if (raw == null) return Optional.empty();
		String value = raw.trim();
		if (value.isEmpty()) return Optional.empty();

		if (value.startsWith("$")) return Optional.of(reference(value.substring(1)));

		String hex = value.startsWith("#") ? value.substring(1)
				: value.toLowerCase(Locale.ROOT).startsWith("0x") ? value.substring(2)
				: null;

		if (hex != null) {
			try {
				long parsed = Long.parseLong(hex, 16);
				return Optional.of(literal(hex.length() <= 6 ? 0xFF000000 | (int) parsed : (int) parsed));
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		}

		// Bare names are channel references — "primary", "metal", …
		return Optional.of(reference(value));
	}

	public static String toHex(int argb) {
		return alphaOf(argb) == 255
				? String.format(Locale.ROOT, "#%06X", argb & 0xFFFFFF)
				: String.format(Locale.ROOT, "#%08X", argb);
	}

	// -------------------------------------------------------------------------
	// Colour maths
	// -------------------------------------------------------------------------

	public static int alphaOf(int argb) {
		return (argb >> 24) & 0xFF;
	}

	public static int withAlpha(int argb, int alpha) {
		return (Math.clamp(alpha, 0, 255) << 24) | (argb & 0xFFFFFF);
	}

	/** Linear blend, {@code amount} = 0 keeps {@code from}, 1 gives {@code to}. Alpha follows {@code from}. */
	public static int mix(int from, int to, float amount) {
		int a = alphaOf(from);
		int r = Math.round(((from >> 16) & 0xFF) + (((to >> 16) & 0xFF) - ((from >> 16) & 0xFF)) * amount);
		int g = Math.round(((from >> 8) & 0xFF) + (((to >> 8) & 0xFF) - ((from >> 8) & 0xFF)) * amount);
		int b = Math.round((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * amount);
		return (a << 24) | (Math.clamp(r, 0, 255) << 16) | (Math.clamp(g, 0, 255) << 8) | Math.clamp(b, 0, 255);
	}

	/** Pushes a colour away from (positive) or towards (negative) its own grey value. */
	public static int saturate(int argb, float amount) {
		int r = (argb >> 16) & 0xFF, g = (argb >> 8) & 0xFF, b = argb & 0xFF;
		float grey = 0.2126F * r + 0.7152F * g + 0.0722F * b;
		int grayed = (alphaOf(argb) << 24)
				| (Math.clamp(Math.round(grey), 0, 255) << 16)
				| (Math.clamp(Math.round(grey), 0, 255) << 8)
				| Math.clamp(Math.round(grey), 0, 255);
		// mixing towards grey by a negative amount extrapolates away from it
		return mix(argb, grayed, -amount);
	}

	private static float clamp01(float value) {
		return Math.clamp(value, 0.0F, 1.0F);
	}

	// -------------------------------------------------------------------------
	// Gson bridge — addon files are read with Gson, not with codecs
	// -------------------------------------------------------------------------

	public static final class Adapter implements JsonDeserializer<PaletteColor>, JsonSerializer<PaletteColor> {

		@Override
		public PaletteColor deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
			if (json == null || json.isJsonNull()) return null;

			if (json.isJsonArray()) {
				JsonArray array = json.getAsJsonArray();
				if (array.size() < 3) throw new JsonParseException("Colour arrays need at least [r, g, b]");
				int a = array.size() > 3 ? array.get(3).getAsInt() : 255;
				return literal((Math.clamp(a, 0, 255) << 24)
						| (Math.clamp(array.get(0).getAsInt(), 0, 255) << 16)
						| (Math.clamp(array.get(1).getAsInt(), 0, 255) << 8)
						| Math.clamp(array.get(2).getAsInt(), 0, 255));
			}

			if (json.isJsonPrimitive()) {
				JsonPrimitive primitive = json.getAsJsonPrimitive();
				if (primitive.isNumber()) return literal(0xFF000000 | primitive.getAsInt());
				return parseString(primitive.getAsString())
						.orElseThrow(() -> new JsonParseException("Invalid colour: " + primitive.getAsString()));
			}

			JsonObject object = json.getAsJsonObject();
			if (object.has("color")) return deserialize(object.get("color"), type, context);

			PaletteColor color = new PaletteColor();
			if (object.has("from")) color.from = object.get("from").getAsString();
			if (object.has("mix")) color.mix = object.get("mix").getAsString();
			if (object.has("mix_amount")) color.mixAmount = object.get("mix_amount").getAsFloat();
			if (object.has("lighten")) color.lighten = object.get("lighten").getAsFloat();
			if (object.has("darken")) color.darken = object.get("darken").getAsFloat();
			if (object.has("saturate")) color.saturate = object.get("saturate").getAsFloat();
			if (object.has("alpha")) color.alpha = object.get("alpha").getAsFloat();

			if (color.from == null) throw new JsonParseException("Derived colours need a \"from\" channel");
			return color;
		}

		@Override
		public JsonElement serialize(PaletteColor color, Type type, JsonSerializationContext context) {
			if (color == null) return JsonNull.INSTANCE;
			if (color.isLiteral()) return new JsonPrimitive(toHex(color.literal));

			JsonObject object = new JsonObject();
			object.addProperty("from", color.from);
			if (color.mix != null) {
				object.addProperty("mix", color.mix);
				object.addProperty("mix_amount", color.mixAmount);
			}
			if (color.lighten != 0.0F) object.addProperty("lighten", color.lighten);
			if (color.darken != 0.0F) object.addProperty("darken", color.darken);
			if (color.saturate != 0.0F) object.addProperty("saturate", color.saturate);
			if (color.alpha != 1.0F) object.addProperty("alpha", color.alpha);
			return object;
		}
	}
}

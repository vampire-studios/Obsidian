package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class DropInformation {

	/**
	 * Whether Obsidian writes this block's loot table. On by default, because a registered block with no
	 * loot table drops nothing at all. Turn it off for a block that should genuinely drop nothing, or one
	 * whose loot table the pack ships itself in a data pack.
	 */
	@SerializedName("generate_loot_table")
	public boolean generateLootTable = true;

	/**
	 * Use this loot table instead of one of our own — any table that exists at break time, whether
	 * vanilla's, another mod's, or one your pack ships. The block is pointed straight at it, so nothing
	 * is generated for it and {@code drops} is ignored.
	 *
	 * <p>This is the block itself; its companion blocks are named individually in
	 * {@link #companionLootTables}.
	 */
	@SerializedName("loot_table")
	public Identifier lootTable;

	/**
	 * The same, for the companion blocks {@code additional_information} adds, keyed by which one:
	 * {@code slab}, {@code stairs}, {@code wall}, {@code fence}, {@code fence_gate}, {@code button},
	 * {@code pressure_plate}, {@code door} or {@code trapdoor}. Spelling is forgiving — the flag's own
	 * name works too, so {@code walls} and {@code fenceGate} are both understood.
	 */
	@SerializedName("companion_loot_tables")
	public Map<String, Identifier> companionLootTables;

	/** What the block drops instead of itself. */
	public Drop[] drops;

	/** The table declared for one companion, or null when it should get one of its own. */
	public Identifier lootTableFor(String companion) {
		if (companionLootTables == null || companionLootTables.isEmpty()) return null;

		String wanted = normalize(companion);
		for (Map.Entry<String, Identifier> entry : companionLootTables.entrySet()) {
			if (normalize(entry.getKey()).equals(wanted)) return entry.getValue();
		}
		return null;
	}

	/** Folds the spellings of one companion together: {@code walls}, {@code fenceGate}, {@code fence_gate}. */
	private static String normalize(String key) {
		String folded = key.toLowerCase(Locale.ROOT).replace("_", "").replace(" ", "");
		return folded.equals("walls") ? "wall" : folded;
	}

	/**
	 * The ore idiom: mining with Silk Touch gives the block itself, anything else gives {@link #drops}.
	 * Ignored when the block has no {@code drops}.
	 */
	@SerializedName("silk_touch_drops_block")
	public boolean silkTouchDropsBlock = false;

	/** Whether the drop survives being blown up, as almost every vanilla block's does. */
	@SerializedName("survives_explosion")
	public boolean survivesExplosion = true;

	@SerializedName("xp_drop_amount")
	public int xpDropAmount = 1;

	public static class Drop {
		public Identifier name;

		/** Only drops when the tool has Silk Touch. */
		@SerializedName("drops_if_silk_touch")
		public boolean dropsIfSilkTouch = false;

		/**
		 * Only drops in the block states named here, keyed by state property. A value is either exact —
		 * {@code {"age": 7}} — or a range, {@code {"age": {"min": 4}}}, with either bound left out.
		 *
		 * <p>This is what makes a crop drop produce only when it is grown and seeds at any stage, and it
		 * works on any property the block has, not just age.
		 */
		public JsonObject when;

		/**
		 * The {@code when} clause as vanilla's state predicate wants it: every value a string, and a range
		 * an object of {@code min} and {@code max}.
		 *
		 * @return null when the drop is not conditioned on the block's state at all
		 */
		public Map<String, Object> stateWhen() {
			if (when == null || when.isEmpty()) return null;

			Map<String, Object> state = new LinkedHashMap<>();
			for (Map.Entry<String, JsonElement> entry : when.entrySet()) {
				JsonElement value = entry.getValue();
				if (value == null || value.isJsonNull()) continue;

				if (!value.isJsonObject()) {
					state.put(entry.getKey(), asStateValue(entry.getKey(), value));
					continue;
				}

				JsonObject range = value.getAsJsonObject();
				Map<String, Object> bounds = new LinkedHashMap<>();
				if (range.has("min")) bounds.put("min", asStateValue(entry.getKey(), range.get("min")));
				if (range.has("max")) bounds.put("max", asStateValue(entry.getKey(), range.get("max")));
				if (bounds.isEmpty()) {
					throw new IllegalArgumentException("'when' range for \"" + entry.getKey()
							+ "\" needs a min, a max, or both");
				}
				state.put(entry.getKey(), bounds);
			}
			return state.isEmpty() ? null : state;
		}

		/** State values are compared as strings, whether they were written as numbers, booleans or names. */
		private static String asStateValue(String property, JsonElement value) {
			if (!value.isJsonPrimitive()) {
				throw new IllegalArgumentException("'when' value for \"" + property
						+ "\" must be a number, a boolean or a name");
			}
			return value.getAsString();
		}

		/** How many to drop: a number, or {@code {"min": 4, "max": 9}} for a random amount. */
		public JsonElement count;

		/**
		 * How an enchantment adds to the amount: the formula name as a string, or an object carrying the
		 * formula's own settings. See {@link Fortune}.
		 */
		public JsonElement fortune;

		public Count getCount() {
			if (count == null || count.isJsonNull()) return null;

			if (count.isJsonPrimitive() && count.getAsJsonPrimitive().isNumber()) {
				float amount = count.getAsFloat();
				return new Count(amount, amount);
			}
			if (!count.isJsonObject()) {
				throw new IllegalArgumentException("'count' must be a number or an object with min and max");
			}

			JsonObject object = count.getAsJsonObject();
			Float min = readFloat(object, "min");
			Float max = readFloat(object, "max");
			if (min == null && max == null) return null;
			if (min == null) min = max;
			if (max == null) max = min;
			return new Count(min, max);
		}

		public Fortune getFortune() {
			if (fortune == null || fortune.isJsonNull()) return null;

			if (fortune.isJsonPrimitive() && fortune.getAsJsonPrimitive().isString()) {
				return new Fortune(fortune.getAsString(), FORTUNE, 1, 3, 0.5F);
			}
			if (!fortune.isJsonObject()) {
				throw new IllegalArgumentException("'fortune' must be a formula name or an object");
			}

			JsonObject object = fortune.getAsJsonObject();
			String formula = object.has("formula") ? object.get("formula").getAsString() : "ore_drops";
			Identifier enchantment = object.has("enchantment")
					? Identifier.parse(object.get("enchantment").getAsString())
					: FORTUNE;
			Float bonusMultiplier = readFloat(object, "bonus_multiplier");
			Float extra = readFloat(object, "extra");
			Float probability = readFloat(object, "probability");
			return new Fortune(formula, enchantment,
					bonusMultiplier == null ? 1 : bonusMultiplier.intValue(),
					extra == null ? 3 : extra.intValue(),
					probability == null ? 0.5F : probability);
		}

		private static Float readFloat(JsonObject object, String key) {
			if (!object.has(key) || object.get(key).isJsonNull()) return null;
			return object.get(key).getAsFloat();
		}
	}

	private static final Identifier FORTUNE = Identifier.withDefaultNamespace("fortune");

	/** A fixed amount when {@code min} and {@code max} are the same, a random one otherwise. */
	public record Count(float min, float max) {
		public boolean fixed() {
			return min == max;
		}
	}

	/**
	 * @param formula    {@code ore_drops}, {@code uniform_bonus_count} or {@code binomial_with_bonus_count},
	 *                   the three vanilla knows
	 * @param bonusMultiplier {@code uniform_bonus_count} only
	 * @param extra           {@code binomial_with_bonus_count} only
	 * @param probability     {@code binomial_with_bonus_count} only
	 */
	public record Fortune(String formula, Identifier enchantment, int bonusMultiplier, int extra, float probability) {
	}
}

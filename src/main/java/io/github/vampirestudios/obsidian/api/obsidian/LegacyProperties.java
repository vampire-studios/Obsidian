package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.vampirestudios.obsidian.Obsidian;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The spellings older packs used for block and item settings, read into the ones Obsidian uses now.
 *
 * <p>Settings have been renamed and rebuilt more than once — the {@code blocks/materials} files went
 * away with Minecraft's own {@code Material} class, and several fields were renamed after that. Rather
 * than have those packs quietly fall back to defaults, the old names are translated here, once, before
 * anything is bound, and each one that turns up is named in the log so a pack can be brought up to date.
 *
 * <p>A file that uses both spellings keeps the current one; the old name is only read into a field
 * nothing has already filled.
 */
public final class LegacyProperties {

	private LegacyProperties() {
	}

	/** Old name to current name. */
	private static final Map<String, String> BLOCK_RENAMES = Map.ofEntries(
			Map.entry("blast_resistance", "resistance"),
			Map.entry("explosion_resistance", "resistance"),
			Map.entry("destroy_time", "hardness"),
			Map.entry("break_time", "hardness"),
			Map.entry("friction", "slipperiness"),
			Map.entry("light_level", "luminance"),
			Map.entry("light_emission", "luminance"),
			Map.entry("map_colour", "map_color"),
			Map.entry("piston_behavior", "push_reaction"),
			Map.entry("piston_behaviour", "push_reaction"),
			Map.entry("random_ticks", "randomTicks"),
			Map.entry("ticks_randomly", "randomTicks"),
			Map.entry("requires_correct_tool", "requires_tool"),
			Map.entry("requires_correct_tool_for_drops", "requires_tool"),
			Map.entry("blocks_movement", "collidable"),
			Map.entry("blocks_light", "force_solid"),
			Map.entry("burnable", "ignited_by_lava"),
			Map.entry("flammable", "ignited_by_lava"),
			Map.entry("sound", "sound_group")
	);

	private static final Map<String, String> ITEM_RENAMES = Map.ofEntries(
			Map.entry("has_glint", "has_enchantment_glint"),
			Map.entry("glint", "has_enchantment_glint"),
			Map.entry("stack_size", "max_stack_size"),
			Map.entry("maxStackSize", "max_stack_size"),
			Map.entry("durability", "max_uses"),
			Map.entry("max_damage", "max_uses"),
			Map.entry("creative_tab", "item_group"),
			Map.entry("group", "item_group"),
			Map.entry("enchantable", "is_enchantable"),
			Map.entry("fuel_time", "fuel_duration"),
			Map.entry("burn_time", "fuel_duration")
	);

	/**
	 * Settings a block material used to carry, as the sound and colour it stood for. The material itself
	 * is gone from the game, so this is the part of it worth keeping.
	 */
	private record Material(String soundGroup, String mapColor) {
	}

	private static final Map<String, Material> MATERIALS = materials();

	private static Map<String, Material> materials() {
		Map<String, Material> materials = new LinkedHashMap<>();
		materials.put("stone", new Material("minecraft:stone", "STONE"));
		materials.put("metal", new Material("minecraft:metal", "METAL"));
		materials.put("wood", new Material("minecraft:wood", "WOOD"));
		materials.put("nether_wood", new Material("minecraft:wood", "WOOD"));
		materials.put("bamboo", new Material("minecraft:bamboo", "WOOD"));
		materials.put("wool", new Material("minecraft:wool", "WOOL"));
		materials.put("glass", new Material("minecraft:glass", "NONE"));
		materials.put("ice", new Material("minecraft:glass", "ICE"));
		materials.put("dirt", new Material("minecraft:gravel", "DIRT"));
		materials.put("soil", new Material("minecraft:gravel", "DIRT"));
		materials.put("grass", new Material("minecraft:grass", "GRASS"));
		materials.put("sand", new Material("minecraft:sand", "SAND"));
		materials.put("gravel", new Material("minecraft:gravel", "STONE"));
		materials.put("snow", new Material("minecraft:snow", "SNOW"));
		materials.put("plant", new Material("minecraft:grass", "PLANT"));
		materials.put("leaves", new Material("minecraft:grass", "PLANT"));
		materials.put("clay", new Material("minecraft:gravel", "CLAY"));
		materials.put("water", new Material("minecraft:stone", "WATER"));
		materials.put("lava", new Material("minecraft:stone", "FIRE"));
		return Map.copyOf(materials);
	}

	/**
	 * Old names already reported, so a pack full of them is mentioned once rather than once per block.
	 * Concurrent because addons are read on more than one thread.
	 */
	private static final Set<String> REPORTED = java.util.concurrent.ConcurrentHashMap.newKeySet();

	public static void block(JsonObject settings) {
		rename(settings, BLOCK_RENAMES, "block");
		material(settings);
		flip(settings, "solid", "translucent");

		// A block never had a fireproof flag of its own — the item does — and a light block is a block
		// with luminance, which is a setting rather than a kind of block.
		drop(settings, "is_light_block", "block");
		drop(settings, "fireproof", "block");
	}

	public static void item(JsonObject settings) {
		rename(settings, ITEM_RENAMES, "item");
	}

	private static void rename(JsonObject settings, Map<String, String> renames, String kind) {
		renames.forEach((oldName, newName) -> {
			if (!settings.has(oldName)) return;

			JsonElement value = settings.remove(oldName);
			// A file using both spellings meant the current one.
			if (!settings.has(newName)) {
				settings.add(newName, value);
				report(kind, oldName, "is now \"" + newName + "\"");
			} else {
				report(kind, oldName, "is now \"" + newName + "\", which this file already sets");
			}
		});
	}

	/**
	 * Reads the old {@code material} into the sound and map colour it used to imply. Anything the file
	 * says itself wins, since it was the more specific of the two even then.
	 */
	private static void material(JsonObject settings) {
		if (!settings.has("material")) return;

		JsonElement declared = settings.remove("material");
		if (declared == null || !declared.isJsonPrimitive() || !declared.getAsJsonPrimitive().isString()) {
			report("block", "material", "is gone; the game no longer has block materials");
			return;
		}

		String name = declared.getAsString();
		int colon = name.indexOf(':');
		Material material = MATERIALS.get((colon < 0 ? name : name.substring(colon + 1)).toLowerCase(Locale.ROOT));
		if (material == null) {
			report("block", "material", "is gone; the game no longer has block materials");
			return;
		}

		if (!settings.has("sound_group")) settings.add("sound_group", new JsonPrimitive(material.soundGroup()));
		if (!settings.has("map_color")) settings.add("map_color", new JsonPrimitive(material.mapColor()));
		report("block", "material", "is gone; read as its sound group and map colour instead");
	}

	/** An old flag that means the opposite of the one that replaced it. */
	private static void flip(JsonObject settings, String oldName, String newName) {
		if (!settings.has(oldName)) return;

		JsonElement value = settings.remove(oldName);
		if (!settings.has(newName) && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
			settings.add(newName, new JsonPrimitive(!value.getAsBoolean()));
		}
		report("block", oldName, "is now \"" + newName + "\", which means the opposite");
	}

	private static void drop(JsonObject settings, String name, String kind) {
		if (settings.remove(name) != null) report(kind, name, "is gone and does nothing");
	}

	private static void report(String kind, String oldName, String what) {
		if (REPORTED.add(kind + "." + oldName)) {
			Obsidian.LOGGER.warn("[Obsidian] The {} setting \"{}\" {}", kind, oldName, what);
		}
	}

}

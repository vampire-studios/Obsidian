package io.github.vampirestudios.obsidian.api.obsidian.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Serialized shape of an Obsidian biome modification.
 * Environment attributes deliberately remain JSON so Minecraft's native codec controls their types.
 */
public class BiomeModificationDefinition {

	public BiomeSelectorDefinition selector;
	public Additions add = new Additions();
	public Removals remove = new Removals();
	public Replacements replace = new Replacements();
	@SerializedName("set")
	public Overrides overrides = new Overrides();
	@SerializedName("environment_attributes")
	public JsonObject environmentAttributes;

	public boolean hasSpawnOperations() {
		return (add != null && !safe(add.spawns).isEmpty())
				|| (remove != null && (!safe(remove.spawns).isEmpty() || !safe(remove.spawnCategories).isEmpty()));
	}

	public boolean hasAnyChanges() {
		return (add != null && add.hasChanges())
				|| (remove != null && remove.hasChanges())
				|| (replace != null && replace.hasChanges())
				|| (overrides != null && overrides.hasChanges())
				|| environmentAttributes != null && !environmentAttributes.isEmpty();
	}

	public static class Additions {
		public List<FeatureEntry> features = List.of();
		public List<Identifier> carvers = List.of();
		public List<SpawnEntry> spawns = List.of();

		public boolean hasChanges() {
			return !safe(features).isEmpty() || !safe(carvers).isEmpty() || !safe(spawns).isEmpty();
		}
	}

	public static class Removals {
		public List<Identifier> features = List.of();
		public List<Identifier> carvers = List.of();
		public List<Identifier> spawns = List.of();
		@SerializedName("spawn_categories")
		public List<String> spawnCategories = List.of();

		public boolean hasChanges() {
			return !safe(features).isEmpty()
					|| !safe(carvers).isEmpty()
					|| !safe(spawns).isEmpty()
					|| !safe(spawnCategories).isEmpty();
		}
	}

	public static class Replacements {
		public List<FeatureReplacement> features = List.of();

		public boolean hasChanges() {
			return !safe(features).isEmpty();
		}
	}

	public static class Overrides {
		@SerializedName(value = "has_precipitation", alternate = "precipitation")
		public Boolean hasPrecipitation;
		public Float temperature;
		@SerializedName("temperature_modifier")
		public String temperatureModifier;
		public Float downfall;
		@SerializedName("water_color")
		public JsonElement waterColor;
		@SerializedName("grass_color")
		public JsonElement grassColor;
		@SerializedName("foliage_color")
		public JsonElement foliageColor;
		@SerializedName("dry_foliage_color")
		public JsonElement dryFoliageColor;
		@SerializedName("grass_color_modifier")
		public String grassColorModifier;

		public boolean hasChanges() {
			return hasPrecipitation != null
					|| temperature != null
					|| temperatureModifier != null
					|| downfall != null
					|| waterColor != null
					|| grassColor != null
					|| foliageColor != null
					|| dryFoliageColor != null
					|| grassColorModifier != null;
		}
	}

	public static class FeatureEntry {
		public String step;
		public Identifier feature;
	}

	public static class FeatureReplacement {
		public String step;
		public Identifier from;
		public Identifier to;
	}

	public static class SpawnEntry {
		public Identifier entity;
		public String category;
		public int weight = 10;
		public int min = 1;
		public int max = 1;
	}

	private static <T> List<T> safe(List<T> values) {
		return values != null ? values : List.of();
	}
}

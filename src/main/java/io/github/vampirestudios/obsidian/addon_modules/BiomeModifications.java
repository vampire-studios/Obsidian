package io.github.vampirestudios.obsidian.addon_modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.world.BiomeModificationDefinition;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;

/** Applies data-driven additions, removals, replacements and overrides to selected biomes. */
public class BiomeModifications implements AddonModule {

	private static final RegistryOps<JsonElement> CODEC_OPS = RegistryOps.create(
			JsonOps.INSTANCE,
			RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)
	);
	private static final String NATURAL_MOB_SPAWNS = "minecraft:gameplay/natural_mob_spawns";
	private static final String NATURAL_MOB_SPAWNS_DEFAULT_NAMESPACE = "gameplay/natural_mob_spawns";

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo addonInfo) throws IOException {
		try {
			JsonObject root = readRoot(addon, file);
			BiomeModificationDefinition definition = BaseGson.GSON.fromJson(root, BiomeModificationDefinition.class);
			if (definition == null) return;
			if (definition.selector == null) {
				throw new JsonParseException("Missing required 'selector' object");
			}
			if (!definition.hasAnyChanges()) {
				throw new JsonParseException("Biome modification does not contain any changes");
			}
			if (definition.hasSpawnOperations()
					&& definition.environmentAttributes != null
					&& (definition.environmentAttributes.has(NATURAL_MOB_SPAWNS)
					|| definition.environmentAttributes.has(NATURAL_MOB_SPAWNS_DEFAULT_NAMESPACE))) {
				throw new JsonParseException("Do not combine high-level spawn operations with '"
						+ NATURAL_MOB_SPAWNS + "' in the same biome modification");
			}

			Identifier modificationId = Identifier.fromNamespaceAndPath(
					addonInfo.modId(), AddonFormats.baseName(file));
			Predicate<net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext> selector =
					definition.selector.compile();
			EnvironmentAttributeMap attributes = parseEnvironmentAttributes(definition.environmentAttributes);
			validateDefinition(definition, modificationId);

			BiomeModification modification = net.fabricmc.fabric.api.biome.v1.BiomeModifications.create(modificationId);

			if (definition.add != null && definition.add.hasChanges()) {
				modification.add(ModificationPhase.ADDITIONS, selector,
						context -> applyAdditions(definition.add, context, modificationId));
			}
			if (definition.remove != null && definition.remove.hasChanges()) {
				modification.add(ModificationPhase.REMOVALS, selector,
						context -> applyRemovals(definition.remove, context, modificationId));
			}
			if (definition.replace != null && definition.replace.hasChanges()) {
				modification.add(ModificationPhase.REPLACEMENTS, selector,
						context -> applyReplacements(definition.replace, context));
			}

			if ((definition.overrides != null && definition.overrides.hasChanges())
					|| !attributes.equals(EnvironmentAttributeMap.EMPTY)) {
				modification.add(ModificationPhase.POST_PROCESSING, selector, context -> {
					if (definition.overrides != null) applyOverrides(definition.overrides, context);
					if (!attributes.equals(EnvironmentAttributeMap.EMPTY)) context.getAttributes().addAll(attributes);
				});
			}

			Obsidian.LOGGER.info("Registered biome modification {}.", modificationId);
		} catch (Exception e) {
			failedRegistering("biome modification", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "world/biome_modification";
	}

	private static void validateDefinition(BiomeModificationDefinition definition, Identifier modificationId) {
		if (definition.add != null) {
			for (BiomeModificationDefinition.FeatureEntry entry : safe(definition.add.features)) {
				if (entry == null || entry.feature == null) {
					throw new IllegalArgumentException("Biome modification " + modificationId + " has an addition with no feature");
				}
				decoration(entry.step);
			}
			for (Identifier carver : safe(definition.add.carvers)) {
				resourceKey(Registries.CARVER, carver, "carver");
			}
			for (BiomeModificationDefinition.SpawnEntry entry : safe(definition.add.spawns)) {
				validateSpawn(entry, modificationId);
			}
		}

		if (definition.remove != null) {
			for (Identifier feature : safe(definition.remove.features)) {
				resourceKey(Registries.PLACED_FEATURE, feature, "feature");
			}
			for (Identifier carver : safe(definition.remove.carvers)) {
				resourceKey(Registries.CARVER, carver, "carver");
			}
			for (Identifier entity : safe(definition.remove.spawns)) {
				if (entity == null) throw new IllegalArgumentException("Biome modification contains a null entity identifier");
			}
			for (String category : safe(definition.remove.spawnCategories)) mobCategory(category);
		}

		if (definition.replace != null) {
			for (BiomeModificationDefinition.FeatureReplacement replacement : safe(definition.replace.features)) {
				if (replacement == null || replacement.from == null || replacement.to == null) {
					throw new IllegalArgumentException("Feature replacements require both 'from' and 'to'");
				}
				decoration(replacement.step);
			}
		}

		if (definition.overrides != null) {
			BiomeModificationDefinition.Overrides overrides = definition.overrides;
			if (overrides.temperatureModifier != null) temperatureModifier(overrides.temperatureModifier);
			if (overrides.grassColorModifier != null) grassColorModifier(overrides.grassColorModifier);
			if (overrides.waterColor != null && !overrides.waterColor.isJsonNull()) rgb(overrides.waterColor);
			validateOptionalColor(overrides.grassColor);
			validateOptionalColor(overrides.foliageColor);
			validateOptionalColor(overrides.dryFoliageColor);
		}
	}

	private static void validateSpawn(BiomeModificationDefinition.SpawnEntry entry, Identifier modificationId) {
		if (entry == null || entry.entity == null) {
			throw new IllegalArgumentException("Biome modification " + modificationId + " contains a spawn with no entity");
		}
		MobCategory category = mobCategory(entry.category);
		if (category == MobCategory.MISC) {
			throw new IllegalArgumentException("Biome modification " + modificationId + " cannot add MISC spawns");
		}
		if (entry.weight <= 0 || entry.min <= 0 || entry.max < entry.min) {
			throw new IllegalArgumentException("Invalid spawn weight/group size for " + entry.entity
					+ " in biome modification " + modificationId);
		}
	}

	private static void applyAdditions(
			BiomeModificationDefinition.Additions additions,
			net.fabricmc.fabric.api.biome.v1.BiomeModificationContext context,
			Identifier modificationId
	) {
		for (BiomeModificationDefinition.FeatureEntry entry : safe(additions.features)) {
			if (entry == null || entry.feature == null) {
				throw new IllegalArgumentException("Biome modification " + modificationId + " has an addition with no feature");
			}
			context.getGenerationSettings().addFeature(
					decoration(entry.step),
					ResourceKey.create(Registries.PLACED_FEATURE, entry.feature));
		}
		for (Identifier carver : safe(additions.carvers)) {
			context.getGenerationSettings().addCarver(resourceKey(Registries.CARVER, carver, "carver"));
		}
		for (BiomeModificationDefinition.SpawnEntry entry : safe(additions.spawns)) {
			EntityType<?> entityType = entityType(entry.entity, modificationId);
			MobCategory category = mobCategory(entry.category);

			IntProvider count = entry.min == entry.max
					? ConstantInt.of(entry.min)
					: UniformInt.of(entry.min, entry.max);
			context.getMobSpawnSettings().addSpawn(
					category,
					new MobSpawnSettings.SpawnerData(entityType, count),
					entry.weight);
		}
	}

	private static void applyRemovals(
			BiomeModificationDefinition.Removals removals,
			net.fabricmc.fabric.api.biome.v1.BiomeModificationContext context,
			Identifier modificationId
	) {
		for (Identifier feature : safe(removals.features)) {
			context.getGenerationSettings().removeFeature(resourceKey(Registries.PLACED_FEATURE, feature, "feature"));
		}
		for (Identifier carver : safe(removals.carvers)) {
			context.getGenerationSettings().removeCarver(resourceKey(Registries.CARVER, carver, "carver"));
		}
		for (Identifier entity : safe(removals.spawns)) {
			context.getMobSpawnSettings().removeSpawnsOfEntityType(entityType(entity, modificationId));
		}
		for (String category : safe(removals.spawnCategories)) {
			context.getMobSpawnSettings().clearSpawns(mobCategory(category));
		}
	}

	private static void applyReplacements(
			BiomeModificationDefinition.Replacements replacements,
			net.fabricmc.fabric.api.biome.v1.BiomeModificationContext context
	) {
		for (BiomeModificationDefinition.FeatureReplacement replacement : safe(replacements.features)) {
			if (replacement == null || replacement.from == null || replacement.to == null) {
				throw new IllegalArgumentException("Feature replacements require both 'from' and 'to'");
			}
			context.getGenerationSettings().removeFeature(
					ResourceKey.create(Registries.PLACED_FEATURE, replacement.from));
			context.getGenerationSettings().addFeature(
					decoration(replacement.step),
					ResourceKey.create(Registries.PLACED_FEATURE, replacement.to));
		}
	}

	private static void applyOverrides(
			BiomeModificationDefinition.Overrides overrides,
			net.fabricmc.fabric.api.biome.v1.BiomeModificationContext context
	) {
		if (overrides.hasPrecipitation != null) context.getWeather().setPrecipitation(overrides.hasPrecipitation);
		if (overrides.temperature != null) context.getWeather().setTemperature(overrides.temperature);
		if (overrides.temperatureModifier != null) {
			context.getWeather().setTemperatureModifier(temperatureModifier(overrides.temperatureModifier));
		}
		if (overrides.downfall != null) context.getWeather().setDownfall(overrides.downfall);

		if (overrides.waterColor != null && !overrides.waterColor.isJsonNull()) {
			context.getEffects().setWaterColor(rgb(overrides.waterColor));
		}
		applyOptionalColor(overrides.grassColor,
				context.getEffects()::setGrassColorOverride,
				context.getEffects()::clearGrassColorOverride);
		applyOptionalColor(overrides.foliageColor,
				context.getEffects()::setFoliageColorOverride,
				context.getEffects()::clearFoliageColorOverride);
		applyOptionalColor(overrides.dryFoliageColor,
				context.getEffects()::setDryFoliageColorOverride,
				context.getEffects()::clearDryFoliageColorOverride);
		if (overrides.grassColorModifier != null) {
			context.getEffects().setGrassColorModifier(grassColorModifier(overrides.grassColorModifier));
		}
	}

	private static EnvironmentAttributeMap parseEnvironmentAttributes(JsonObject attributes) {
		if (attributes == null || attributes.isEmpty()) return EnvironmentAttributeMap.EMPTY;
		return EnvironmentAttributeMap.CODEC.parse(CODEC_OPS, attributes).getOrThrow();
	}

	private static JsonObject readRoot(IAddonPack addon, File file) throws IOException {
		JsonElement parsed = AddonFormats.readTree(addon, file);
		if (parsed == null || !parsed.isJsonObject()) {
			throw new JsonParseException("Biome modification root must be an object");
		}
		return parsed.getAsJsonObject();
	}

	private static GenerationStep.Decoration decoration(String name) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Feature entry is missing its generation 'step'");
		}
		for (GenerationStep.Decoration value : GenerationStep.Decoration.values()) {
			if (value.getSerializedName().equalsIgnoreCase(name)) return value;
		}
		throw new IllegalArgumentException("Unknown biome generation step '" + name + "'");
	}

	private static MobCategory mobCategory(String name) {
		if (name == null || name.isBlank()) throw new IllegalArgumentException("Spawn entry is missing its category");
		for (MobCategory value : MobCategory.values()) {
			if (value.getSerializedName().equalsIgnoreCase(name)) return value;
		}
		throw new IllegalArgumentException("Unknown mob category '" + name + "'");
	}

	private static Biome.TemperatureModifier temperatureModifier(String name) {
		for (Biome.TemperatureModifier value : Biome.TemperatureModifier.values()) {
			if (value.getSerializedName().equalsIgnoreCase(name)) return value;
		}
		throw new IllegalArgumentException("Unknown temperature modifier '" + name + "'");
	}

	private static BiomeSpecialEffects.GrassColorModifier grassColorModifier(String name) {
		for (BiomeSpecialEffects.GrassColorModifier value : BiomeSpecialEffects.GrassColorModifier.values()) {
			if (value.getSerializedName().equalsIgnoreCase(name)) return value;
		}
		throw new IllegalArgumentException("Unknown grass color modifier '" + name + "'");
	}

	private static EntityType<?> entityType(Identifier identifier, Identifier modificationId) {
		if (identifier == null || !BuiltInRegistries.ENTITY_TYPE.containsKey(identifier)) {
			throw new IllegalArgumentException("Unknown entity type '" + identifier
					+ "' in biome modification " + modificationId);
		}
		return BuiltInRegistries.ENTITY_TYPE.getValue(identifier);
	}

	private static <T> ResourceKey<T> resourceKey(
			ResourceKey<? extends net.minecraft.core.Registry<T>> registry,
			Identifier identifier,
			String type
	) {
		if (identifier == null) throw new IllegalArgumentException("Biome modification contains a null " + type + " identifier");
		return ResourceKey.create(registry, identifier);
	}

	private static void applyOptionalColor(JsonElement value, java.util.function.IntConsumer setter, Runnable clearer) {
		if (value == null || value.isJsonNull()) return;
		if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
			String string = value.getAsString();
			if (string.equalsIgnoreCase("default") || string.equalsIgnoreCase("clear")) {
				clearer.run();
				return;
			}
		}
		setter.accept(rgb(value));
	}

	private static void validateOptionalColor(JsonElement value) {
		if (value == null || value.isJsonNull()) return;
		if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
			String string = value.getAsString();
			if (string.equalsIgnoreCase("default") || string.equalsIgnoreCase("clear")) return;
		}
		rgb(value);
	}

	private static int rgb(JsonElement value) {
		if (!value.isJsonPrimitive()) {
			throw new IllegalArgumentException("RGB color must be a number or a #RRGGBB string");
		}
		if (value.getAsJsonPrimitive().isNumber()) {
			long color = value.getAsLong();
			if (color < 0 || color > 0xFFFFFFL) {
				throw new IllegalArgumentException("RGB color must be between 0 and 16777215");
			}
			return (int) color;
		}

		String original = value.getAsString();
		String normalized = original.trim().toLowerCase(Locale.ROOT);
		if (normalized.startsWith("#")) normalized = normalized.substring(1);
		else if (normalized.startsWith("0x")) normalized = normalized.substring(2);

		try {
			long color = normalized.matches("[0-9a-f]{6}")
					? Long.parseLong(normalized, 16)
					: Long.parseLong(normalized);
			if (color < 0 || color > 0xFFFFFFL) throw new NumberFormatException();
			return (int) color;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid RGB color '" + original + "'; expected #RRGGBB or 0-16777215");
		}
	}

	private static <T> List<T> safe(List<T> values) {
		return values != null ? values : List.of();
	}
}

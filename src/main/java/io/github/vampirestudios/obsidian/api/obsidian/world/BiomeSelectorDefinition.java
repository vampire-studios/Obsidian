package io.github.vampirestudios.obsidian.api.obsidian.world;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Data-driven selector shared by biome modifications and future world-generation modules.
 * Biome entries may be exact identifiers or tags prefixed with {@code #}.
 */
public class BiomeSelectorDefinition {

	@SerializedName(value = "all", alternate = "all_biomes")
	public boolean all;
	public List<String> biomes = List.of();
	public List<String> exclude = List.of();
	public List<Identifier> dimensions = List.of();

	public Predicate<BiomeSelectionContext> compile() {
		List<Target> included = compileTargets(biomes, "selector.biomes");
		List<Target> excluded = compileTargets(exclude, "selector.exclude");
		List<ResourceKey<LevelStem>> dimensionKeys = new ArrayList<>();

		for (Identifier dimension : safe(dimensions)) {
			if (dimension == null) {
				throw new IllegalArgumentException("selector.dimensions contains an invalid identifier");
			}
			dimensionKeys.add(ResourceKey.create(Registries.LEVEL_STEM, dimension));
		}

		if (all && !included.isEmpty()) {
			throw new IllegalArgumentException("selector cannot declare both 'all' and 'biomes'");
		}
		if (!all && included.isEmpty()) {
			throw new IllegalArgumentException("selector must declare 'biomes', or explicitly set 'all' to true");
		}

		return context -> {
			if (!dimensionKeys.isEmpty() && dimensionKeys.stream().noneMatch(context::canGenerateIn)) {
				return false;
			}
			if (!all && included.stream().noneMatch(target -> target.matches(context))) {
				return false;
			}
			return excluded.stream().noneMatch(target -> target.matches(context));
		};
	}

	private static List<Target> compileTargets(List<String> values, String field) {
		List<Target> targets = new ArrayList<>();
		for (String value : safe(values)) {
			if (value == null || value.isBlank()) {
				throw new IllegalArgumentException(field + " contains an empty biome target");
			}

			boolean tag = value.charAt(0) == '#';
			String rawIdentifier = tag ? value.substring(1) : value;
			Identifier identifier = Identifier.tryParse(rawIdentifier);
			if (identifier == null) {
				throw new IllegalArgumentException("Invalid biome target '" + value + "' in " + field);
			}

			targets.add(tag
					? new TagTarget(TagKey.create(Registries.BIOME, identifier))
					: new BiomeTarget(identifier));
		}
		return targets;
	}

	private static <T> List<T> safe(List<T> values) {
		return values != null ? values : List.of();
	}

	private interface Target {
		boolean matches(BiomeSelectionContext context);
	}

	private record BiomeTarget(Identifier identifier) implements Target {
		@Override
		public boolean matches(BiomeSelectionContext context) {
			return context.getBiomeKey().identifier().equals(identifier);
		}
	}

	private record TagTarget(TagKey<Biome> tag) implements Target {
		@Override
		public boolean matches(BiomeSelectionContext context) {
			return context.hasTag(tag);
		}
	}
}

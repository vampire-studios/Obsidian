package io.github.vampirestudios.obsidian;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BiomeUtils {

	public static void addFeatureToBiome(Biome biome, GenerationStep.Feature feature, ConfiguredFeature<?, ?> configuredFeature) {
		convertImmutableFeatures(biome);
		List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = biome.getGenerationSettings().features;
		while (biomeFeatures.size() <= feature.ordinal()) {
			biomeFeatures.add(Lists.newArrayList());
		}
		biomeFeatures.get(feature.ordinal()).add(() -> configuredFeature);
	}

	private static void convertImmutableFeatures(Biome biome) {
		if (biome.getGenerationSettings().features instanceof ImmutableList) {
			biome.getGenerationSettings().features = biome.getGenerationSettings().features.stream().map(Lists::newArrayList).collect(Collectors.toList());
		}
	}

	public static int calcSkyColor(float f) {
		float g = f / 3.0F;
		g = MathHelper.clamp(g, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - g * 0.05F, 0.5F + g * 0.1F, 1.0F);
	}

}
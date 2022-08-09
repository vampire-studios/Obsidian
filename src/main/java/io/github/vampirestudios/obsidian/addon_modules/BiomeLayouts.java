package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.biomeLayouts.BiomeLayout;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;

public class BiomeLayouts implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		BiomeLayout biomeLayout = Obsidian.GSON.fromJson(new FileReader(file), BiomeLayout.class);
		try {
			if (biomeLayout == null) return;
			/*BiomeProviders.register(new BiomeProvider(new Identifier(id.getModId(), "custom_biomes"), 3) {
				@Override
				public void addOverworldBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, RegistryKey<Biome>>> mapper) {
					if (biomeLayout.dimensionType.equals(BiomeLayout.DimensionType.OVERWORLD)) {
						addCustomBiomes(registry, mapper);
					}
				}

				@Override
				public void addNetherBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, RegistryKey<Biome>>> mapper) {
					if (biomeLayout.dimensionType.equals(BiomeLayout.DimensionType.NETHER)) {
						addCustomBiomes(registry, mapper);
					}
				}

				public void addCustomBiomes(Registry<Biome> registry, Consumer<Pair<TBClimate.ParameterPoint, RegistryKey<Biome>>> mapper) {
					for (BiomeLayout.BiomeInformation biomeInformation : biomeLayout.biomes) {
						if (biomeInformation.type.equals(BiomeLayout.BiomeInformation.BiomeSpawnType.SIMILAR)) {
							if (registry.containsId(biomeInformation.name)
									&& registry.containsId(biomeInformation.similarBiomeName)) {
								Biome biome = registry.get(biomeInformation.name);
								Biome similarBiome = registry.get(biomeInformation.similarBiomeName);
								Optional<RegistryKey<Biome>> biomeKey = registry.getKey(biome);
								Optional<RegistryKey<Biome>> similarBiomeKey = registry.getKey(similarBiome);
								if (biomeKey.isPresent() && similarBiomeKey.isPresent()) {
									addBiomeSimilar(mapper, similarBiomeKey.get(), biomeKey.get());
								}
							}
						} else {
							if (registry.containsId(biomeInformation.name)) {
								Biome biome = registry.get(biomeInformation.name);
								Optional<RegistryKey<Biome>> biomeKey = registry.getKey(biome);
								if (biomeKey.isPresent()) {
									MultiNoise multiNoise = biomeInformation.multiNoise;
									if (biomeInformation.multiNoise.floatValues) {
										addBiome(mapper, TBClimate.parameters(
												MultiNoiseUtil.ParameterRange.of(multiNoise.temperatureValue),
												MultiNoiseUtil.ParameterRange.of(multiNoise.humidityValue),
												MultiNoiseUtil.ParameterRange.of(multiNoise.continentalnessValue),
												MultiNoiseUtil.ParameterRange.of(multiNoise.erosionValue),
												MultiNoiseUtil.ParameterRange.of(multiNoise.depthValue),
												MultiNoiseUtil.ParameterRange.of(multiNoise.weirdnessValue),
												getUniquenessParameter(),
												multiNoise.offset
										), biomeKey.get());
									} else {
										addBiome(mapper, TBClimate.parameters(
												multiNoise.temperatureNoise.getValue(),
												multiNoise.humidityNoise.getValue(),
												multiNoise.continentalnessNoise.getValue(),
												multiNoise.erosionNoise.getValue(),
												multiNoise.depthNoise.getValue(),
												multiNoise.weirdnessNoise.getValue(),
												getUniquenessParameter(),
												multiNoise.offset
										), biomeKey.get());
									}
								}
							}
						}
					}
				}
			});*/
//			register(ApiElements.BIOME_LAYOUTS, "biome_layout", biomeLayout.regionName, biomeLayout);
		} catch (Exception e) {
			failedRegistering("biome_layout", biomeLayout.regionName, e);
		}
	}

	@Override
	public String getType() {
		return "biome_layouts";
	}
}
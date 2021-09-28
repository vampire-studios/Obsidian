package io.github.vampirestudios.obsidian.api.obsidian.block;

import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.Optional;
import java.util.function.Predicate;

public class OreInformation {

    public String test_type;
    public TargetState target_state;
    public Identifier[] biomes;
    public String spawnPredicate;
    public String[] biomeCategories;
    public int size = 17;
    public int repeat_amount = 30;
    public OreRangeConfig config;
    public boolean survivesExplosion = true;

    public Predicate<BiomeSelectionContext> biomeSelector() {
        Predicate<BiomeSelectionContext> predicate;
        switch (spawnPredicate) {
            default -> predicate = BiomeSelectors.all();
            case "built_in" -> predicate = BiomeSelectors.builtIn();
            case "vanilla" -> predicate = BiomeSelectors.vanilla();
            case "overworld" -> predicate = BiomeSelectors.foundInOverworld();
            case "the_nether" -> predicate = BiomeSelectors.foundInTheNether();
            case "the_end" -> predicate = BiomeSelectors.foundInTheEnd();
            case "categories" -> {
                if (biomeCategories != null) {
                    Biome.Category[] categories = new Biome.Category[biomeCategories.length];
                    for (int i = 0; i < biomeCategories.length; i++) {
                        categories[i] = Biome.Category.byName(biomeCategories[i]);
                    }
                    predicate = BiomeSelectors.categories(Utils.stripNulls(categories));
                }
                else predicate = BiomeSelectors.categories();
            }
            case "biomes" -> {
                if (biomes != null) {
                    RegistryKey<Biome>[] biomeArray = new RegistryKey[biomes.length];
                    for (int i = 0; i < biomes.length; i++) {
                        Biome actualBiome = BuiltinRegistries.BIOME.get(biomes[i]);
                        Optional<RegistryKey<Biome>> optional = BuiltinRegistries.BIOME.getKey(actualBiome);
                        if (optional.isPresent()) biomeArray[i] = optional.get();
                    }
                    predicate = BiomeSelectors.includeByKey(Utils.stripNulls(biomeArray));
                }
                else predicate = BiomeSelectors.includeByKey();
            }
        }
        return predicate;
    }

}

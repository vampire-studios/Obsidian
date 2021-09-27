package io.github.vampirestudios.obsidian.api.obsidian.block;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;

import java.util.function.Predicate;

public class OreInformation {

    public String test_type;
    public TargetState target_state;
    public Identifier[] biomes;
    public String spawnPredicate;
    public String[] biomeCategories;
    public int size;
    public OreRangeConfig config;
    
    public Predicate<BiomeSelectionContext> biomeSelector() {
        Predicate<BiomeSelectionContext> predicate = null;
        switch(spawnPredicate) {
            default:
            case "all":
                predicate = BiomeSelectors.all();
                break;
            case "builtIn":
                predicate = BiomeSelectors.builtIn();
                break;
            case "vanilla":
                predicate = BiomeSelectors.vanilla();
                break;
            case "foundInOverworld":
                predicate = BiomeSelectors.foundInOverworld();
                break;
            case "foundInTheNether":
                predicate = BiomeSelectors.foundInTheNether();
                break;
            case "foundInTheEnd":
                predicate = BiomeSelectors.foundInTheEnd();
                break;
            case "categories": {
                if (biomeCategories != null) {
                    for (String category : biomeCategories) predicate = BiomeSelectors.categories(Biome.Category.byName(category));
                } else {
                    predicate = BiomeSelectors.categories();
                }
                break;
            }
            case "biomes": {
                if (biomes != null) {
                    for (Identifier biome : biomes) {
                        Biome actualBiome = BuiltinRegistries.BIOME.get(biome);
                        predicate = BiomeSelectors.includeByKey(BuiltinRegistries.BIOME.getKey(actualBiome).get());
                    }
                } else {
                    predicate = BiomeSelectors.includeByKey();
                }
                break;
            }
        }
        return predicate;
    }

}

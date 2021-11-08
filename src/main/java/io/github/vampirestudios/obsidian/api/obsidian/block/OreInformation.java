package io.github.vampirestudios.obsidian.api.obsidian.block;

import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.structure.rule.*;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.heightprovider.TrapezoidHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class OreInformation {

    public String test_type = "tag";
    public TargetState target_state;
    public Identifier[] biomes;
    public boolean triangleRange = false;
    public int plateau = 0;
    public String spawnPredicate = "built_in";
    public String[] biomeCategories;
    public int size = 17;
    public int chance = 30;
    public boolean survivesExplosion = true;
    public CustomYOffset topOffset;
    public CustomYOffset bottomOffset;

    public RangeDecoratorConfig rangeConfig() {
        return new RangeDecoratorConfig(
                triangleRange ? TrapezoidHeightProvider.create(bottomOffset.yOffset(), topOffset.yOffset(), plateau)
                        : UniformHeightProvider.create(bottomOffset.yOffset(), topOffset.yOffset())
        );
    }

    public RuleTest ruleTest() {
        return switch (test_type) {
            case "tag" -> new TagMatchRuleTest(/*BlockTags.getTagGroup().getTag(*//*target_state.tag*//*new Identifier("base_stone_overworld"))*/BlockTags.BASE_STONE_OVERWORLD);
            case "always_true" -> AlwaysTrueRuleTest.INSTANCE;
            case "block_match" -> new BlockMatchRuleTest(Registry.BLOCK.get(target_state.block));
            case "block_state_match" -> new BlockStateMatchRuleTest(ObsidianAddonLoader.getState(Registry.BLOCK.get(target_state.block), target_state.properties));
            case "random_block_match" -> new RandomBlockMatchRuleTest(Registry.BLOCK.get(target_state.block), target_state.probability);
            case "random_block_state_match" -> new RandomBlockStateMatchRuleTest(ObsidianAddonLoader.getState(Registry.BLOCK.get(target_state.block), target_state.properties), target_state.probability);
            default -> throw new IllegalStateException("Unexpected value: " + test_type);
        };
    }

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
                } else predicate = BiomeSelectors.categories();
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
                } else predicate = BiomeSelectors.includeByKey();
            }
        }
        return predicate;
    }

    protected static class CustomYOffset {
        public String type = "fixed";
        public int offset = 0;

        public YOffset yOffset() {
            return switch (type) {
                case "fixed" -> new YOffset.Fixed(offset);
                case "above_bottom" -> new YOffset.AboveBottom(offset);
                case "below_top" -> new YOffset.BelowTop(offset);
                default -> throw new IllegalArgumentException();
            };
        }
    }

    protected static class TargetState {

        public Identifier block;
        public Identifier tag = new Identifier("base_stone_overworld");
        public Map<String, String> properties;
        public float probability;

    }

}

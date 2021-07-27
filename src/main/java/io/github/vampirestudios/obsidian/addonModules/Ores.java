package io.github.vampirestudios.obsidian.addonModules;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.BlockImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.CustomBlockItem;
import io.github.vampirestudios.obsidian.threadhandlers.data.BlockInitThread;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.item.Item;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Ores implements AddonModule {

    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        io.github.vampirestudios.obsidian.api.obsidian.block.Block block = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
        try {
            if (block == null) return;
            FabricBlockSettings blockSettings = FabricBlockSettings.of(block.information.getMaterial()).sounds(block.information.getBlockSoundGroup())
                    .strength(block.information.hardness, block.information.resistance).drops(block.information.drop)
                    .slipperiness(block.information.slipperiness).emissiveLighting((state, world, pos) -> block.information.is_emissive)
                    .nonOpaque();
            net.minecraft.block.Block blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new BlockImpl(block, blockSettings), block.information.name.id.getPath());
            REGISTRY_HELPER.registerItem(new CustomBlockItem(block, blockImpl, new Item.Settings().group(block.information.getItemGroup())), block.information.name.id.getPath());

            RuleTest test;
            if (block.ore_information.test_type.equals("tag")) {
                Tag<net.minecraft.block.Block> tag = BlockTags.getTagGroup().getTag(block.ore_information.target_state.block);
                test = new TagMatchRuleTest(tag == null ? BlockTags.BASE_STONE_OVERWORLD : tag);
            } else if (block.ore_information.test_type.equals("blockstate")) {
                test = new BlockStateMatchRuleTest(getState(Registry.BLOCK.get(block.ore_information.target_state.block), block.ore_information.target_state.properties));
            } else {
                test = new BlockMatchRuleTest(Registry.BLOCK.get(block.ore_information.target_state.block));
            }
            ConfiguredFeature<?, ?> feature = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Utils.appendToPath(block.information.name.id, "_ore_feature"),
                    Feature.ORE.configure(
                            new OreFeatureConfig(
                                    test,
                                    blockImpl.getDefaultState(),
                                    block.ore_information.size
                            )
                    ).decorate(
                            Decorator.RANGE.configure(
                                    new RangeDecoratorConfig(UniformHeightProvider.create(
                                            YOffset.fixed(block.ore_information.config.bottom_offset),
                                            YOffset.fixed(block.ore_information.config.top_offset)
                                    ))
                            )
                    ).spreadHorizontally().repeat(20));
            BuiltinRegistries.BIOME.forEach(biome -> {
                if (block.ore_information.biomes != null) {
                    for (String biome2 : block.ore_information.biomes) {
                        BiomeModifications.addFeature(biomeSelectionContext -> BuiltinRegistries.BIOME.getId(biome).toString().equals(biome2),
                                GenerationStep.Feature.UNDERGROUND_ORES, BuiltinRegistries.CONFIGURED_FEATURE.getKey(feature).get());
                    }
                } else {
//                    BiomeUtils.addFeatureToBiome(biome, GenerationStep.Feature.UNDERGROUND_ORES, feature);
//                    BiomeModifications.addFeature(BiomeSelectors.,
//                            GenerationStep.Feature.UNDERGROUND_ORES, BuiltinRegistries.CONFIGURED_FEATURE.getKey(feature).get());
                }
            });
            Artifice.registerDataPack(String.format("%s:%s_data", block.information.name.id.getNamespace(), block.information.name.id.getPath()), serverResourcePackBuilder ->
                    serverResourcePackBuilder.addLootTable(block.information.name.id, lootTableBuilder -> {
                        lootTableBuilder.type(new Identifier("block"));
                        lootTableBuilder.pool(pool -> {
                            pool.rolls(1);
                            pool.entry(entry -> {
                                entry.type(new Identifier("item"));
                                entry.name(block.information.name.id);
                            });
                            pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {

                            });
                        });
                    })
            );

            if (!addon.getConfigPackInfo().hasData) {
                new BlockInitThread(block);
            }

            register(ORES, "ore", block.information.name.id, block);
        } catch (Exception e) {
            failedRegistering("ore", block.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "blocks/ores";
    }

}

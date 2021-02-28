package io.github.vampirestudios.obsidian.addonModules;

import com.google.gson.JsonObject;
import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.BiomeUtils;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class Blocks implements AddonModule {

	@Override
	public void init(File file, ModIdAndAddonPath id) throws FileNotFoundException {
		io.github.vampirestudios.obsidian.api.obsidian.block.Block block = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
		try {
			FabricBlockSettings blockSettings = FabricBlockSettings.of(block.information.getMaterial()).sounds(block.information.getBlockSoundGroup())
					.strength(block.information.destroy_time, block.information.explosion_resistance).drops(block.information.drop)
					.collidable(block.information.collidable).slipperiness(block.information.slipperiness).emissiveLighting((state, world, pos) ->
							block.information.is_emissive).nonOpaque();
			if (block.information.dynamicBounds) {
				blockSettings.dynamicBounds();
			}
			if (block.information.randomTicks) {
				blockSettings.ticksRandomly();
			}
			if (block.information.translucent) {
				blockSettings.nonOpaque();
			}
			if (block.information.is_bouncy) {
				blockSettings.jumpVelocityMultiplier(block.information.jump_velocity_modifier);
			}
			if (block.information.is_light_block) {
				blockSettings.lightLevel(value -> block.information.luminance);
			}
			net.minecraft.block.Block blockImpl;
			if(block.additional_information != null) {
				if(block.additional_information.rotatable) {
					blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new FacingBlockImpl(block, blockSettings), block.information.name.id.getPath());
				} else if(block.additional_information.horizontal_rotatable) {
					blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new HorizontalFacingBlockImpl(block, blockSettings), block.information.name.id.getPath());
				} else if(block.additional_information.pillar) {
					blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new PillarBlockImpl(block, blockSettings), block.information.name.id.getPath());
				} else if(block.additional_information.path) {
					blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new PathBlockImpl(blockSettings, block), block.information.name.id.getPath());
				} else if(block.additional_information.lantern) {
					blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new LanternBlock(blockSettings), block.information.name.id.getPath());
				} else if(block.additional_information.barrel) {
					blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new BarrelBlock(blockSettings), block.information.name.id.getPath());
				} else if(block.additional_information.leaves) {
					blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new LeavesBaseBlock(), block.information.name.id.getPath());
				} else if(block.additional_information.chains) {
					blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new ChainBlock(blockSettings), block.information.name.id.getPath());
				} else if(block.additional_information.cake_like) {
					blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new CakeBlockImpl(block), block.information.name.id.getPath());
				} else {
					blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new BlockImpl(block, blockSettings), block.information.name.id.getPath());
				}
			} else {
				blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new BlockImpl(block, blockSettings), block.information.name.id.getPath());
			}
			Item.Settings settings = new Item.Settings().group(block.information.getItemGroup());
			if (block.food_information != null) {
				FoodComponent foodComponent = block.food_information.getBuilder().build();
				settings.food(foodComponent);
			}
			if (block.information.fireproof) {
				settings.fireproof();
			}
			REGISTRY_HELPER.registerItem(new CustomBlockItem(block, blockImpl, settings), block.information.name.id.getPath());
			net.minecraft.block.Block finalBlockImpl = blockImpl;

			if (block.ore_information != null) {
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
										finalBlockImpl.getDefaultState(),
										block.ore_information.size
								)
						).decorate(
								Decorator.RANGE.configure(
										new RangeDecoratorConfig(
												YOffset.fixed(block.ore_information.config.bottom_offset),
												YOffset.fixed(block.ore_information.config.top_offset)
										)
								)
						).rangeOf(YOffset.fixed(block.ore_information.config.minimum), YOffset.fixed(block.ore_information.config.maximum)).spreadHorizontally().repeat(20));
				BuiltinRegistries.BIOME.forEach(biome -> {
					if (block.ore_information.biomes != null) {
						for (String biome2 : block.ore_information.biomes) {
							if (BuiltinRegistries.BIOME.getId(biome).toString().equals(biome2)) {
								BiomeUtils.addFeatureToBiome(biome, GenerationStep.Feature.UNDERGROUND_ORES, feature);
							}
						}
					} else {
						BiomeUtils.addFeatureToBiome(biome, GenerationStep.Feature.UNDERGROUND_ORES, feature);
					}
				});
			}
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
			if (block.additional_information != null) {
				if (block.additional_information.slab) {
					REGISTRY_HELPER.registerBlock(new SlabImpl(block),
							Utils.appendToPath(block.information.name.id, "_slab").getPath(), ItemGroup.BUILDING_BLOCKS);
					Artifice.registerDataPack(String.format("%s:%s_slab_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
						serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_slab"), lootTableBuilder -> {
							lootTableBuilder.type(new Identifier("block"));
							lootTableBuilder.pool(pool -> {
								pool.rolls(1);
								pool.entry(entry -> {
									entry.type(new Identifier("item"));
									entry.function(new Identifier("set_count"), function ->
											function.condition(new Identifier("block_state_property"), jsonObjectBuilder -> {
												jsonObjectBuilder.add("block", Utils.appendToPath(block.information.name.id, "_slab").toString());
												JsonObject property = new JsonObject();
												property.addProperty("type", "double");
												jsonObjectBuilder.add("property", property);
											})
									);
									entry.weight(2);
									entry.function(new Identifier("explosion_decay"), function -> { });
									entry.name(Utils.appendToPath(block.information.name.id, "_slab"));
								});
								pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> { });
							});
						});
						serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name.id, "_slab"), shapedRecipeBuilder -> {
							shapedRecipeBuilder.group(new Identifier(id.getModId(), "slabs"));
							shapedRecipeBuilder.pattern(
									"###"
							);
							shapedRecipeBuilder.ingredientItem('#', block.information.name.id);
							shapedRecipeBuilder.result(Utils.appendToPath(block.information.name.id, "_slab"), 6);
						});
					});
				}
				if (block.additional_information.stairs) {
					REGISTRY_HELPER.registerBlock(new StairsImpl(block), new Identifier(id.getModId(), block.information.name.id.getPath() + "_stairs").getPath(),
							ItemGroup.BUILDING_BLOCKS);
					Artifice.registerDataPack(String.format("%s:%s_stairs_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
						serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_stairs"), lootTableBuilder -> {
							lootTableBuilder.type(new Identifier("block"));
							lootTableBuilder.pool(pool -> {
								pool.rolls(1);
								pool.entry(entry -> {
									entry.type(new Identifier("item"));
									entry.name(Utils.appendToPath(block.information.name.id, "_stairs"));
								});
								pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {

								});
							});
						});
						serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name.id, "_stairs"), shapedRecipeBuilder -> {
							shapedRecipeBuilder.group(new Identifier(id.getModId(), "stairs"));
							shapedRecipeBuilder.pattern(
									"#  ",
									"## ",
									"###"
							);
							shapedRecipeBuilder.ingredientItem('#', block.information.name.id);
							shapedRecipeBuilder.result(Utils.appendToPath(block.information.name.id, "_stairs"), 4);
						});
					});
				}
				if (block.additional_information.fence) {
					REGISTRY_HELPER.registerBlock(new FenceImpl(block),
							new Identifier(id.getModId(), block.information.name.id.getPath() + "_fence").getPath(), ItemGroup.DECORATIONS);
					Artifice.registerDataPack(String.format("%s:%s_fence_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
						serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_fence"), lootTableBuilder -> {
							lootTableBuilder.type(new Identifier("block"));
							lootTableBuilder.pool(pool -> {
								pool.rolls(1);
								pool.entry(entry -> {
									entry.type(new Identifier("item"));
									entry.name(Utils.appendToPath(block.information.name.id, "_fence"));
								});
								pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {

								});
							});
						});
						serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name.id, "_fence"), shapedRecipeBuilder -> {
							shapedRecipeBuilder.group(new Identifier(id.getModId(), "fences"));
							shapedRecipeBuilder.pattern(
									"W#W",
									"W#W"
							);
							shapedRecipeBuilder.ingredientItem('W', block.information.name.id);
							shapedRecipeBuilder.ingredientItem('#', new Identifier("stick"));
							shapedRecipeBuilder.result(Utils.appendToPath(block.information.name.id, "_fence"), 3);
						});
					});
				}
				if (block.additional_information.fenceGate) {
					REGISTRY_HELPER.registerBlock(new FenceGateImpl(block),
							Utils.appendToPath(block.information.name.id, "_fence_gate").getPath(), ItemGroup.REDSTONE);
					Artifice.registerDataPack(String.format("%s:%s_fence_gate_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
						serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_fence_gate"), lootTableBuilder -> {
							lootTableBuilder.type(new Identifier("block"));
							lootTableBuilder.pool(pool -> {
								pool.rolls(1);
								pool.entry(entry -> {
									entry.type(new Identifier("item"));
									entry.name(new Identifier(id.getModId(), block.information.name.id.getPath() + "_fence_gate"));
								});
								pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> { });
							});
						});
						serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name.id, "_fence_gate"), shapedRecipeBuilder -> {
							shapedRecipeBuilder.group(new Identifier(id.getModId(), "fence_gates"));
							shapedRecipeBuilder.pattern(
									"#W#",
									"#W#"
							);
							shapedRecipeBuilder.ingredientItem('W', block.information.name.id);
							shapedRecipeBuilder.ingredientItem('#', new Identifier("stick"));
							shapedRecipeBuilder.result(Utils.appendToPath(block.information.name.id, "_fence_gate"), 3);
						});
					});
				}
				if (block.additional_information.walls) {
					REGISTRY_HELPER.registerBlock(new WallImpl(block),
							Utils.appendToPath(block.information.name.id, "_wall").getPath(), ItemGroup.DECORATIONS);
					Artifice.registerDataPack(String.format("%s:%s_wall_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder ->
							serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_wall"), lootTableBuilder -> {
								lootTableBuilder.type(new Identifier("block"));
								lootTableBuilder.pool(pool -> {
									pool.rolls(1);
									pool.entry(entry -> {
										entry.type(new Identifier("item"));
										entry.name(Utils.appendToPath(block.information.name.id, "_wall"));
									});
									pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> { });
								});
							})
					);
				}
			}
			register(BLOCKS, "block", block.information.name.id.toString(), block);
		} catch (Exception e) {
			failedRegistering("block", block.information.name.id.toString(), e);
		}
	}

	@Override
	public String getType() {
		return "blocks";
	}

}

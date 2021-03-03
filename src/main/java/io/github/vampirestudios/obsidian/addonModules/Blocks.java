package io.github.vampirestudios.obsidian.addonModules;

import com.google.gson.JsonObject;
import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.Utils;
import io.github.vampirestudios.vampirelib.blocks.CustomLadderBlock;
import io.github.vampirestudios.vampirelib.blocks.DoorBaseBlock;
import io.github.vampirestudios.vampirelib.blocks.TorchBaseBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class Blocks implements AddonModule {

	@Override
	public void init(File file, ModIdAndAddonPath id) throws FileNotFoundException {
		io.github.vampirestudios.obsidian.api.obsidian.block.Block block = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
		try {
			if(block == null) return;
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

			Item.Settings settings = new Item.Settings().group(block.information.getItemGroup());
			if (block.food_information != null) {
				FoodComponent foodComponent = block.food_information.getBuilder().build();
				settings.food(foodComponent);
			}
			if (block.information.fireproof) {
				settings.fireproof();
			}

			if (block.block_type == null) {
				if(block.additional_information != null) {
					if(block.additional_information.rotatable) {
						REGISTRY_HELPER.registerBlock(new FacingBlockImpl(block, blockSettings), block.information.name.id.getPath(), settings);
					} else if(block.additional_information.horizontal_rotatable) {
						REGISTRY_HELPER.registerBlock(new HorizontalFacingBlockImpl(block, blockSettings), block.information.name.id.getPath(), settings);
					} else if(block.additional_information.pillar) {
						REGISTRY_HELPER.registerBlock(new PillarBlockImpl(block, blockSettings), block.information.name.id.getPath(), settings);
					} else if(block.additional_information.path) {
						REGISTRY_HELPER.registerBlock(new PathBlockImpl(blockSettings, block), block.information.name.id.getPath(), settings);
					} else if(block.additional_information.lantern) {
						REGISTRY_HELPER.registerBlock(new LanternBlock(blockSettings), block.information.name.id.getPath(), settings);
					} else if(block.additional_information.barrel) {
						REGISTRY_HELPER.registerBlock(new BarrelBlock(blockSettings), block.information.name.id.getPath(), settings);
					} else if(block.additional_information.leaves) {
						REGISTRY_HELPER.registerBlock(new LeavesBaseBlock(), block.information.name.id.getPath(), settings);
					} else if(block.additional_information.chains) {
						REGISTRY_HELPER.registerBlock(new ChainBlock(blockSettings), block.information.name.id.getPath(), settings);
					} else if(block.additional_information.cake_like) {
						REGISTRY_HELPER.registerBlock(new CakeBlockImpl(block), block.information.name.id.getPath(), settings);
					} else {
						REGISTRY_HELPER.registerBlock(new BlockImpl(block, blockSettings), block.information.name.id.getPath(), settings);
					}
				} else {
					REGISTRY_HELPER.registerBlock(new BlockImpl(block, blockSettings), block.information.name.id.getPath(), settings);
				}
			} else {
				switch(block.block_type) {
					case CAMPFIRE:
						REGISTRY_HELPER.registerBlock(new CampfireBlockImpl(block.campfire_properties), block.information.name.id.getPath(), settings);
						break;
					case STAIRS:
						REGISTRY_HELPER.registerBlock(new StairsImpl(block), block.information.name.id.getPath(), settings);
						break;
					case SLAB:
						REGISTRY_HELPER.registerBlock(new SlabImpl(block), block.information.name.id.getPath(), settings);
						break;
					case FENCE:
						REGISTRY_HELPER.registerBlock(new FenceImpl(block), block.information.name.id.getPath(), settings);
						break;
					case FENCE_GATE:
						REGISTRY_HELPER.registerBlock(new FenceGateImpl(block), block.information.name.id.getPath(), settings);
						break;
					case CAKE:
						REGISTRY_HELPER.registerBlock(new CakeBlockImpl(block), block.information.name.id.getPath(), settings);
						break;
					case TRAPDOOR:
						REGISTRY_HELPER.registerBlock(new TrapdoorBlockImpl(blockSettings), block.information.name.id.getPath(), settings);
						break;
					case DOOR:
						REGISTRY_HELPER.registerBlock(new DoorBaseBlock(blockSettings), block.information.name.id.getPath(), settings);
						break;
					case LOG:
						REGISTRY_HELPER.registerLog(block.information.name.id.getPath(), block.information.getMaterial().getColor(),
								block.information.getMaterial().getColor(), settings);
						break;
					case OXIDIZING_BLOCK:
						Block oxidized = REGISTRY_HELPER.registerBlock(new OxidizableBlock(blockSettings), "oxidized_" + block.information.name.id.getPath());
						Block weathered = REGISTRY_HELPER.registerBlock(new OxidizableBlock(blockSettings, Oxidizable.OxidizationLevel.WEATHERED, oxidized),
								"weathered_" + block.information.name.id.getPath());
						Block exposed = REGISTRY_HELPER.registerBlock(new OxidizableBlock(blockSettings, Oxidizable.OxidizationLevel.EXPOSED, weathered),
								"exposed_" + block.information.name.id.getPath());
						REGISTRY_HELPER.registerBlock(new OxidizableBlock(blockSettings, Oxidizable.OxidizationLevel.UNAFFECTED, exposed),
								block.information.name.id.getPath() + "_block");
						break;
					case PLANT:
						REGISTRY_HELPER.registerBlock(new PlantBlockImpl(block, blockSettings), block.information.name.id.getPath());
						break;
					case SAPLING:
//						REGISTRY_HELPER.registerBlock(new SaplingBaseBlock(block, blockSettings), block.information.name.id.getPath());
						break;
					case TORCH:
						//TODO: Add particle lookup registry/method
						REGISTRY_HELPER.registerBlock(new TorchBaseBlock(), block.information.name.id.getPath());
						break;
					case BEE_HIVE:
						REGISTRY_HELPER.registerBlock(new BeehiveBlock(blockSettings), block.information.name.id.getPath());
						break;
					case LEAVES:
						REGISTRY_HELPER.registerBlock(new LeavesBaseBlock(), block.information.name.id.getPath());
						break;
					case LADDER:
						REGISTRY_HELPER.registerBlock(new CustomLadderBlock(), block.information.name.id.getPath());
						break;
					case PATH:
						REGISTRY_HELPER.registerBlock(new PathBlockImpl(blockSettings, block), block.information.name.id.getPath());
						break;
				}
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
							Utils.appendToPath(block.information.name.id, "_slab").getPath(), ItemGroup.BUILDING_BLOCKS, settings);
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
							new Identifier(id.getModId(), block.information.name.id.getPath() + "_fence").getPath(), ItemGroup.DECORATIONS, settings);
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
							Utils.appendToPath(block.information.name.id, "_fence_gate").getPath(), ItemGroup.REDSTONE, settings);
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
							Utils.appendToPath(block.information.name.id, "_wall").getPath(), ItemGroup.DECORATIONS, settings);
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

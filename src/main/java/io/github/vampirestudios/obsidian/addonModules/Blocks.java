package io.github.vampirestudios.obsidian.addonModules;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;
import io.github.vampirestudios.obsidian.ConvertableOxidizableBlock;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.mixins.HoneycombItemAccessor;
import io.github.vampirestudios.obsidian.threadhandlers.data.BlockInitThread;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.Utils;
import io.github.vampirestudios.vampirelib.blocks.ButtonBaseBlock;
import io.github.vampirestudios.vampirelib.blocks.DoorBaseBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class Blocks implements AddonModule {

	@Override
	public void init(File file, ModIdAndAddonPath id) throws FileNotFoundException {
		io.github.vampirestudios.obsidian.api.obsidian.block.Block block = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
		try {
			if (block == null) return;
			FabricBlockSettings blockSettings = FabricBlockSettings.of(block.information.getMaterial()).sounds(block.information.getBlockSoundGroup())
					.strength(block.information.destroy_time, block.information.explosion_resistance).collidable(block.information.collidable)
					.slipperiness(block.information.slipperiness).emissiveLighting((state, world, pos) -> block.information.is_emissive);
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
				if (block.additional_information != null) {
					if (block.additional_information.rotatable) {
						REGISTRY_HELPER.registerBlock(new FacingBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
					} else if (block.additional_information.horizontal_rotatable) {
						REGISTRY_HELPER.registerBlock(new HorizontalFacingBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
					} else if (block.additional_information.pillar) {
						REGISTRY_HELPER.registerBlock(new PillarBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
					} else if (block.additional_information.path) {
						REGISTRY_HELPER.registerBlock(new PathBlockImpl(blockSettings, block), block, block.information.name.id.getPath(), settings);
					} else if (block.additional_information.lantern) {
						REGISTRY_HELPER.registerBlock(new LanternBlock(blockSettings), block, block.information.name.id.getPath(), settings);
					} else if (block.additional_information.barrel) {
						REGISTRY_HELPER.registerBlock(new BarrelBlock(blockSettings), block, block.information.name.id.getPath(), settings);
					} else if (block.additional_information.leaves) {
						REGISTRY_HELPER.registerBlock(new LeavesBaseBlock(), block, block.information.name.id.getPath(), settings);
					} else if (block.additional_information.chains) {
						REGISTRY_HELPER.registerBlock(new ChainBlock(blockSettings), block, block.information.name.id.getPath(), settings);
					} else if (block.additional_information.cake_like) {
						REGISTRY_HELPER.registerBlock(new CakeBlockImpl(block), block, block.information.name.id.getPath(), settings);
					} else {
						REGISTRY_HELPER.registerBlock(new BlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
					}
				} else {
					REGISTRY_HELPER.registerBlock(new BlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
				}
			} else {
				switch (block.block_type) {
					case CAMPFIRE:
						REGISTRY_HELPER.registerBlock(new CampfireBlockImpl(block.campfire_properties), block, block.information.name.id.getPath(), settings);
						break;
					case STAIRS:
						REGISTRY_HELPER.registerBlock(new StairsImpl(block), block, block.information.name.id.getPath(), settings);
						break;
					case SLAB:
						REGISTRY_HELPER.registerBlock(new SlabImpl(block), block, block.information.name.id.getPath(), settings);
						break;
					case FENCE:
						REGISTRY_HELPER.registerBlock(new FenceImpl(block), block, block.information.name.id.getPath(), settings);
						break;
					case FENCE_GATE:
						REGISTRY_HELPER.registerBlock(new FenceGateImpl(block), block, block.information.name.id.getPath(), settings);
						break;
					case CAKE:
						REGISTRY_HELPER.registerBlock(new CakeBlockImpl(block), block, block.information.name.id.getPath(), settings);
						break;
					case TRAPDOOR:
						REGISTRY_HELPER.registerBlock(new TrapdoorBlockImpl(blockSettings), block, block.information.name.id.getPath(), settings);
						break;
					case DOOR:
						REGISTRY_HELPER.registerBlock(new DoorBaseBlock(blockSettings), block, block.information.name.id.getPath(), settings);
						break;
					case LOG:
						REGISTRY_HELPER.registerLog(block, block.information.name.id.getPath(), block.information.getMaterial().getColor(),
								block.information.getMaterial().getColor(), settings);
						break;
					case WOOD:
						REGISTRY_HELPER.registerLog(block, block.information.name.id.getPath(), block.information.getMaterial().getColor(),
								block.information.getMaterial().getColor(), settings);
						break;
					case OXIDIZING_BLOCK:
						Block oxidized = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.OXIDIZED, blockSettings), block,
								"oxidized_" + block.information.name.id.getPath(), settings);
						Block weathered = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.WEATHERED, blockSettings), block,
								"weathered_" + block.information.name.id.getPath(), settings);
						Block exposed = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.EXPOSED, blockSettings), block,
								"exposed_" + block.information.name.id.getPath(), settings);
						Block unaffected = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.UNAFFECTED, blockSettings), block,
								block.information.name.id.getPath() + "_block", settings);

						Registry.register(Obsidian.CONVERTABLE_OXIDIZABLE_BLOCKS, block.information.name.id, new ConvertableOxidizableBlock(unaffected, exposed));
						Registry.register(Obsidian.CONVERTABLE_OXIDIZABLE_BLOCKS, block.information.name.id, new ConvertableOxidizableBlock(exposed, weathered));
						Registry.register(Obsidian.CONVERTABLE_OXIDIZABLE_BLOCKS, block.information.name.id, new ConvertableOxidizableBlock(weathered, oxidized));

						if (block.additional_information.hasCut) {
							Block cutOxidized = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.OXIDIZED, blockSettings), block,
									"oxidized_" + block.information.name.id.getPath(), settings);
							Block cutWeathered = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.WEATHERED, blockSettings), block,
									"weathered_" + block.information.name.id.getPath(), settings);
							Block cutExposed = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.EXPOSED, blockSettings), block,
									"exposed_" + block.information.name.id.getPath(), settings);
							Block cutUnaffected = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.UNAFFECTED, blockSettings), block,
									block.information.name.id.getPath() + "_block", settings);
							Registry.register(Obsidian.CONVERTABLE_OXIDIZABLE_BLOCKS, block.information.name.id, new ConvertableOxidizableBlock(cutUnaffected, cutExposed));
							Registry.register(Obsidian.CONVERTABLE_OXIDIZABLE_BLOCKS, block.information.name.id, new ConvertableOxidizableBlock(cutExposed, cutWeathered));
							Registry.register(Obsidian.CONVERTABLE_OXIDIZABLE_BLOCKS, block.information.name.id, new ConvertableOxidizableBlock(cutWeathered, cutOxidized));

							if (block.additional_information.hasCut && block.additional_information.hasWaxed) {
								Block cutWeatheredWaxed = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.WEATHERED, blockSettings), block,
										"waxed_weathered_cut_" + block.information.name.id.getPath(), settings);
								Block cutExposedWaxed = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.EXPOSED, blockSettings), block,
										"waxed_exposed_cut_" + block.information.name.id.getPath(), settings);
								Block cutUnaffectedWaxed = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.UNAFFECTED, blockSettings), block,
										"waxed_cut_" + block.information.name.id.getPath() + "_block", settings);
								BiMap<Block, Block> blocks = Maps.synchronizedBiMap(HoneycombItemAccessor.getUNWAXED_TO_WAXED_BLOCKS().get());
								blocks.put(cutUnaffected, cutUnaffectedWaxed);
								blocks.put(cutExposed, cutExposedWaxed);
								blocks.put(cutWeathered, cutWeatheredWaxed);
								HoneycombItemAccessor.setUNWAXED_TO_WAXED_BLOCKS(Suppliers.ofInstance(blocks));

								BiMap<Block, Block> blocksReverse = Maps.synchronizedBiMap(HoneycombItemAccessor.getWAXED_TO_UNWAXED_BLOCKS().get());
								blocksReverse.put(cutUnaffectedWaxed, cutUnaffected);
								blocksReverse.put(cutExposedWaxed, cutExposed);
								blocksReverse.put(cutWeatheredWaxed, cutWeathered);
								HoneycombItemAccessor.setWAXED_TO_UNWAXED_BLOCKS(Suppliers.ofInstance(blocksReverse));
							}
						}

						if (block.additional_information.hasWaxed) {
							Block waxedWeathered = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.WEATHERED, blockSettings), block,
									"waxed_weathered_" + block.information.name.id.getPath(), settings);
							Block waxedExposed = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.EXPOSED, blockSettings), block,
									"waxed_exposed_" + block.information.name.id.getPath(), settings);
							Block waxedUnaffected = REGISTRY_HELPER.registerBlock(new OxidizableBlock(Oxidizable.OxidizationLevel.UNAFFECTED, blockSettings), block,
									"waxed_" + block.information.name.id.getPath() + "_block", settings);
							BiMap<Block, Block> blocks = Maps.synchronizedBiMap(HoneycombItemAccessor.getUNWAXED_TO_WAXED_BLOCKS().get());
							blocks.put(unaffected, waxedUnaffected);
							blocks.put(exposed, waxedExposed);
							blocks.put(weathered, waxedWeathered);
							HoneycombItemAccessor.setUNWAXED_TO_WAXED_BLOCKS(Suppliers.ofInstance(blocks));

							BiMap<Block, Block> blocksReverse = Maps.synchronizedBiMap(HoneycombItemAccessor.getWAXED_TO_UNWAXED_BLOCKS().get());
							blocksReverse.put(waxedWeathered, weathered);
							blocksReverse.put(waxedExposed, exposed);
							blocksReverse.put(waxedUnaffected, unaffected);
							HoneycombItemAccessor.setWAXED_TO_UNWAXED_BLOCKS(Suppliers.ofInstance(blocksReverse));
						}

						break;
					case PLANT:
						REGISTRY_HELPER.registerBlock(new PlantBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
						break;
					case HORIZONTAL_FACING_PLANT:
						REGISTRY_HELPER.registerBlock(new HorizontalFacingPlantBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
						break;
					case SAPLING:
//						REGISTRY_HELPER.registerBlock(new SaplingBaseBlock(block, blockSettings), block.information.name.id.getPath());
						break;
					case TORCH:
						//TODO: Add particle lookup registry/method
						REGISTRY_HELPER.registerBlock(new TorchBaseBlock(), block, block.information.name.id.getPath(), settings);
						break;
					case BEEHIVE:
						Block beeHive = REGISTRY_HELPER.registerBlock(new BeehiveBlockImpl(blockSettings), block, block.information.name.id.getPath(), settings);
						REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(BeehiveBlockEntity::new, beeHive), block.information.name.id.getPath() + "_beehive_be");
						break;
					case LEAVES:
						Block leaves = REGISTRY_HELPER.registerBlock(new LeavesBaseBlock(), block, block.information.name.id.getPath(), settings);
						break;
					case LADDER:
						REGISTRY_HELPER.registerBlock(new CustomLadderBlock(), block, block.information.name.id.getPath(), settings);
						break;
					case PATH:
						REGISTRY_HELPER.registerBlock(new PathBlockImpl(blockSettings, block), block, block.information.name.id.getPath(), settings);
						break;
					case BUTTON:
						//TODO: Unhardcode wooden
						REGISTRY_HELPER.registerBlock(new ButtonBaseBlock(true, blockSettings), block, block.information.name.id.getPath(), settings);
						break;
					case DOUBLE_PLANT:
						REGISTRY_HELPER.registerTallBlock(new TallFlowerBlockImpl(blockSettings.noCollision().breakInstantly()), block, block.information.name.id.getPath(), settings);
						break;
					case HORIZONTAL_FACING_DOUBLE_PLANT:
						REGISTRY_HELPER.registerTallBlock(new TallFlowerBlock(blockSettings.noCollision().breakInstantly()), block, block.information.name.id.getPath(), settings);
						break;
					case HANGING_DOUBLE_LEAVES:
						REGISTRY_HELPER.registerTallBlock(new HangingDoubleLeaves(blockSettings.noCollision().breakInstantly()), block, block.information.name.id.getPath(), settings);
						break;
				}
			}

			if (block.additional_information != null) {
				if(!block.additional_information.extraBlocksName.isEmpty()) {
					Identifier identifier = new Identifier(block.information.name.id.getNamespace(), block.additional_information.extraBlocksName);
					if (block.additional_information.slab) {
						REGISTRY_HELPER.registerBlock(new SlabImpl(block), block,
								Utils.appendToPath(identifier, "_slab").getPath(), ItemGroup.BUILDING_BLOCKS, settings);
					}
					if (block.additional_information.stairs) {
						REGISTRY_HELPER.registerBlock(new StairsImpl(block), block, new Identifier(id.getModId(), identifier.getPath() + "_stairs").getPath(),
								ItemGroup.BUILDING_BLOCKS);
					}
					if (block.additional_information.fence) {
						REGISTRY_HELPER.registerBlock(new FenceImpl(block), block,
								new Identifier(id.getModId(), identifier.getPath() + "_fence").getPath(), ItemGroup.DECORATIONS, settings);
					}
					if (block.additional_information.fenceGate) {
						REGISTRY_HELPER.registerBlock(new FenceGateImpl(block), block,
								Utils.appendToPath(identifier, "_fence_gate").getPath(), ItemGroup.REDSTONE, settings);
					}
					if (block.additional_information.walls) {
						REGISTRY_HELPER.registerBlock(new WallImpl(block), block,
								Utils.appendToPath(identifier, "_wall").getPath(), ItemGroup.DECORATIONS, settings);
					}
				} else {
					if (block.additional_information.slab) {
						REGISTRY_HELPER.registerBlock(new SlabImpl(block), block,
								Utils.appendToPath(block.information.name.id, "_slab").getPath(), ItemGroup.BUILDING_BLOCKS, settings);
					}
					if (block.additional_information.stairs) {
						REGISTRY_HELPER.registerBlock(new StairsImpl(block), block, new Identifier(id.getModId(), block.information.name.id.getPath() + "_stairs").getPath(),
								ItemGroup.BUILDING_BLOCKS);
					}
					if (block.additional_information.fence) {
						REGISTRY_HELPER.registerBlock(new FenceImpl(block), block,
								new Identifier(id.getModId(), block.information.name.id.getPath() + "_fence").getPath(), ItemGroup.DECORATIONS, settings);
					}
					if (block.additional_information.fenceGate) {
						REGISTRY_HELPER.registerBlock(new FenceGateImpl(block), block,
								Utils.appendToPath(block.information.name.id, "_fence_gate").getPath(), ItemGroup.REDSTONE, settings);
					}
					if (block.additional_information.walls) {
						REGISTRY_HELPER.registerBlock(new WallImpl(block), block,
								Utils.appendToPath(block.information.name.id, "_wall").getPath(), ItemGroup.DECORATIONS, settings);
					}
				}
			}

			new BlockInitThread(block);

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

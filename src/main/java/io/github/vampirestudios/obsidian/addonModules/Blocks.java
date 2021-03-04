package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.threadhandlers.data.BlockInitThread;
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
						REGISTRY_HELPER.registerBlock(new FacingBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
					} else if(block.additional_information.horizontal_rotatable) {
						REGISTRY_HELPER.registerBlock(new HorizontalFacingBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
					} else if(block.additional_information.pillar) {
						REGISTRY_HELPER.registerBlock(new PillarBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
					} else if(block.additional_information.path) {
						REGISTRY_HELPER.registerBlock(new PathBlockImpl(blockSettings, block), block, block.information.name.id.getPath(), settings);
					} else if(block.additional_information.lantern) {
						REGISTRY_HELPER.registerBlock(new LanternBlock(blockSettings), block, block.information.name.id.getPath(), settings);
					} else if(block.additional_information.barrel) {
						REGISTRY_HELPER.registerBlock(new BarrelBlock(blockSettings), block, block.information.name.id.getPath(), settings);
					} else if(block.additional_information.leaves) {
						REGISTRY_HELPER.registerBlock(new LeavesBaseBlock(), block, block.information.name.id.getPath(), settings);
					} else if(block.additional_information.chains) {
						REGISTRY_HELPER.registerBlock(new ChainBlock(blockSettings), block, block.information.name.id.getPath(), settings);
					} else if(block.additional_information.cake_like) {
						REGISTRY_HELPER.registerBlock(new CakeBlockImpl(block), block, block.information.name.id.getPath(), settings);
					} else {
						REGISTRY_HELPER.registerBlock(new BlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
					}
				} else {
					REGISTRY_HELPER.registerBlock(new BlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
				}
			} else {
				switch(block.block_type) {
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
					case OXIDIZING_BLOCK:
						Block oxidized = REGISTRY_HELPER.registerBlock(new OxidizableBlock(blockSettings), block, "oxidized_" + block.information.name.id.getPath());
						Block weathered = REGISTRY_HELPER.registerBlock(new OxidizableBlock(blockSettings, Oxidizable.OxidizationLevel.WEATHERED, oxidized), block,
								"weathered_" + block.information.name.id.getPath());
						Block exposed = REGISTRY_HELPER.registerBlock(new OxidizableBlock(blockSettings, Oxidizable.OxidizationLevel.EXPOSED, weathered), block,
								"exposed_" + block.information.name.id.getPath());
						REGISTRY_HELPER.registerBlock(new OxidizableBlock(blockSettings, Oxidizable.OxidizationLevel.UNAFFECTED, exposed), block,
								block.information.name.id.getPath() + "_block");
						break;
					case PLANT:
						REGISTRY_HELPER.registerBlock(new PlantBlockImpl(block, blockSettings), block, block.information.name.id.getPath());
						break;
					case SAPLING:
//						REGISTRY_HELPER.registerBlock(new SaplingBaseBlock(block, blockSettings), block.information.name.id.getPath());
						break;
					case TORCH:
						//TODO: Add particle lookup registry/method
						REGISTRY_HELPER.registerBlock(new TorchBaseBlock(), block, block.information.name.id.getPath());
						break;
					case BEE_HIVE:
						REGISTRY_HELPER.registerBlock(new BeehiveBlock(blockSettings), block, block.information.name.id.getPath());
						break;
					case LEAVES:
						REGISTRY_HELPER.registerBlock(new LeavesBaseBlock(), block, block.information.name.id.getPath());
						break;
					case LADDER:
						REGISTRY_HELPER.registerBlock(new CustomLadderBlock(), block, block.information.name.id.getPath());
						break;
					case PATH:
						REGISTRY_HELPER.registerBlock(new PathBlockImpl(blockSettings, block), block, block.information.name.id.getPath());
						break;
				}
			}

			if (block.additional_information != null) {
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

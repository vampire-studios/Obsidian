package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.block.SaplingBaseBlock;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.threadhandlers.data.BlockInitThread;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.Utils;
import io.github.vampirestudios.vampirelib.blocks.ButtonBaseBlock;
import io.github.vampirestudios.vampirelib.blocks.DoorBaseBlock;
import io.github.vampirestudios.vampirelib.blocks.PressurePlateBaseBlock;
import io.github.vampirestudios.vampirelib.blocks.entity.IBlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import org.hjson.JsonValue;
import org.hjson.Stringify;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Blocks implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        io.github.vampirestudios.obsidian.api.obsidian.block.Block block;
        if (addon.getConfigPackInfo() instanceof LegacyObsidianAddonInfo) {
            block = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
        } else {
            ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addon.getConfigPackInfo();
            if (addonInfo.format == ObsidianAddonInfo.Format.JSON) {
                block = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.JSON5) {
                JsonObject jsonObject = Obsidian.JANKSON.load(file);
                block = Obsidian.JANKSON.fromJson(jsonObject, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.YAML) {
//                LoadSettings settings = LoadSettings.builder().build();
//                Load load = new Load(settings);
//                Map<String, String> map = (Map<String, String>) load.loadFromReader(new FileReader(file));
//                System.out.println(map);

//                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//                mapper.findAndRegisterModules();
//                block = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
                block = null;
                try (FileConfig config = FileConfig.of(file)) {
                    config.load();
                    System.out.println(config.valueMap());
                }
            } else if (addonInfo.format == ObsidianAddonInfo.Format.TOML) {
                ObjectMapper mapper = new ObjectMapper(new TomlFactory());
                mapper.findAndRegisterModules();
                block = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.HJSON) {
                block = Obsidian.GSON.fromJson(JsonValue.readHjson(new FileReader(file)).toString(Stringify.FORMATTED), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
            } else {
                block = null;
            }
        }
        try {
            if (block == null) return;
            QuiltBlockSettings blockSettings = QuiltBlockSettings.of(block.information.getMaterial())
                    .hardness(block.information.hardness).resistance(block.information.resistance)
                    .sounds(block.information.getBlockSoundGroup())
                    .slipperiness(block.information.slipperiness)
                    .emissiveLighting((state, world, pos) -> block.information.is_emissive)
                    .luminance(block.information.luminance)
                    .velocityMultiplier(block.information.velocity_modifier)
                    .jumpVelocityMultiplier(block.information.jump_velocity_modifier);
            if (block.information.randomTicks) blockSettings.ticksRandomly();
            if (block.information.instant_break) blockSettings.breakInstantly();
            if (!block.information.collidable) blockSettings.noCollision();
            if (block.information.translucent) blockSettings.nonOpaque();
            if (block.information.dynamic_boundaries) blockSettings.dynamicBounds();

            Item.Settings settings = new Item.Settings().group(block.information.getItemGroup());
            if (block.food_information != null) settings.food(Registries.FOOD_COMPONENTS.get(block.food_information.foodComponent));
            if (block.information.fireproof) settings.fireproof();

            if (block.additional_information != null) {
                if (block.additional_information.path) {
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
                }
            }

            if (block.block_type != null) {
                switch (block.block_type) {
                    case BLOCK:
                    case WOOD:
                        if (block.additional_information != null && block.additional_information.dyable) {
                            net.minecraft.block.Block registeredBlock = REGISTRY_HELPER.registerBlockWithoutItem(new DyeableBlock(block, blockSettings), block.information.name.id.getPath());
                            REGISTRY_HELPER.registerItem(new CustomDyeableItem(block, registeredBlock, settings), block.information.name.id.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                            (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                    block.information.name.id.getPath() + "_be");
                        } else REGISTRY_HELPER.registerBlock(new BlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case HORIZONTAL_FACING_BLOCK:
                        if (block.additional_information != null && block.additional_information.dyable && block.additional_information.sittable) {
                            net.minecraft.block.Block registeredBlock = REGISTRY_HELPER.registerBlockWithoutItem(new HorizontalFacingSittableAndDyableBlock(block, blockSettings), block.information.name.id.getPath());
                            REGISTRY_HELPER.registerItem(new CustomDyeableItem(block, registeredBlock, settings), block.information.name.id.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                            (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                    block.information.name.id.getPath() + "_be");
                        } else if (block.additional_information != null && block.additional_information.dyable) {
                            net.minecraft.block.Block registeredBlock = REGISTRY_HELPER.registerBlockWithoutItem(new HorizontalFacingDyableBlockImpl(block, blockSettings), block.information.name.id.getPath());
                            REGISTRY_HELPER.registerItem(new CustomDyeableItem(block, registeredBlock, settings), block.information.name.id.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                            (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                    block.information.name.id.getPath() + "_be");
                        } else if (block.additional_information != null && block.additional_information.sittable) {
                            net.minecraft.block.Block registeredBlock = REGISTRY_HELPER.registerBlockWithoutItem(new HorizontalFacingSittableBlock(block, blockSettings), block.information.name.id.getPath());
                            REGISTRY_HELPER.registerItem(new CustomDyeableItem(block, registeredBlock, settings), block.information.name.id.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                            (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                    block.information.name.id.getPath() + "_be");
                        } else REGISTRY_HELPER.registerBlock(new HorizontalFacingBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case ROTATABLE_BLOCK:
                        REGISTRY_HELPER.registerBlock(new FacingBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case BED:
                        REGISTRY_HELPER.registerBlock(new BedBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case CAMPFIRE:
                        REGISTRY_HELPER.registerBlock(new CampfireBlockImpl(block.campfire_properties), block, block.information.name.id.getPath(), settings);
                        break;
                    case STAIRS:
                        REGISTRY_HELPER.registerBlock(new StairsImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case SLAB:
                        REGISTRY_HELPER.registerBlock(new SlabImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case FENCE:
                        REGISTRY_HELPER.registerBlock(new FenceImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case FENCE_GATE:
                        REGISTRY_HELPER.registerBlock(new FenceGateImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case CAKE:
                        REGISTRY_HELPER.registerBlock(new CakeBlockImpl(block), block, block.information.name.id.getPath(), settings);
                        break;
                    case TRAPDOOR:
                        REGISTRY_HELPER.registerBlock(new TrapdoorBlockImpl(blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case METAL_DOOR:
                        REGISTRY_HELPER.registerBlock(new DoorBaseBlock(net.minecraft.block.Blocks.IRON_DOOR, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case WOODEN_DOOR:
                        REGISTRY_HELPER.registerBlock(new DoorBaseBlock(net.minecraft.block.Blocks.DARK_OAK_DOOR, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case LOG:
                        REGISTRY_HELPER.registerLog(block, block.information.name.id.getPath(), block.information.getMaterial().getColor(),
                                block.information.getMaterial().getColor(), settings);
                        break;
                    case STEM:
                        REGISTRY_HELPER.registerNetherStemBlock(block, block.information.name.id.getPath(), block.information.getMaterial().getColor(), settings);
                        break;
                    case OXIDIZING_BLOCK:
                        List<Identifier> names = new ArrayList<>();
                        block.oxidizable_properties.stages.forEach(oxidationStage -> oxidationStage.blocks.forEach(variantBlock -> {
                            if (!names.contains(variantBlock.name.id)) names.add(variantBlock.name.id);
                        }));
                        names.forEach(identifier -> REGISTRY_HELPER.registerBlock(new BlockImpl(block, blockSettings), block, identifier.getPath(), settings));
                        break;
                    case PLANT:
                        if (block.additional_information != null) {
                            if (block.additional_information.waterloggable) {
                                REGISTRY_HELPER.registerBlock(new WaterloggablePlantBlockImpl(block, blockSettings.noCollision().breakInstantly()), block, block.information.name.id.getPath(), settings);
                            }
                        } else {
                            REGISTRY_HELPER.registerBlock(new PlantBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        }
                        break;
                    case PILLAR:
                        REGISTRY_HELPER.registerBlock(new PillarBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case HORIZONTAL_FACING_PLANT:
                        REGISTRY_HELPER.registerBlock(new HorizontalFacingPlantBlockImpl(block, blockSettings.noCollision().breakInstantly()), block, block.information.name.id.getPath(), settings);
                        break;
                    case SAPLING:
						REGISTRY_HELPER.registerBlock(new SaplingBaseBlock(block), block, block.information.name.id.getPath(), settings);
                        break;
                    case TORCH:
                        //TODO: Add particle lookup registry/method
                        REGISTRY_HELPER.registerBlock(new TorchBaseBlock(), block, block.information.name.id.getPath(), settings);
                        break;
                    case BEEHIVE:
                        net.minecraft.block.Block beeHive = REGISTRY_HELPER.registerBlock(new BeehiveBlockImpl(blockSettings), block, block.information.name.id.getPath(), settings);
                        REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(BeehiveBlockEntity::new, beeHive), block.information.name.id.getPath() + "_beehive_be");
                        break;
                    case LEAVES:
                        REGISTRY_HELPER.registerLeavesBlock(block, block.information.name.id.getPath(), settings);
                        break;
                    case LADDER:
                        REGISTRY_HELPER.registerBlock(new CustomLadderBlock(), block, block.information.name.id.getPath(), settings);
                        break;
                    case PATH:
                        REGISTRY_HELPER.registerBlock(new PathBlockImpl(blockSettings, block), block, block.information.name.id.getPath(), settings);
                        break;
                    case WOODEN_BUTTON:
                        REGISTRY_HELPER.registerBlock(new ButtonBaseBlock(true, net.minecraft.block.Blocks.DARK_OAK_BUTTON, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case STONE_BUTTON:
                        REGISTRY_HELPER.registerBlock(new ButtonBaseBlock(false, net.minecraft.block.Blocks.POLISHED_BLACKSTONE_BUTTON, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case DOUBLE_PLANT:
                        if (block.additional_information != null) {
                            if (block.additional_information.waterloggable) {
                                REGISTRY_HELPER.registerTallBlock(new WaterloggableTallFlowerBlockImpl(block, blockSettings.noCollision().breakInstantly()), block, block.information.name.id.getPath(), settings);
                            }
                        } else {
                            REGISTRY_HELPER.registerTallBlock(new TallFlowerBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        }
                        break;
                    case HORIZONTAL_FACING_DOUBLE_PLANT:
                        REGISTRY_HELPER.registerTallBlock(new TallFlowerBlock(blockSettings.noCollision().breakInstantly()), block, block.information.name.id.getPath(), settings);
                        break;
                    case HANGING_DOUBLE_LEAVES:
                        REGISTRY_HELPER.registerHangingTallBlock(new HangingDoubleLeaves(blockSettings.noCollision().breakInstantly()), block, block.information.name.id.getPath(), settings);
                        break;
                    case LANTERN:
                        REGISTRY_HELPER.registerBlock(new LanternBlock(blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case CHAIN:
                        REGISTRY_HELPER.registerBlock(new ChainBlock(blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case PANE:
                        REGISTRY_HELPER.registerBlock(new PaneBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case DYEABLE:
                        net.minecraft.block.Block registeredBlock = REGISTRY_HELPER.registerBlockWithoutItem(new DyeableBlock(block, blockSettings), block.information.name.id.getPath());
                        REGISTRY_HELPER.registerItem(new CustomDyeableItem(block, registeredBlock, settings), block.information.name.id.getPath());
                        REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                        (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                block.information.name.id.getPath() + "_be");
                        break;
                    case LOOM:
                        REGISTRY_HELPER.registerBlock(new LoomBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case CRAFTING_TABLE:
                        REGISTRY_HELPER.registerBlock(new CraftingTableBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case FURNACE:
                        Block furnace = REGISTRY_HELPER.registerBlock(new FurnaceBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        ((IBlockEntityType) BlockEntityType.FURNACE).vl_addBlocks(furnace);
                        break;
                    case BLAST_FURNACE:
                        Block blastFurnace = REGISTRY_HELPER.registerBlock(new BlastFurnaceBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        ((IBlockEntityType) BlockEntityType.BLAST_FURNACE).vl_addBlocks(blastFurnace);
                        break;
                    case SMOKER:
                        Block smoker = REGISTRY_HELPER.registerBlock(new SmokerBlockImpl(blockSettings), block, block.information.name.id.getPath(), settings);
                        ((IBlockEntityType) BlockEntityType.SMOKER).vl_addBlocks(smoker);
                        break;
                    case BARREL:
                        Block barrel = REGISTRY_HELPER.registerBlock(new BarrelBlockImpl(blockSettings), block, block.information.name.id.getPath(), settings);
                        ((IBlockEntityType) BlockEntityType.BARREL).vl_addBlocks(barrel);
                        break;
                    case PISTON:
                        Block piston = REGISTRY_HELPER.registerBlock(new PistonBlockImpl(false, blockSettings), block, block.information.name.id.getPath(), settings);
                        Block stickyPiston = REGISTRY_HELPER.registerBlock(new PistonBlockImpl(true, blockSettings), block, block.information.name.id.getPath() + "_sticky", settings);
                        ((IBlockEntityType) BlockEntityType.PISTON).vl_addBlocks(piston, stickyPiston);
                        break;
                    case CARPET:
                        REGISTRY_HELPER.registerBlock(new CarpetBlock(blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    /*case PANE:
//                        BlockEntityType<SignBlockEntity> signBlockEntityBlockEntityType = REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(CraftingTableBlockEntity), Utils.appendToPath(block.information.name.id, "_base"));

                        REGISTRY_HELPER.registerBlock(new PaneBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;
                    case PANE:
                        REGISTRY_HELPER.registerBlock(new PaneBlockImpl(block, blockSettings), block, block.information.name.id.getPath(), settings);
                        break;*/
                }
            }

            if (block.additional_information != null) {
                if (!block.additional_information.extraBlocksName.isEmpty()) {
                    Identifier identifier = new Identifier(block.information.name.id.getNamespace(), block.additional_information.extraBlocksName);
                    if (block.additional_information.slab) {
                        REGISTRY_HELPER.registerBlock(new SlabImpl(block, blockSettings), block,
                                Utils.appendToPath(identifier, "_slab").getPath(), ItemGroup.BUILDING_BLOCKS, settings);
                    }
                    if (block.additional_information.stairs) {
                        REGISTRY_HELPER.registerBlock(new StairsImpl(block, blockSettings), block, new Identifier(id.modId(), identifier.getPath() + "_stairs").getPath(),
                                ItemGroup.BUILDING_BLOCKS);
                    }
                    if (block.additional_information.fence) {
                        REGISTRY_HELPER.registerBlock(new FenceImpl(block, blockSettings), block,
                                new Identifier(id.modId(), identifier.getPath() + "_fence").getPath(), ItemGroup.DECORATIONS, settings);
                    }
                    if (block.additional_information.fenceGate) {
                        REGISTRY_HELPER.registerBlock(new FenceGateImpl(block, blockSettings), block,
                                Utils.appendToPath(identifier, "_fence_gate").getPath(), ItemGroup.REDSTONE, settings);
                    }
                    if (block.additional_information.walls) {
                        REGISTRY_HELPER.registerBlock(new WallImpl(block, blockSettings), block,
                                Utils.appendToPath(identifier, "_wall").getPath(), ItemGroup.DECORATIONS, settings);
                    }
                    if (block.additional_information.pressurePlate) {
                        REGISTRY_HELPER.registerBlock(new PressurePlateBaseBlock(net.minecraft.block.Blocks.DARK_OAK_PRESSURE_PLATE, blockSettings, PressurePlateBlock.ActivationRule.EVERYTHING), block,
                                Utils.appendToPath(identifier, "_pressure_plate").getPath(), ItemGroup.REDSTONE, settings);
                    }
                    if (block.additional_information.button) {
                        REGISTRY_HELPER.registerBlock(new ButtonBaseBlock(true, net.minecraft.block.Blocks.DARK_OAK_BUTTON, blockSettings), block,
                                Utils.appendToPath(identifier, "_button").getPath(), ItemGroup.REDSTONE, settings);
                    }
                    if (block.additional_information.door) {
                        REGISTRY_HELPER.registerBlock(new DoorBaseBlock(net.minecraft.block.Blocks.DARK_OAK_DOOR, blockSettings), block,
                                Utils.appendToPath(identifier, "_door").getPath(), ItemGroup.REDSTONE, settings);
                    }
                    if (block.additional_information.trapdoor) {
                        REGISTRY_HELPER.registerBlock(new TrapdoorBlockImpl(blockSettings), block,
                                Utils.appendToPath(identifier, "_trapdoor").getPath(), ItemGroup.REDSTONE, settings);
                    }
                } else {
                    if (block.additional_information.slab) {
                        REGISTRY_HELPER.registerBlock(new SlabImpl(block, blockSettings), block,
                                Utils.appendToPath(block.information.name.id, "_slab").getPath(), ItemGroup.BUILDING_BLOCKS, settings);
                    }
                    if (block.additional_information.stairs) {
                        REGISTRY_HELPER.registerBlock(new StairsImpl(block, blockSettings), block, new Identifier(id.modId(), block.information.name.id.getPath() + "_stairs").getPath(),
                                ItemGroup.BUILDING_BLOCKS);
                    }
                    if (block.additional_information.fence) {
                        REGISTRY_HELPER.registerBlock(new FenceImpl(block, blockSettings), block,
                                new Identifier(id.modId(), block.information.name.id.getPath() + "_fence").getPath(), ItemGroup.DECORATIONS, settings);
                    }
                    if (block.additional_information.fenceGate) {
                        REGISTRY_HELPER.registerBlock(new FenceGateImpl(block, blockSettings), block,
                                Utils.appendToPath(block.information.name.id, "_fence_gate").getPath(), ItemGroup.REDSTONE, settings);
                    }
                    if (block.additional_information.walls) {
                        REGISTRY_HELPER.registerBlock(new WallImpl(block, blockSettings), block,
                                Utils.appendToPath(block.information.name.id, "_wall").getPath(), ItemGroup.DECORATIONS, settings);
                    }
                    if (block.additional_information.pressurePlate) {
                        REGISTRY_HELPER.registerBlock(new PressurePlateBaseBlock(net.minecraft.block.Blocks.DARK_OAK_PRESSURE_PLATE, blockSettings, PressurePlateBlock.ActivationRule.EVERYTHING), block,
                                Utils.appendToPath(block.information.name.id, "_pressure_plate").getPath(), ItemGroup.REDSTONE, settings);
                    }
                    if (block.additional_information.button) {
                        REGISTRY_HELPER.registerBlock(new ButtonBaseBlock(true, net.minecraft.block.Blocks.DARK_OAK_BUTTON, blockSettings), block,
                                Utils.appendToPath(block.information.name.id, "_button").getPath(), ItemGroup.REDSTONE, settings);
                    }
                    if (block.additional_information.door) {
                        REGISTRY_HELPER.registerBlock(new DoorBaseBlock(net.minecraft.block.Blocks.DARK_OAK_DOOR, blockSettings), block,
                                Utils.appendToPath(block.information.name.id, "_door").getPath(), ItemGroup.REDSTONE, settings);
                    }
                    if (block.additional_information.trapdoor) {
                        REGISTRY_HELPER.registerBlock(new TrapdoorBlockImpl(blockSettings), block,
                                Utils.appendToPath(block.information.name.id, "_trapdoor").getPath(), ItemGroup.REDSTONE, settings);
                    }
                }
            }

            if (!addon.getConfigPackInfo().hasData) {
                new BlockInitThread(block);
            }

//            BlockRenderLayerMap.INSTANCE.putBlock(Registry.BLOCK.get(block.information.name.id), block.information.getRenderLayer());

            if (block.block_type == io.github.vampirestudios.obsidian.api.obsidian.block.Block.BlockType.OXIDIZING_BLOCK) {
                List<Identifier> names = new ArrayList<>();
                block.oxidizable_properties.stages.forEach(oxidationStage -> oxidationStage.blocks.forEach(variantBlock -> {
                    if (!names.contains(variantBlock.name.id)) names.add(variantBlock.name.id);
                }));
                names.forEach(identifier -> {
                    if (ContentRegistries.BLOCKS.get(identifier) != null) register(ContentRegistries.BLOCKS, "block", identifier, block);
                });
            } else {
                register(ContentRegistries.BLOCKS, "block", block.information.name.id, block);
            }
        } catch (Exception e) {
            if (block.block_type == io.github.vampirestudios.obsidian.api.obsidian.block.Block.BlockType.OXIDIZING_BLOCK) {
                List<Identifier> names = new ArrayList<>();
                block.oxidizable_properties.stages.forEach(oxidationStage -> oxidationStage.blocks.forEach(variantBlock -> {
                    if (!names.contains(variantBlock.name.id)) names.add(variantBlock.name.id);
                }));
                names.forEach(identifier -> failedRegistering("block", identifier.toString(), e));
            } else {
                failedRegistering("block", block.information.name.id.toString(), e);
            }
        }
    }

    @Override
    public String getType() {
        return "block";
    }

}

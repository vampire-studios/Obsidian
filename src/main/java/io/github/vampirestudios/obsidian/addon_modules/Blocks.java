package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.hjson.JsonValue;
import org.hjson.Stringify;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                mapper.findAndRegisterModules();
                block = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
//                block = null;
//                try (FileConfig config = FileConfig.of(file)) {
//                    config.load();
//                    System.out.println(config.valueMap());
//                }
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

            Identifier blockId;
            if (block.description != null) {
                blockId = block.description.identifier;
            } else {
                if (block.information.name.id != null) {
                    blockId = block.information.name.id;
                } else {
                    blockId = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));
                    block.information.name.id = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));
                }
            }

            QuiltBlockSettings blockSettings;

            if (block.information.parentBlock != null) {
                blockSettings = QuiltBlockSettings.copyOf(Registry.BLOCK.get(block.information.parentBlock));
            } else {
                blockSettings = QuiltBlockSettings.of(block.information.properties.getMaterial());
            }

            blockSettings.hardness(block.information.properties.hardness).resistance(block.information.properties.resistance)
                    .sounds(block.information.properties.getBlockSoundGroup())
                    .slipperiness(block.information.properties.slipperiness)
                    .emissiveLighting((state, world, pos) -> block.information.properties.is_emissive)
                    .luminance(block.information.properties.luminance)
                    .velocityMultiplier(block.information.properties.velocity_modifier)
                    .jumpVelocityMultiplier(block.information.properties.jump_velocity_modifier);
            if (block.information.properties.randomTicks) blockSettings.ticksRandomly();
            if (block.information.properties.instant_break) blockSettings.breakInstantly();
            if (!block.information.properties.collidable) blockSettings.noCollision();
            if (block.information.properties.translucent) blockSettings.nonOpaque();
            if (block.information.properties.dynamic_boundaries) blockSettings.dynamicBounds();

            QuiltItemSettings settings = new QuiltItemSettings().group(block.information.getItemGroup());
            settings.maxCount(block.information.properties.maxStackSize);
            settings.rarity(Rarity.valueOf(block.information.properties.rarity.toUpperCase(Locale.ROOT)));
            if (block.information.properties.maxDurability != 0) settings.maxDamage(block.information.properties.maxDurability);
            if (!block.information.properties.equipmentSlot.isEmpty() && !block.information.properties.equipmentSlot.isBlank())
                settings.equipmentSlot(EquipmentSlot.byName(block.information.properties.equipmentSlot));
            if (block.food_information != null) settings.food(Registries.FOOD_COMPONENTS.get(block.food_information.foodComponent));
            if (block.information.properties.fireproof) settings.fireproof();

            if (block.additional_information != null) {
                if (block.additional_information.path) {
                    REGISTRY_HELPER.registerBlock(new PathBlockImpl(blockSettings, block), block, blockId.getPath(), settings);
                } else if (block.additional_information.lantern) {
                    REGISTRY_HELPER.registerBlock(new LanternBlock(blockSettings), block, blockId.getPath(), settings);
                } else if (block.additional_information.barrel) {
                    REGISTRY_HELPER.registerBlock(new BarrelBlock(blockSettings), block, blockId.getPath(), settings);
                } else if (block.additional_information.leaves) {
                    REGISTRY_HELPER.registerBlock(new LeavesBaseBlock(), block, blockId.getPath(), settings);
                } else if (block.additional_information.chains) {
                    REGISTRY_HELPER.registerBlock(new ChainBlock(blockSettings), block, blockId.getPath(), settings);
                } else if (block.additional_information.cake_like) {
                    REGISTRY_HELPER.registerBlock(new CakeBlockImpl(block), block, blockId.getPath(), settings);
                }
            }

            if (block.block_type != null) {
                switch (block.block_type) {
                    case BLOCK:
                    case WOOD:
                        if (block.additional_information != null && block.additional_information.dyable) {
                            net.minecraft.block.Block registeredBlock = REGISTRY_HELPER.registerBlockWithoutItem(new DyeableBlock(block, blockSettings), blockId.getPath());
                            REGISTRY_HELPER.registerItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                            (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                    blockId.getPath() + "_be");
                        } else REGISTRY_HELPER.registerBlock(new BlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case HORIZONTAL_DIRECTIONAL:
                        if (block.additional_information != null && block.additional_information.dyable && block.additional_information.sittable) {
                            net.minecraft.block.Block registeredBlock = REGISTRY_HELPER.registerBlockWithoutItem(new HorizontalFacingSittableAndDyableBlock(block, blockSettings), blockId.getPath());
                            REGISTRY_HELPER.registerItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                            (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                    blockId.getPath() + "_be");
                        } else if (block.additional_information != null && block.additional_information.dyable) {
                            net.minecraft.block.Block registeredBlock = REGISTRY_HELPER.registerBlockWithoutItem(new HorizontalFacingDyableBlockImpl(block, blockSettings), blockId.getPath());
                            REGISTRY_HELPER.registerItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                            (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                    blockId.getPath() + "_be");
                        } else if (block.additional_information != null && block.additional_information.sittable) {
                            net.minecraft.block.Block registeredBlock = REGISTRY_HELPER.registerBlockWithoutItem(new HorizontalFacingSittableBlock(block, blockSettings), blockId.getPath());
                            REGISTRY_HELPER.registerItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                            (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                    blockId.getPath() + "_be");
                        } else REGISTRY_HELPER.registerBlock(new HorizontalFacingBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case DIRECTIONAL:
                        REGISTRY_HELPER.registerBlock(new FacingBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case BED:
                        REGISTRY_HELPER.registerBlock(new BedBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case CAMPFIRE:
                        REGISTRY_HELPER.registerBlock(new CampfireBlockImpl(block.campfire_properties), block, blockId.getPath(), settings);
                        break;
                    case STAIRS:
                        REGISTRY_HELPER.registerBlock(new StairsImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case SLAB:
                        REGISTRY_HELPER.registerBlock(new SlabImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case FENCE:
                        REGISTRY_HELPER.registerBlock(new FenceImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case FENCE_GATE:
                        REGISTRY_HELPER.registerBlock(new FenceGateImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case CAKE:
                        REGISTRY_HELPER.registerBlock(new CakeBlockImpl(block), block, blockId.getPath(), settings);
                        break;
                    case TRAPDOOR:
                        REGISTRY_HELPER.registerBlock(new TrapdoorBlockImpl(blockSettings), block, blockId.getPath(), settings);
                        break;
                    case METAL_DOOR:
                        REGISTRY_HELPER.registerBlock(new DoorBaseBlock(net.minecraft.block.Blocks.IRON_DOOR, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case WOODEN_DOOR:
                        REGISTRY_HELPER.registerBlock(new DoorBaseBlock(net.minecraft.block.Blocks.DARK_OAK_DOOR, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case LOG:
                        REGISTRY_HELPER.registerLog(block, blockId.getPath(), block.information.properties.getMaterial().getColor(),
                                block.information.properties.getMaterial().getColor(), settings);
                        break;
                    case STEM:
                        REGISTRY_HELPER.registerNetherStemBlock(block, blockId.getPath(), block.information.properties.getMaterial().getColor(), settings);
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
                                REGISTRY_HELPER.registerBlock(new WaterloggablePlantBlockImpl(block, blockSettings.noCollision().breakInstantly()), block, blockId.getPath(), settings);
                            }
                        } else {
                            REGISTRY_HELPER.registerBlock(new PlantBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        }
                        break;
                    case ROTATED_PILLAR:
                        REGISTRY_HELPER.registerBlock(new PillarBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case HORIZONTAL_FACING_PLANT:
                        REGISTRY_HELPER.registerBlock(new HorizontalFacingPlantBlockImpl(block, blockSettings.noCollision().breakInstantly()), block, blockId.getPath(), settings);
                        break;
                    case SAPLING:
						REGISTRY_HELPER.registerBlock(new SaplingBaseBlock(block), block, blockId.getPath(), settings);
                        break;
                    case TORCH:
                        //TODO: Add particle lookup registry/method
                        REGISTRY_HELPER.registerBlock(new TorchBaseBlock(), block, blockId.getPath(), settings);
                        break;
                    case BEEHIVE:
                        net.minecraft.block.Block beeHive = REGISTRY_HELPER.registerBlock(new BeehiveBlockImpl(blockSettings), block, blockId.getPath(), settings);
                        REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(BeehiveBlockEntity::new, beeHive), blockId.getPath() + "_beehive_be");
                        break;
                    case LEAVES:
                        REGISTRY_HELPER.registerLeavesBlock(block, blockId.getPath(), settings);
                        break;
                    case LADDER:
                        REGISTRY_HELPER.registerBlock(new CustomLadderBlock(), block, blockId.getPath(), settings);
                        break;
                    case PATH:
                        REGISTRY_HELPER.registerBlock(new PathBlockImpl(blockSettings, block), block, blockId.getPath(), settings);
                        break;
                    case WOODEN_BUTTON:
                        REGISTRY_HELPER.registerBlock(new ButtonBaseBlock(true, net.minecraft.block.Blocks.DARK_OAK_BUTTON, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case STONE_BUTTON:
                        REGISTRY_HELPER.registerBlock(new ButtonBaseBlock(false, net.minecraft.block.Blocks.POLISHED_BLACKSTONE_BUTTON, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case DOUBLE_PLANT:
                        if (block.additional_information != null) {
                            if (block.additional_information.waterloggable) {
                                REGISTRY_HELPER.registerTallBlock(new WaterloggableTallFlowerBlockImpl(block, blockSettings.noCollision().breakInstantly()), block, blockId.getPath(), settings);
                            }
                        } else {
                            REGISTRY_HELPER.registerTallBlock(new TallFlowerBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        }
                        break;
                    case HORIZONTAL_FACING_DOUBLE_PLANT:
                        REGISTRY_HELPER.registerTallBlock(new TallFlowerBlock(blockSettings.noCollision().breakInstantly()), block, blockId.getPath(), settings);
                        break;
                    case HANGING_DOUBLE_LEAVES:
                        REGISTRY_HELPER.registerHangingTallBlock(new HangingDoubleLeaves(blockSettings.noCollision().breakInstantly()), block, blockId.getPath(), settings);
                        break;
                    case LANTERN:
                        REGISTRY_HELPER.registerBlock(new LanternBlock(blockSettings), block, blockId.getPath(), settings);
                        break;
                    case CHAIN:
                        REGISTRY_HELPER.registerBlock(new ChainBlock(blockSettings), block, blockId.getPath(), settings);
                        break;
                    case PANE:
                        REGISTRY_HELPER.registerBlock(new PaneBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case DYEABLE:
                        net.minecraft.block.Block registeredBlock = REGISTRY_HELPER.registerBlockWithoutItem(new DyeableBlock(block, blockSettings), blockId.getPath());
                        REGISTRY_HELPER.registerItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                        REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                        (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                blockId.getPath() + "_be");
                        break;
                    case LOOM:
                        REGISTRY_HELPER.registerBlock(new LoomBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case CRAFTING_TABLE:
                        REGISTRY_HELPER.registerBlock(new CraftingTableBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case FURNACE:
                        Block furnace = REGISTRY_HELPER.registerBlock(new FurnaceBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        ((IBlockEntityType) BlockEntityType.FURNACE).vlAddBlocks(furnace);
                        break;
                    case BLAST_FURNACE:
                        Block blastFurnace = REGISTRY_HELPER.registerBlock(new BlastFurnaceBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        ((IBlockEntityType) BlockEntityType.BLAST_FURNACE).vlAddBlocks(blastFurnace);
                        break;
                    case SMOKER:
                        Block smoker = REGISTRY_HELPER.registerBlock(new SmokerBlockImpl(blockSettings), block, blockId.getPath(), settings);
                        ((IBlockEntityType) BlockEntityType.SMOKER).vlAddBlocks(smoker);
                        break;
                    case BARREL:
                        Block barrel = REGISTRY_HELPER.registerBlock(new BarrelBlockImpl(blockSettings), block, blockId.getPath(), settings);
                        ((IBlockEntityType) BlockEntityType.BARREL).vlAddBlocks(barrel);
                        break;
                    case PISTON:
                        Block piston = REGISTRY_HELPER.registerBlock(new PistonBlockImpl(false, blockSettings), block, blockId.getPath(), settings);
                        Block stickyPiston = REGISTRY_HELPER.registerBlock(new PistonBlockImpl(true, blockSettings), block, blockId.getPath() + "_sticky", settings);
                        ((IBlockEntityType) BlockEntityType.PISTON).vlAddBlocks(piston, stickyPiston);
                        break;
                    case CARPET:
                        REGISTRY_HELPER.registerBlock(new CarpetBlock(blockSettings), block, blockId.getPath(), settings);
                        break;
                    /*case PANE:
//                        BlockEntityType<SignBlockEntity> signBlockEntityBlockEntityType = REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(CraftingTableBlockEntity), Utils.appendToPath(blockId, "_base"));

                        REGISTRY_HELPER.registerBlock(new PaneBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;
                    case PANE:
                        REGISTRY_HELPER.registerBlock(new PaneBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        break;*/
                }
            }

            if (block.additional_information != null) {
                if (!block.additional_information.extraBlocksName.isEmpty()) {
                    Identifier identifier = new Identifier(blockId.getNamespace(), block.additional_information.extraBlocksName);
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
                                Utils.appendToPath(blockId, "_slab").getPath(), ItemGroup.BUILDING_BLOCKS, settings);
                    }
                    if (block.additional_information.stairs) {
                        REGISTRY_HELPER.registerBlock(new StairsImpl(block, blockSettings), block, new Identifier(id.modId(), blockId.getPath() + "_stairs").getPath(),
                                ItemGroup.BUILDING_BLOCKS);
                    }
                    if (block.additional_information.fence) {
                        REGISTRY_HELPER.registerBlock(new FenceImpl(block, blockSettings), block,
                                new Identifier(id.modId(), blockId.getPath() + "_fence").getPath(), ItemGroup.DECORATIONS, settings);
                    }
                    if (block.additional_information.fenceGate) {
                        REGISTRY_HELPER.registerBlock(new FenceGateImpl(block, blockSettings), block,
                                Utils.appendToPath(blockId, "_fence_gate").getPath(), ItemGroup.REDSTONE, settings);
                    }
                    if (block.additional_information.walls) {
                        REGISTRY_HELPER.registerBlock(new WallImpl(block, blockSettings), block,
                                Utils.appendToPath(blockId, "_wall").getPath(), ItemGroup.DECORATIONS, settings);
                    }
                    if (block.additional_information.pressurePlate) {
                        REGISTRY_HELPER.registerBlock(new PressurePlateBaseBlock(net.minecraft.block.Blocks.DARK_OAK_PRESSURE_PLATE, blockSettings, PressurePlateBlock.ActivationRule.EVERYTHING), block,
                                Utils.appendToPath(blockId, "_pressure_plate").getPath(), ItemGroup.REDSTONE, settings);
                    }
                    if (block.additional_information.button) {
                        REGISTRY_HELPER.registerBlock(new ButtonBaseBlock(true, net.minecraft.block.Blocks.DARK_OAK_BUTTON, blockSettings), block,
                                Utils.appendToPath(blockId, "_button").getPath(), ItemGroup.REDSTONE, settings);
                    }
                    if (block.additional_information.door) {
                        REGISTRY_HELPER.registerBlock(new DoorBaseBlock(net.minecraft.block.Blocks.DARK_OAK_DOOR, blockSettings), block,
                                Utils.appendToPath(blockId, "_door").getPath(), ItemGroup.REDSTONE, settings);
                    }
                    if (block.additional_information.trapdoor) {
                        REGISTRY_HELPER.registerBlock(new TrapdoorBlockImpl(blockSettings), block,
                                Utils.appendToPath(blockId, "_trapdoor").getPath(), ItemGroup.REDSTONE, settings);
                    }
                }
            }

            if (!addon.getConfigPackInfo().hasData) {
                new BlockInitThread(block);
            }

//            BlockRenderLayerMap.INSTANCE.putBlock(Registry.BLOCK.get(blockId), block.information.getRenderLayer());

            if (block.block_type == io.github.vampirestudios.obsidian.api.obsidian.block.Block.BlockType.OXIDIZING_BLOCK) {
                List<Identifier> names = new ArrayList<>();
                block.oxidizable_properties.stages.forEach(oxidationStage -> oxidationStage.blocks.forEach(variantBlock -> {
                    if (!names.contains(variantBlock.name.id)) names.add(variantBlock.name.id);
                }));
                names.forEach(identifier -> {
                    if (ContentRegistries.BLOCKS.get(identifier) != null) register(ContentRegistries.BLOCKS, "block", identifier, block);
                });
            } else {
                register(ContentRegistries.BLOCKS, "block", blockId, block);
            }
        } catch (Exception e) {
            if (block.block_type == io.github.vampirestudios.obsidian.api.obsidian.block.Block.BlockType.OXIDIZING_BLOCK) {
                List<Identifier> names = new ArrayList<>();
                block.oxidizable_properties.stages.forEach(oxidationStage -> oxidationStage.blocks.forEach(variantBlock -> {
                    if (!names.contains(variantBlock.name.id)) names.add(variantBlock.name.id);
                }));
                names.forEach(identifier -> failedRegistering("block", identifier.toString(), e));
            } else {
                failedRegistering("block", file.getName(), e);
            }
        }
    }

    @Override
    public String getType() {
        return "block";
    }

}

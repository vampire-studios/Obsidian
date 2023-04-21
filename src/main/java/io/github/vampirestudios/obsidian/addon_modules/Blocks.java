package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperBlockExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.block.AdditionalBlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.SaplingBaseBlock;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.threadhandlers.data.BlockInitThread;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.Utils;
import io.github.vampirestudios.vampirelib.blocks.entity.IBlockEntityType;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LoomBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.hjson.JsonValue;
import org.hjson.Stringify;

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

            ResourceLocation blockId;
            if (block.description != null) {
                blockId = block.description.identifier;
            } else {
                if (block.information.name.id != null) {
                    blockId = block.information.name.id;
                } else {
                    blockId = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
                    block.information.name.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
                }
            }

            FabricBlockSettings blockSettings;

            if (block.information.parentBlock != null) {
                blockSettings = FabricBlockSettings.copyOf(net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(block.information.parentBlock));
            } else {
                blockSettings = FabricBlockSettings.of(block.information.blockProperties.getMaterial());
            }

            blockSettings.destroyTime(block.information.blockProperties.hardness).explosionResistance(block.information.blockProperties.resistance)
                    .sound(block.information.blockProperties.getBlockSoundGroup())
                    .friction(block.information.blockProperties.slipperiness)
                    .emissiveRendering((state, world, pos) -> block.information.blockProperties.is_emissive)
                    .luminance(block.information.blockProperties.luminance)
                    .speedFactor(block.information.blockProperties.velocity_modifier)
                    .jumpFactor(block.information.blockProperties.jump_velocity_modifier);
            if (block.information.blockProperties.randomTicks) blockSettings.randomTicks();
            if (block.information.blockProperties.instant_break) blockSettings.instabreak();
            if (!block.information.blockProperties.collidable) blockSettings.noCollission();
            if (block.information.blockProperties.translucent) blockSettings.noOcclusion();
            if (block.information.blockProperties.dynamic_boundaries) blockSettings.dynamicShape();

            FabricItemSettings settings = new FabricItemSettings()/*.group(block.information.itemProperties.getItemGroup())*/;
            settings.stacksTo(block.information.itemProperties.maxStackSize);
            settings.rarity(Rarity.valueOf(block.information.itemProperties.rarity.toUpperCase(Locale.ROOT)));
            if (block.information.itemProperties.maxDurability != 0)
                settings.durability(block.information.itemProperties.maxDurability);
            if (!block.information.itemProperties.equipmentSlot.isEmpty() && !block.information.itemProperties.equipmentSlot.isBlank())
                settings.equipmentSlot(stack -> EquipmentSlot.byName(block.information.itemProperties.equipmentSlot.toLowerCase(Locale.ROOT)));
            if (block.food_information != null)
                settings.food(Registries.FOOD_COMPONENTS.get(block.food_information.foodComponent));
            if (block.information.itemProperties.fireproof) settings.fireResistant();

            RegistryHelperBlockExpanded expanded = (RegistryHelperBlockExpanded) REGISTRY_HELPER.blocks();

            if (block.additional_information != null) {
                if (block.additional_information.path) {
                    expanded.registerBlock(new PathBlockImpl(blockSettings, block), block, blockId.getPath(), settings);
                } else if (block.additional_information.lantern) {
                    expanded.registerBlock(new LanternBlock(blockSettings), block, blockId.getPath(), settings);
                } else if (block.additional_information.barrel) {
                    expanded.registerBlock(new BarrelBlock(blockSettings), block, blockId.getPath(), settings);
                } else if (block.additional_information.leaves) {
                    expanded.registerLeavesBlock(block, blockId.getPath(), settings);
                } else if (block.additional_information.chains) {
                    expanded.registerBlock(new ChainBlock(blockSettings), block, blockId.getPath(), settings);
                } else if (block.additional_information.cake_like) {
                    expanded.registerBlock(new CakeBlockImpl(block), block, blockId.getPath(), settings);
                }
            }

            if (block.block_type != null) {
                switch (block.block_type) {
                    case BLOCK, WOOD -> {
                        if (block.additional_information != null && block.additional_information.dyable) {
                            Block registeredBlock = expanded.registerBlockWithoutItem(blockId.getPath(), new DyeableBlock(block, blockSettings));
                            expanded.registerDyeableItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                            (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                    blockId.getPath() + "_be");
                        } else
                            expanded.registerBlock(new BlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                    }
                    case HORIZONTAL_DIRECTIONAL -> {
                        if (block.additional_information != null && block.additional_information.dyable && block.additional_information.sittable) {
                            Block registeredBlock = expanded.registerBlockWithoutItem(blockId.getPath(), new HorizontalFacingSittableAndDyableBlock(block, blockSettings));
                            expanded.registerDyeableItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                            (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                    blockId.getPath() + "_be");
                        } else if (block.additional_information != null && block.additional_information.dyable) {
                            Block registeredBlock = expanded.registerBlockWithoutItem(blockId.getPath(), new HorizontalFacingDyableBlockImpl(block, blockSettings));
                            expanded.registerDyeableItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                            (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                    blockId.getPath() + "_be");
                        } else if (block.additional_information != null && block.additional_information.sittable) {
                            Block registeredBlock = expanded.registerBlockWithoutItem(blockId.getPath(), new HorizontalFacingSittableBlock(block, blockSettings));
                            expanded.registerDyeableItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                            (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                    blockId.getPath() + "_be");
                        } else
                            expanded.registerBlock(new HorizontalFacingBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                    }
                    case DIRECTIONAL ->
                            expanded.registerBlock(new FacingBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case BED ->
                            expanded.registerBlock(new BedBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case CAMPFIRE ->
                            expanded.registerBlock(new CampfireBlockImpl(block.campfire_properties), block, blockId.getPath(), settings);
                    case STAIRS ->
                            expanded.registerBlock(new StairsImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case SLAB ->
                            expanded.registerBlock(new SlabImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case FENCE ->
                            expanded.registerBlock(new FenceImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case OVERWORLD_FENCE_GATE ->
                            expanded.registerBlock(new FenceGateImpl(block, blockSettings, WoodType.ACACIA), block, blockId.getPath(), settings);
                    case NETHER_FENCE_GATE ->
                            expanded.registerBlock(new FenceGateImpl(block, blockSettings, WoodType.CRIMSON), block, blockId.getPath(), settings);
                    case BAMBOO_FENCE_GATE ->
                            expanded.registerBlock(new FenceGateImpl(block, blockSettings, WoodType.BAMBOO), block, blockId.getPath(), settings);
                    case CAKE -> expanded.registerBlock(new CakeBlockImpl(block), block, blockId.getPath(), settings);
                    case OVERWORLD_TRAPDOOR ->
                            expanded.registerBlock(new TrapDoorBlock(blockSettings, WoodType.ACACIA.setType()), block, blockId.getPath(), settings);
                    case NETHER_TRAPDOOR ->
                            expanded.registerBlock(new TrapDoorBlock(blockSettings, WoodType.CRIMSON.setType()), block, blockId.getPath(), settings);
                    case BAMBOO_TRAPDOOR ->
                            expanded.registerBlock(new TrapDoorBlock(blockSettings, WoodType.BAMBOO.setType()), block, blockId.getPath(), settings);
                    case METAL_DOOR ->
                            expanded.registerBlock(new DoorBlock(blockSettings, BlockSetType.IRON), block, blockId.getPath(), settings);
                    case OVERWORLD_DOOR ->
                            expanded.registerBlock(new DoorBlock(blockSettings, WoodType.ACACIA.setType()), block, blockId.getPath(), settings);
                    case NETHER_DOOR ->
                            expanded.registerBlock(new DoorBlock(blockSettings, WoodType.CRIMSON.setType()), block, blockId.getPath(), settings);
                    case BAMBOO_DOOR ->
                            expanded.registerBlock(new DoorBlock(blockSettings, WoodType.BAMBOO.setType()), block, blockId.getPath(), settings);
                    case LOG ->
                            expanded.registerLog(block, blockId.getPath(), block.information.blockProperties.getMaterial().getColor(),
                                    block.information.blockProperties.getMaterial().getColor(), settings);
                    case STEM -> expanded.registerNetherStemBlock(block, blockId.getPath(), block.information.blockProperties.getMaterial().getColor(), settings);
                    case OXIDIZING_BLOCK -> {
                        List<ResourceLocation> names = new ArrayList<>();
                        block.oxidizable_properties.stages.forEach(oxidationStage -> oxidationStage.blocks.forEach(variantBlock -> {
                            if (!names.contains(variantBlock.name.id)) names.add(variantBlock.name.id);
                        }));
                        names.forEach(identifier -> expanded.registerBlock(new BlockImpl(block, blockSettings), block, identifier.getPath(), settings));
                    }
                    case PLANT -> {
                        if (block.additional_information != null) {
                            if (block.additional_information.waterloggable) {
                                expanded.registerBlock(new WaterloggablePlantBlockImpl(block, blockSettings.noCollission().instabreak()), block, blockId.getPath(), settings);
                            }
                        } else {
                            expanded.registerBlock(new PlantBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        }
                    }
                    case ROTATED_PILLAR -> expanded.registerBlock(new PillarBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case HORIZONTAL_FACING_PLANT ->
                        expanded.registerBlock(new HorizontalFacingPlantBlockImpl(block, blockSettings.noCollission().instabreak()), block, blockId.getPath(), settings);
                    case SAPLING -> expanded.registerBlock(new SaplingBaseBlock(block), block, blockId.getPath(), settings);
                    case TORCH ->
                        //TODO: Add particle lookup registry/method
                        expanded.registerBlock(new TorchBaseBlock(), block, blockId.getPath(), settings);
                    case BEEHIVE -> {
                        Block beeHive = expanded.registerBlock(new BeehiveBlock(blockSettings), block, blockId.getPath(), settings);
                        REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(BeehiveBlockEntity::new, beeHive), blockId.getPath() + "_beehive_be");
                    }
                    case LEAVES -> expanded.registerLeavesBlock(block, blockId.getPath(), settings);
                    case LADDER -> expanded.registerBlock(new CustomLadderBlock(), block, blockId.getPath(), settings);
                    case PATH ->
                            expanded.registerBlock(new PathBlockImpl(blockSettings, block), block, blockId.getPath(), settings);
                    case OVERWORLD_WOOD_BUTTON ->
                            expanded.registerBlock(new ButtonBlock(blockSettings, WoodType.ACACIA.setType(), 30, true), block, blockId.getPath(), settings);
                    case NETHER_WOOD_BUTTON ->
                            expanded.registerBlock(new ButtonBlock(blockSettings, WoodType.CRIMSON.setType(), 30, true), block, blockId.getPath(), settings);
                    case BAMBOO_BUTTON ->
                            expanded.registerBlock(new ButtonBlock(blockSettings, WoodType.BAMBOO.setType(), 30, true), block, blockId.getPath(), settings);
                    case STONE_BUTTON ->
                            expanded.registerBlock(new ButtonBlock(blockSettings, BlockSetType.STONE, 20, true), block, blockId.getPath(), settings);
                    case DOUBLE_PLANT -> {
                        if (block.additional_information != null) {
                            if (block.additional_information.waterloggable) {
                                expanded.registerDoubleBlock(new WaterloggableTallFlowerBlockImpl(block, blockSettings.noCollission().instabreak()), block, blockId.getPath(), settings);
                            }
                        } else {
                            expanded.registerDoubleBlock(new TallFlowerBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        }
                    }
                    case HORIZONTAL_FACING_DOUBLE_PLANT ->
                            expanded.registerDoubleBlock(new TallFlowerBlock(blockSettings.noCollission().instabreak()), block, blockId.getPath(), settings);
                    case HANGING_DOUBLE_LEAVES ->
                            expanded.registerHangingTallBlock(new HangingDoubleLeaves(blockSettings.noCollission().instabreak()), block, blockId.getPath(), settings);
                    case LANTERN -> expanded.registerBlock(new LanternBlock(blockSettings), block, blockId.getPath(), settings);
                    case CHAIN -> expanded.registerBlock(new ChainBlock(blockSettings), block, blockId.getPath(), settings);
                    case PANE -> expanded.registerBlock(new PaneBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case DYEABLE -> {
                        Block registeredBlock = expanded.registerBlockWithoutItem(blockId.getPath(), new DyeableBlock(block, blockSettings));
                        expanded.registerDyeableItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                        REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                        (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                                blockId.getPath() + "_be");
                    }
                    case LOOM -> expanded.registerBlock(new LoomBlock(blockSettings), block, blockId.getPath(), settings);
                    case CRAFTING_TABLE -> expanded.registerBlock(new CraftingTableBlock(blockSettings), block, blockId.getPath(), settings);
                    case FURNACE -> {
                        Block furnace = expanded.registerBlock(new FurnaceBlock(blockSettings), block, blockId.getPath(), settings);
                        ((IBlockEntityType) BlockEntityType.FURNACE).vlAddBlocks(furnace);
                    }
                    case BLAST_FURNACE -> {
                        Block blastFurnace = expanded.registerBlock(new BlastFurnaceBlock(blockSettings), block, blockId.getPath(), settings);
                        ((IBlockEntityType) BlockEntityType.BLAST_FURNACE).vlAddBlocks(blastFurnace);
                    }
                    case SMOKER -> {
                        Block smoker = expanded.registerBlock(new SmokerBlock(blockSettings), block, blockId.getPath(), settings);
                        ((IBlockEntityType) BlockEntityType.SMOKER).vlAddBlocks(smoker);
                    }
                    case BARREL -> {
                        Block barrel = expanded.registerBlock(new BarrelBlock(blockSettings), block, blockId.getPath(), settings);
                        ((IBlockEntityType) BlockEntityType.BARREL).vlAddBlocks(barrel);
                    }
                    case CARPET -> expanded.registerBlock(new CarpetBlock(blockSettings), block, blockId.getPath(), settings);

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
                AdditionalBlockInformation additionalInformation = block.additional_information;
                if (!additionalInformation.extraBlocksName.isEmpty()) {
                    ResourceLocation identifier = new ResourceLocation(blockId.getNamespace(), additionalInformation.extraBlocksName);
                    if (additionalInformation.slab) {
                        expanded.registerBlock(new SlabImpl(block, blockSettings), block,
                                Utils.appendToPath(identifier, "_slab").getPath(), CreativeModeTabs.BUILDING_BLOCKS, settings);
                    }
                    if (additionalInformation.stairs) {
                        expanded.registerBlock(new StairsImpl(block, blockSettings), block, new ResourceLocation(id.modId(),
                                        identifier.getPath() + "_stairs").getPath(), CreativeModeTabs.BUILDING_BLOCKS);
                    }
                    if (additionalInformation.fence) {
                        expanded.registerBlock(new FenceImpl(block, blockSettings), block,
                                new ResourceLocation(id.modId(), identifier.getPath() + "_fence").getPath(), CreativeModeTabs.BUILDING_BLOCKS, settings);
                    }
                    if (additionalInformation.fenceGate) {
                        SoundType soundType = additionalInformation.overworldLike ? SoundType.OVERWORLD :
                                additionalInformation.netherLike ? SoundType.NETHER : SoundType.BAMBOO;
                        expanded.registerBlock(new FenceGateImpl(block, blockSettings, getWoodTypeSpecificSounds(soundType)),
                                block, Utils.appendToPath(identifier, "_fence_gate").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
                    }
                    if (additionalInformation.walls) {
                        expanded.registerBlock(new WallImpl(block, blockSettings), block,
                                Utils.appendToPath(identifier, "_wall").getPath(), CreativeModeTabs.BUILDING_BLOCKS, settings);
                    }
                    if (additionalInformation.pressurePlate) {
                        SoundType soundType = additionalInformation.overworldLike ? SoundType.OVERWORLD :
                                additionalInformation.netherLike ? SoundType.NETHER : SoundType.BAMBOO;
                        expanded.registerBlock(new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockSettings,
                                getWoodTypeSpecificSounds(soundType).setType()), block,
                                Utils.appendToPath(identifier, "_pressure_plate").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
                    }
                    if (additionalInformation.button) {
                        SoundType soundType = additionalInformation.overworldLike ? SoundType.OVERWORLD :
                                additionalInformation.netherLike ? SoundType.NETHER : SoundType.BAMBOO;
                        expanded.registerBlock(new ButtonBlock(blockSettings, getWoodTypeSpecificSounds(soundType).setType(),
                                30, true), block, Utils.appendToPath(identifier, "_button").getPath(),
                                CreativeModeTabs.REDSTONE_BLOCKS, settings);
                    }
                    if (additionalInformation.door) {
                        SoundType soundType = additionalInformation.overworldLike ? SoundType.OVERWORLD :
                                additionalInformation.netherLike ? SoundType.NETHER : SoundType.BAMBOO;
                        expanded.registerBlock(new DoorBlock(blockSettings, getWoodTypeSpecificSounds(soundType).setType()), block,
                                Utils.appendToPath(identifier, "_door").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
                    }
                    if (additionalInformation.trapdoor) {
                        SoundType soundType = additionalInformation.overworldLike ? SoundType.OVERWORLD :
                                additionalInformation.netherLike ? SoundType.NETHER : SoundType.BAMBOO;
                        expanded.registerBlock(new TrapDoorBlock(blockSettings, getWoodTypeSpecificSounds(soundType).setType()),
                                block, Utils.appendToPath(identifier, "_trapdoor").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
                    }
                } else {
                    if (additionalInformation.slab) {
                        expanded.registerBlock(new SlabImpl(block, blockSettings), block,
                                Utils.appendToPath(blockId, "_slab").getPath(), CreativeModeTabs.BUILDING_BLOCKS, settings);
                    }
                    if (additionalInformation.stairs) {
                        expanded.registerBlock(new StairsImpl(block, blockSettings), block, new ResourceLocation(id.modId(), blockId.getPath() + "_stairs").getPath(),
                                CreativeModeTabs.BUILDING_BLOCKS);
                    }
                    if (additionalInformation.fence) {
                        expanded.registerBlock(new FenceImpl(block, blockSettings), block,
                                new ResourceLocation(id.modId(), blockId.getPath() + "_fence").getPath(), CreativeModeTabs.BUILDING_BLOCKS, settings);
                    }
                    if (additionalInformation.fenceGate) {
                        SoundType soundType = additionalInformation.overworldLike ? SoundType.OVERWORLD :
                                additionalInformation.netherLike ? SoundType.NETHER : SoundType.BAMBOO;
                        expanded.registerBlock(new FenceGateImpl(block, blockSettings, getWoodTypeSpecificSounds(soundType)),
                                block, Utils.appendToPath(blockId, "_fence_gate").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
                    }
                    if (additionalInformation.walls) {
                        expanded.registerBlock(new WallImpl(block, blockSettings), block,
                                Utils.appendToPath(blockId, "_wall").getPath(), CreativeModeTabs.BUILDING_BLOCKS, settings);
                    }
                    if (additionalInformation.pressurePlate) {
                        SoundType soundType = additionalInformation.overworldLike ? SoundType.OVERWORLD :
                                additionalInformation.netherLike ? SoundType.NETHER : SoundType.BAMBOO;
                        expanded.registerBlock(new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockSettings,
                                getWoodTypeSpecificSounds(soundType).setType()), block,
                                Utils.appendToPath(blockId, "_pressure_plate").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
                    }
                    if (additionalInformation.button) {
                        SoundType soundType = additionalInformation.overworldLike ? SoundType.OVERWORLD :
                                additionalInformation.netherLike ? SoundType.NETHER : SoundType.BAMBOO;
                        expanded.registerBlock(new ButtonBlock(blockSettings, getWoodTypeSpecificSounds(soundType).setType(),
                                30, true), block, Utils.appendToPath(blockId, "_button").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
                    }
                    if (additionalInformation.door) {
                        SoundType soundType = additionalInformation.overworldLike ? SoundType.OVERWORLD :
                                additionalInformation.netherLike ? SoundType.NETHER : SoundType.BAMBOO;
                        expanded.registerBlock(new DoorBlock(blockSettings, getWoodTypeSpecificSounds(soundType).setType()), block,
                                Utils.appendToPath(blockId, "_door").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
                    }
                    if (additionalInformation.trapdoor) {
                        SoundType soundType = additionalInformation.overworldLike ? SoundType.OVERWORLD :
                                additionalInformation.netherLike ? SoundType.NETHER : SoundType.BAMBOO;
                        expanded.registerBlock(new TrapDoorBlock(blockSettings, getWoodTypeSpecificSounds(soundType).setType()), block,
                                Utils.appendToPath(blockId, "_trapdoor").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
                    }
                }
            }

            if (!addon.getConfigPackInfo().hasData) {
                new BlockInitThread(block);
            }

//            BlockRenderLayerMap.INSTANCE.putBlock(Registry.BLOCK.get(blockId), block.information.getRenderLayer());

            if (block.block_type == io.github.vampirestudios.obsidian.api.obsidian.block.Block.BlockType.OXIDIZING_BLOCK) {
                List<ResourceLocation> names = new ArrayList<>();
                block.oxidizable_properties.stages.forEach(oxidationStage -> oxidationStage.blocks.forEach(variantBlock -> {
                    if (!names.contains(variantBlock.name.id)) names.add(variantBlock.name.id);
                }));
                names.forEach(identifier -> {
                    if (ContentRegistries.BLOCKS.get(identifier) != null)
                        register(ContentRegistries.BLOCKS, "block", identifier, block);
                });
            } else {
                register(ContentRegistries.BLOCKS, "block", blockId, block);
            }
        } catch (Exception e) {
            if (block.block_type == io.github.vampirestudios.obsidian.api.obsidian.block.Block.BlockType.OXIDIZING_BLOCK) {
                List<ResourceLocation> names = new ArrayList<>();
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

    private WoodType getWoodTypeSpecificSounds(SoundType soundType) {
        if (soundType == SoundType.OVERWORLD) {
            return WoodType.ACACIA;
        } else if (soundType == SoundType.NETHER) {
            return WoodType.CRIMSON;
        } else if (soundType == SoundType.BAMBOO) {
            return WoodType.BAMBOO;
        } else if (soundType == SoundType.CHERRY) {
            return WoodType.CHERRY;
        } else {
            return null;
        }
    }

    private enum SoundType {
        OVERWORLD,
        NETHER,
        BAMBOO,
        CHERRY
    }

}

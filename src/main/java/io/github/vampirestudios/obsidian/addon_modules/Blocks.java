package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.VanillaBlockSetTypes;
import io.github.vampirestudios.obsidian.api.VanillaWoodTypes;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperBlockExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.block.AdditionalBlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.SaplingBaseBlock;
import io.github.vampirestudios.obsidian.block.PaintingTableBlock;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OBE;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.threadhandlers.data.BlockInitThread;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
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
        io.github.vampirestudios.obsidian.api.obsidian.block.Block block = getBlock(addon, file);

        try {
            if (block == null) return;

            Identifier blockId = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
            block.information.name.id = blockId;

            BlockBehaviour.Properties blockSettings;

            if (block.information.parentBlock != null) {
                blockSettings = BlockBehaviour.Properties.ofLegacyCopy(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(block.information.parentBlock));
            } else {
                blockSettings = BlockBehaviour.Properties.of();
            }

            blockSettings.setId(ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, blockId));
            if (block.information.getBlockSettings() != null) {
                blockSettings.destroyTime(block.information.getBlockSettings().hardness)
                        .explosionResistance(block.information.getBlockSettings().resistance)
                        .mapColor(block.information.getBlockSettings().getMapColor())
                        .pushReaction(block.information.getBlockSettings().getPushReaction())
                        .sound(block.information.getBlockSettings().getBlockSoundGroup())
                        .friction(block.information.getBlockSettings().slipperiness)
                        .emissiveRendering((_, _, _) -> block.information.getBlockSettings().is_emissive)
                        .lightLevel(_ -> block.information.getBlockSettings().luminance)
                        .speedFactor(block.information.getBlockSettings().velocity_modifier)
                        .jumpFactor(block.information.getBlockSettings().jump_velocity_modifier);
                if (block.information.getBlockSettings().randomTicks) blockSettings.randomTicks();
                if (block.information.getBlockSettings().instant_break) blockSettings.instabreak();
                if (!block.information.getBlockSettings().collidable) blockSettings.noCollision();
                if (block.information.getBlockSettings().translucent) blockSettings.noOcclusion();
                if (block.information.getBlockSettings().dynamic_boundaries) blockSettings.dynamicShape();
            }

            Item.Properties settings = new Item.Properties();
            settings.useBlockDescriptionPrefix();
            settings.setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, blockId));
            if (block.information.getItemSettings() != null) {
                settings.stacksTo(block.information.getItemSettings().maxStackSize);
                settings.rarity(Rarity.valueOf(block.information.getItemSettings().rarity.toUpperCase(Locale.ROOT)));
                if (block.information.getItemSettings().durability != 0)
                    settings.durability(block.information.getItemSettings().durability);
//                if (!block.information.getItemSettings().wearableSlot.isEmpty() && !block.information.getItemSettings().wearableSlot.isBlank())
//                    settings.equipmentSlot(stack -> EquipmentSlot.byName(block.information.getItemSettings().wearableSlot.toLowerCase(Locale.ROOT)));
                if (block.food_information != null)
                    settings.food(Registries.FOODS.getValue(block.food_information.foodComponent));
                if (block.information.getItemSettings().fireproof) settings.fireResistant();
            }

            RegistryHelperBlockExpanded expanded = new RegistryHelperBlockExpanded(id.modId());

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

            if (block.getBlockType() != null) {
                switch (block.getBlockType()) {
                    case PAINTING_TABLE -> {
                        Block block1 = expanded.registerBlock(new PaintingTableBlock(block, blockSettings), block, blockId.getPath(), settings);
                        OBE.PAINTING_TABLE.addSupportedBlock(block1);
                    }
                    case BLOCK, WOOD -> {
                        if (block.additional_information != null && block.additional_information.dyable) {
                            Block registeredBlock = expanded.registerBlockWithoutItem(blockId.getPath(), new DyeableBlock(blockId, block, blockSettings));
                            expanded.registerDyeableItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(
                                    (blockPos, blockState) -> new DyableBlockEntity(blockId, blockPos, blockState),
                                    registeredBlock
                            ), blockId.getPath() + "_be");
                        } else
                            expanded.registerBlock(new BlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                    }
                    case HORIZONTAL_DIRECTIONAL -> {
                        if (block.additional_information != null && block.additional_information.dyable && block.additional_information.sittable) {
                            Block registeredBlock = expanded.registerBlockWithoutItem(
                                    blockId.getPath(),
                                    new HorizontalFacingSittableAndDyableBlock(blockId, block, blockSettings)
                            );
                            expanded.registerDyeableItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(
                                    (blockPos, blockState) -> new DyableBlockEntity(blockId, blockPos, blockState),
                                    registeredBlock
                            ), blockId.getPath() + "_be");
                        } else if (block.additional_information != null && block.additional_information.dyable) {
                            Block registeredBlock = expanded.registerBlockWithoutItem(
                                    blockId.getPath(),
                                    new HorizontalFacingDyableBlockImpl(blockId, block, blockSettings)
                            );
                            expanded.registerDyeableItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(
                                    (blockPos, blockState) -> new DyableBlockEntity(blockId, blockPos, blockState),
                                    registeredBlock
                            ), blockId.getPath() + "_be");
                        } else if (block.additional_information != null && block.additional_information.sittable) {
                            Block registeredBlock = expanded.registerBlockWithoutItem(blockId.getPath(), new HorizontalFacingSittableBlock(block,
                                    blockSettings));
                            expanded.registerDyeableItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                            REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(
                                    (blockPos, blockState) -> new DyableBlockEntity(blockId, blockPos, blockState),
                                    registeredBlock
                            ), blockId.getPath() + "_be");
                        } else expanded.registerBlock(new HorizontalFacingBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                    }
                    case DIRECTIONAL -> expanded.registerBlock(new FacingBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case BED -> expanded.registerBlock(new BedBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case CAMPFIRE -> expanded.registerBlock(new CampfireBlockImpl(block.campfire_properties), block, blockId.getPath(), settings);
                    case STAIRS -> expanded.registerBlock(new StairsImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case SLAB -> expanded.registerBlock(new SlabImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case FENCE -> expanded.registerBlock(new FenceImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case FENCE_GATE ->
                            expanded.registerBlock(new FenceGateImpl(block, blockSettings, VanillaWoodTypes.get(block.information.woodType)), block,
                                    blockId.getPath(), settings);
                    case CAKE -> expanded.registerBlock(new CakeBlockImpl(block), block, blockId.getPath(), settings);
                    case TRAPDOOR ->
                            expanded.registerBlock(new TrapDoorBlock(VanillaBlockSetTypes.get(block.information.blockSetType), blockSettings), block,
                                    blockId.getPath(), settings);
                    case DOOR ->
                            expanded.registerBlock(new DoorBlock(VanillaBlockSetTypes.get(block.information.blockSetType), blockSettings), block,
                                    blockId.getPath(), settings);
                    case LOG -> expanded.registerLog(block, blockSettings, blockId.getPath(), MapColor.STONE, MapColor.STONE, settings);
                    case STEM -> expanded.registerNetherStemBlock(block, blockId.getPath(), MapColor.STONE, settings);
                    case OXIDIZING_BLOCK -> {
                        List<Identifier> names = new ArrayList<>();
                        block.oxidizable_properties.stages.forEach(oxidationStage -> oxidationStage.blocks.forEach(variantBlock -> {
                            if (!names.contains(variantBlock.name.id)) names.add(variantBlock.name.id);
                        }));
                        names.forEach(identifier -> expanded.registerBlock(new BlockImpl(block, blockSettings), block, identifier.getPath(), settings));
                    }
                    case PLANT -> {
                        if (block.additional_information != null) {
                            if (block.additional_information.waterloggable) {
                                expanded.registerBlock(new WaterloggablePlantBlockImpl(block, blockSettings.noCollision().instabreak()), block,
                                        blockId.getPath(), settings);
                            }
                        } else {
                            expanded.registerBlock(new PlantBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        }
                    }
                    case ROTATED_PILLAR -> expanded.registerBlock(new PillarBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case HORIZONTAL_FACING_PLANT -> expanded.registerBlock(new HorizontalFacingPlantBlockImpl(block,
                                    blockSettings.noCollision().instabreak()), block, blockId.getPath(), settings);
                    case SAPLING -> expanded.registerBlock(new SaplingBaseBlock(block), block, blockId.getPath(), settings);
                    case TORCH -> expanded.registerBlock(new TorchBaseBlock(), block, blockId.getPath(), settings);
                    case BEEHIVE -> {
                        Block beeHive = expanded.registerBlock(new BeehiveBlock(blockSettings), block, blockId.getPath(), settings);
                        REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(BeehiveBlockEntity::new, beeHive),
                                blockId.getPath() + "_beehive_be");
                    }
                    case LEAVES -> expanded.registerLeavesBlock(block, blockId.getPath(), settings);
                    case LADDER -> expanded.registerBlock(new CustomLadderBlock(), block, blockId.getPath(), settings);
                    case PATH -> expanded.registerBlock(new PathBlockImpl(blockSettings, block), block, blockId.getPath(), settings);
                    case BUTTON -> expanded.registerBlock(new ButtonBlock(VanillaBlockSetTypes.get(block.information.blockSetType),
                            block.information.wooden_button ? 30 : 20, blockSettings), block, blockId.getPath(), settings);
                    case DOUBLE_PLANT -> {
                        if (block.additional_information != null) {
                            if (block.additional_information.waterloggable) {
                                expanded.registerDoubleBlock(new WaterloggableTallFlowerBlockImpl(block, blockSettings.noCollision().instabreak()),
                                        block, blockId.getPath(), settings);
                            }
                        } else {
                            expanded.registerDoubleBlock(new TallFlowerBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                        }
                    }
                    case HORIZONTAL_FACING_DOUBLE_PLANT -> expanded.registerDoubleBlock(new TallFlowerBlock(blockSettings.noCollision().instabreak()),
                            block, blockId.getPath(), settings);
                    case HANGING_DOUBLE_LEAVES ->
                            expanded.registerHangingTallBlock(new HangingDoubleLeaves(blockSettings.noCollision().instabreak()), block,
                                    blockId.getPath(), settings);
                    case LANTERN -> expanded.registerBlock(new LanternBlock(blockSettings), block, blockId.getPath(), settings);
                    case CHAIN -> expanded.registerBlock(new ChainBlock(blockSettings), block, blockId.getPath(), settings);
                    case PANE -> expanded.registerBlock(new PaneBlockImpl(block, blockSettings), block, blockId.getPath(), settings);
                    case DYEABLE -> {
                        Block registeredBlock = expanded.registerBlockWithoutItem(blockId.getPath(), new DyeableBlock(blockId, block, blockSettings));
                        expanded.registerDyeableItem(new CustomDyeableItem(block, registeredBlock, settings), blockId.getPath());
                        REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                        (blockPos, blockState) -> new DyableBlockEntity(blockId, blockPos, blockState), registeredBlock),
                                blockId.getPath() + "_be");
                    }
                    case LOOM -> expanded.registerBlock(new LoomBlock(blockSettings), block, blockId.getPath(), settings);
                    case CRAFTING_TABLE -> expanded.registerBlock(new CraftingTableBlock(blockSettings), block, blockId.getPath(), settings);
                    case FURNACE -> {
                        Block furnace = expanded.registerBlock(new FurnaceBlock(blockSettings), block, blockId.getPath(), settings);
                        BlockEntityType.FURNACE.addSupportedBlock(furnace);
                    }
                    case BLAST_FURNACE -> {
                        Block blastFurnace = expanded.registerBlock(new BlastFurnaceBlock(blockSettings), block, blockId.getPath(), settings);
                        BlockEntityType.BLAST_FURNACE.addSupportedBlock(blastFurnace);
                    }
                    case SMOKER -> {
                        Block smoker = expanded.registerBlock(new SmokerBlock(blockSettings), block, blockId.getPath(), settings);
                        BlockEntityType.SMOKER.addSupportedBlock(smoker);
                    }
                    case BARREL -> {
                        Block barrel = expanded.registerBlock(new BarrelBlock(blockSettings), block, blockId.getPath(), settings);
                        BlockEntityType.BARREL.addSupportedBlock(barrel);
                    }
                    case CARPET -> expanded.registerBlock(new CarpetBlock(blockSettings), block, blockId.getPath(), settings);
                }
            }

            if (block.additional_information != null) {
                AdditionalBlockInformation additionalInformation = block.additional_information;
                Identifier identifier = getIdentifier(additionalInformation, blockId);
                registerBlocksIfNeeded(additionalInformation, identifier, block, blockSettings, id, settings, expanded);
            }

            if (!addon.getConfigPackInfo().hasData) {
                new BlockInitThread(block);
            }

            if (block.getBlockType() == io.github.vampirestudios.obsidian.api.obsidian.block.Block.BlockType.OXIDIZING_BLOCK) {
                List<Identifier> names = new ArrayList<>();
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
            if (block.getBlockType() == io.github.vampirestudios.obsidian.api.obsidian.block.Block.BlockType.OXIDIZING_BLOCK) {
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

    private io.github.vampirestudios.obsidian.api.obsidian.block.Block getBlock(IAddonPack addon, File file) throws IOException, SyntaxError {
        if (addon.getConfigPackInfo() instanceof LegacyObsidianAddonInfo) {
            return parseJsonBlock(file);
        } else {
            ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addon.getConfigPackInfo();
            if (addonInfo.format == ObsidianAddonInfo.Format.JSON) {
                return parseJsonBlock(file);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.JSON5) {
                JsonObject jsonObject = Jankson.builder().build().load(file);
                return Jankson.builder().build().fromJson(jsonObject, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.YAML) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                mapper.findAndRegisterModules();
                return mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.TOML) {
                ObjectMapper mapper = new ObjectMapper(new TomlFactory());
                mapper.findAndRegisterModules();
                return mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.HJSON) {
                return BaseGson.GSON.fromJson(JsonValue.readHjson(new FileReader(file)).toString(Stringify.FORMATTED),
                        io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
            } else {
                return null;
            }
        }
    }

    private io.github.vampirestudios.obsidian.api.obsidian.block.Block parseJsonBlock(File file) throws IOException {
        return BaseGson.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
    }

    private Identifier getIdentifier(AdditionalBlockInformation info, Identifier defaultId) {
        return !info.extraBlocksName.isEmpty()
                ? Identifier.fromNamespaceAndPath(defaultId.getNamespace(), info.extraBlocksName)
                : defaultId;
    }

    private void registerBlocksIfNeeded(AdditionalBlockInformation info, Identifier identifier,
                                        io.github.vampirestudios.obsidian.api.obsidian.block.Block block, BlockBehaviour.Properties blockSettings,
                                        BasicAddonInfo id, Item.Properties settings, RegistryHelperBlockExpanded expanded) {
        SoundType soundType = determineSoundType(info);
        if (info.slab) {
            expanded.registerBlock(new SlabImpl(block, blockSettings), block,
                    Utils.appendToPath(identifier, "_slab").getPath(), CreativeModeTabs.BUILDING_BLOCKS, settings);
        }
        if (info.stairs) {
            expanded.registerBlock(new StairsImpl(block, blockSettings), block, Identifier.fromNamespaceAndPath(id.modId(),
                    identifier.getPath() + "_stairs").getPath(), CreativeModeTabs.BUILDING_BLOCKS);
        }
        if (info.fence) {
            expanded.registerBlock(new FenceImpl(block, blockSettings), block,
                    Identifier.fromNamespaceAndPath(id.modId(), identifier.getPath() + "_fence").getPath(), CreativeModeTabs.BUILDING_BLOCKS, settings);
        }
        if (info.fenceGate) {
            expanded.registerBlock(new FenceGateImpl(block, blockSettings, getWoodTypeSpecificSounds(soundType)),
                    block, Utils.appendToPath(identifier, "_fence_gate").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
        }
        if (info.walls) {
            expanded.registerBlock(new WallImpl(block, blockSettings), block,
                    Utils.appendToPath(identifier, "_wall").getPath(), CreativeModeTabs.BUILDING_BLOCKS, settings);
        }
        if (info.pressurePlate) {
            expanded.registerBlock(new PressurePlateBlock(getWoodTypeSpecificSounds(soundType).setType(), blockSettings), block,
                    Utils.appendToPath(identifier, "_pressure_plate").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
        }
        if (info.button) {
            expanded.registerBlock(new ButtonBlock(getWoodTypeSpecificSounds(soundType).setType(), 30, blockSettings),
                    block, Utils.appendToPath(identifier, "_button").getPath(),
                    CreativeModeTabs.REDSTONE_BLOCKS, settings);
        }
        if (info.door) {
            expanded.registerBlock(new DoorBlock(getWoodTypeSpecificSounds(soundType).setType(), blockSettings), block,
                    Utils.appendToPath(identifier, "_door").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
        }
        if (info.trapdoor) {
            expanded.registerBlock(new TrapDoorBlock(getWoodTypeSpecificSounds(soundType).setType(), blockSettings),
                    block, Utils.appendToPath(identifier, "_trapdoor").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, settings);
        }
    }

    private SoundType determineSoundType(AdditionalBlockInformation info) {
        return info.overworldLike ? SoundType.OVERWORLD : (info.netherLike ? SoundType.NETHER : SoundType.BAMBOO);
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
            return WoodType.ACACIA;
        }
    }

    private enum SoundType {
        OVERWORLD,
        NETHER,
        BAMBOO,
        CHERRY
    }

}

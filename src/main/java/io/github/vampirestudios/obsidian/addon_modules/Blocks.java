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
import io.github.vampirestudios.obsidian.api.obsidian.block.Block.BlockType;
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
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
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
import java.util.Optional;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Blocks implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo modInfo) throws IOException, SyntaxError {
        io.github.vampirestudios.obsidian.api.obsidian.block.Block block = getBlock(addon, file);
        if (block == null) return;

        Identifier blockId = Identifier.fromNamespaceAndPath(modInfo.modId(), file.getName().replace(".json", ""));
        block.information.id = blockId;

        BlockBehaviour.Properties blockProps = createBlockProperties(block);
        Item.Properties itemProps = createItemProperties(block).setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, blockId));
        ResourceKey<CreativeModeTab> tab = getCreativeTab(block);
        RegistryHelperBlockExpanded registry = new RegistryHelperBlockExpanded(modInfo.modId());

        try {
            registerSpecificBlockType(block, blockId, blockProps, itemProps, registry, tab);
            registerAdditionalFeatures(block, blockId, blockProps, itemProps, registry);

            if (!addon.getConfigPackInfo().hasData) {
                new BlockInitThread(block);
            }

            registerToContentRegistries(block, blockId);

        } catch (Exception e) {
            if (block.getBlockType() == BlockType.OXIDIZING_BLOCK) {
                getOxidationStageIds(block).forEach(id -> failedRegistering("block", id.toString(), e));
            } else {
                failedRegistering("block", file.getName(), e);
            }
        }
    }

    private BlockBehaviour.Properties createBlockProperties(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
        BlockBehaviour.Properties props = block.information.parentBlock != null
                ? BlockBehaviour.Properties.ofLegacyCopy(BuiltInRegistries.BLOCK.getValue(block.information.parentBlock))
                : BlockBehaviour.Properties.of();

        props.setId(ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, block.information.id));

        if (block.information.getBlockSettings() != null) {
            var settings = block.information.getBlockSettings();
            props.destroyTime(settings.hardness)
                    .explosionResistance(settings.resistance)
                    .mapColor(settings.getMapColor())
                    .pushReaction(settings.getPushReaction())
                    .sound(settings.getBlockSoundGroup())
                    .friction(settings.slipperiness)
                    .emissiveRendering((state, level, pos) -> settings.is_emissive)
                    .lightLevel(state -> settings.luminance)
                    .speedFactor(settings.velocity_modifier)
                    .jumpFactor(settings.jump_velocity_modifier)
                    .noOcclusion();

            if (settings.randomTicks) props.randomTicks();
            if (settings.instant_break) props.instabreak();
            if (!settings.collidable) props.noCollision();
//            if (settings.translucent) props.noOcclusion();
            if (settings.dynamic_boundaries) props.dynamicShape();
        }

        return props;
    }

    private Item.Properties createItemProperties(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
        Item.Properties props = new Item.Properties()
                .useBlockDescriptionPrefix();

        // --------------------------------------------------
        // 1. Legacy / JSON item settings (base defaults)
        // --------------------------------------------------
        if (block.information.getItemSettings() != null) {
            var settings = block.information.getItemSettings();

            props.stacksTo(settings.maxStackSize)
                    .rarity(Rarity.valueOf(settings.rarity.toUpperCase(Locale.ROOT)));

            if (settings.durability != 0) {
                props.durability(settings.durability);
            }

            if (settings.fireproof) {
                props.fireResistant();
            }
        }

        // --------------------------------------------------
        // 2. Food definition
        // --------------------------------------------------
        if (block.food_information != null) {
            props.food(Registries.FOODS.getValue(block.food_information.foodComponent));
        }

        // --------------------------------------------------
        // 3. Apply DataComponents LAST (override layer)
        // --------------------------------------------------
        if (block.components != null) {
            applyAllComponents(props, block.components);
        }

        return props;
    }

    @SuppressWarnings("unchecked")
    static <T> void applyAllComponents(Item.Properties props, DataComponentPatch map) {
        for (var e : map.entrySet()) {
            var type = (DataComponentType<T>) e.getKey();
            var opt  = (Optional<T>) e.getValue();
            opt.ifPresent(v -> props.component(type, v));
        }
    }

    private ResourceKey<CreativeModeTab> getCreativeTab(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
        // 1) Components override everything
        var comps = block.components;
//        if (comps != null) {
//            var opt = comps.get(OItemComponents.CREATIVE_TAB); // Optional<Identifier> (based on your usage)
//            if (opt != null && opt.isPresent()) {
//                Identifier id = opt.get();
//                return ResourceKey.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, id);
//            }
//        }

        // 2) Then item settings
        var settings = block.information.getItemSettings();
        if (settings != null) {
            var group = settings.getItemGroup();
            if (group != null) return group;

            var parent = settings.getParentSettings();
            if (parent != null && parent.getItemGroup() != null) return parent.getItemGroup();
        }

        // 3) Fallback
        return CreativeModeTabs.BUILDING_BLOCKS;
    }

    private void registerSpecificBlockType(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
                                           Identifier blockId,
                                           BlockBehaviour.Properties blockProps,
                                           Item.Properties itemProps,
                                           RegistryHelperBlockExpanded registry,
                                           ResourceKey<CreativeModeTab> itemGroup) {
        BlockType type = block.getBlockType();
        if (type == null) {
            handleAdditionalInformationFlags(block, blockId, blockProps, itemProps, registry, itemGroup);
            return;
        }

        switch (type) {
            case PAINTING_TABLE -> {
                Block paintingTable = registry.registerBlock(new PaintingTableBlock(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
                OBE.PAINTING_TABLE.addValidBlock(paintingTable);
            }
            case BLOCK, WOOD -> {
                if (isDyable(block)) {
                    registerDyableBlock(blockId, block, blockProps, itemProps, registry);
                } else if (isPowerable(block) || isToggleable(block)) {
                    registry.registerBlock(new PowerableBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
                } else {
                    registry.registerBlock(new BlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
                }
            }
            case HORIZONTAL_DIRECTIONAL -> {
                boolean dyable = isDyable(block);
                boolean sittable = isSittable(block);
                boolean powerable = isPowerable(block) || isToggleable(block);

                if (dyable && sittable) {
                    registerDyableAndSittableHorizontalBlock(blockId, block, blockProps, itemProps, registry);
                } else if (dyable) {
                    registerDyableHorizontalBlock(blockId, block, blockProps, itemProps, registry);
                } else if (sittable) {
                    registerSittableHorizontalBlock(blockId, block, blockProps, itemProps, registry);
                } else if (powerable) {
                    registry.registerBlock(new PowerableHorizontalFacingBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
                } else {
                    registry.registerBlock(new HorizontalFacingBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
                }
            }
            case DIRECTIONAL -> registry.registerBlock(new FacingBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
            case BED -> registry.registerBlock(new BedBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
            case CAMPFIRE -> registry.registerBlock(new CampfireBlockImpl(block.campfire_properties), block, blockId.getPath(), itemProps, itemGroup);
            case STAIRS -> registry.registerBlock(new StairsImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
            case SLAB -> registry.registerBlock(new SlabImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
            case FENCE -> registry.registerBlock(new FenceImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
            case FENCE_GATE -> registry.registerBlock(new FenceGateImpl(block, blockProps, VanillaWoodTypes.get(block.information.woodType)),
                    block, blockId.getPath(), itemProps, itemGroup);
            case CAKE -> registry.registerBlock(new CakeBlockImpl(block), block, blockId.getPath(), itemProps, itemGroup);
            case TRAPDOOR -> registry.registerBlock(new TrapDoorBlock(VanillaBlockSetTypes.get(block.information.blockSetType), blockProps),
                    block, blockId.getPath(), itemProps, itemGroup);
            case DOOR -> registry.registerBlock(new DoorBlock(VanillaBlockSetTypes.get(block.information.blockSetType), blockProps),
                    block, blockId.getPath(), itemProps, itemGroup);
            case LOG -> registry.registerLog(block, blockProps, blockId.getPath(), MapColor.STONE, MapColor.STONE, itemProps);
            case STEM -> registry.registerNetherStemBlock(block, blockId.getPath(), MapColor.STONE, itemProps);
            case OXIDIZING_BLOCK -> getOxidationStageIds(block).forEach(id ->
                    registry.registerBlock(new BlockImpl(block, blockProps), block, id.getPath(), itemProps, itemGroup));
            case PLANT -> {
                if (isWaterloggable(block)) {
                    registry.registerBlock(new WaterloggablePlantBlockImpl(block, blockProps.noCollision().instabreak()),
                            block, blockId.getPath(), itemProps, itemGroup);
                } else {
                    registry.registerBlock(new PlantBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
                }
            }
            case ROTATED_PILLAR -> registry.registerBlock(new PillarBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
            case HORIZONTAL_FACING_PLANT -> registry.registerBlock(new HorizontalFacingPlantBlockImpl(block, blockProps.noCollision().instabreak()),
                    block, blockId.getPath(), itemProps, itemGroup);
            case SAPLING -> registry.registerBlock(new SaplingBaseBlock(block), block, blockId.getPath(), itemProps, itemGroup);
            case TORCH -> registry.registerBlock(new TorchBaseBlock(), block, blockId.getPath(), itemProps, itemGroup);
            case BEEHIVE -> {
                Block beehive = registry.registerBlock(new BeehiveBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
                REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(BeehiveBlockEntity::new, beehive),
                        blockId.getPath() + "_beehive_be");
            }
            case LEAVES -> registry.registerLeavesBlock(block, blockId.getPath(), itemProps);
            case LADDER -> registry.registerBlock(new CustomLadderBlock(), block, blockId.getPath(), itemProps, itemGroup);
            case PATH -> registry.registerBlock(new PathBlockImpl(blockProps, block), block, blockId.getPath(), itemProps, itemGroup);
            case BUTTON -> registry.registerBlock(new ButtonBlock(VanillaBlockSetTypes.get(block.information.blockSetType),
                    block.information.wooden_button ? 30 : 20, blockProps), block, blockId.getPath(), itemProps, itemGroup);
            case DOUBLE_PLANT -> {
                if (isWaterloggable(block)) {
                    registry.registerDoubleBlock(new WaterloggableTallFlowerBlockImpl(block, blockProps.noCollision().instabreak()),
                            block, blockId.getPath(), itemProps);
                } else {
                    registry.registerDoubleBlock(new TallFlowerBlockImpl(block, blockProps), block, blockId.getPath(), itemProps);
                }
            }
            case HORIZONTAL_FACING_DOUBLE_PLANT -> registry.registerDoubleBlock(new TallFlowerBlock(blockProps.noCollision().instabreak()),
                    block, blockId.getPath(), itemProps);
            case HANGING_DOUBLE_LEAVES -> registry.registerHangingTallBlock(new HangingDoubleLeaves(blockProps.noCollision().instabreak()),
                    block, blockId.getPath(), itemProps);
            case LANTERN -> registry.registerBlock(new LanternBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
            case CHAIN -> registry.registerBlock(new ChainBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
            case PANE -> registry.registerBlock(new PaneBlockImpl(block, blockProps), block, blockId.getPath(), itemProps, itemGroup);
            case DYEABLE -> registerDyableBlock(blockId, block, blockProps, itemProps, registry);
            case LOOM -> registry.registerBlock(new LoomBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
            case CRAFTING_TABLE -> registry.registerBlock(new CraftingTableBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
            case FURNACE -> {
                Block furnace = registry.registerBlock(new FurnaceBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
                BlockEntityType.FURNACE.addValidBlock(furnace);
            }
            case BLAST_FURNACE -> {
                Block blast = registry.registerBlock(new BlastFurnaceBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
                BlockEntityType.BLAST_FURNACE.addValidBlock(blast);
            }
            case SMOKER -> {
                Block smoker = registry.registerBlock(new SmokerBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
                BlockEntityType.SMOKER.addValidBlock(smoker);
            }
            case BARREL -> {
                Block barrel = registry.registerBlock(new BarrelBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
                BlockEntityType.BARREL.addValidBlock(barrel);
            }
            case CARPET -> registry.registerBlock(new CarpetBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
        }
    }

    private void handleAdditionalInformationFlags(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
                                                  Identifier blockId,
                                                  BlockBehaviour.Properties blockProps,
                                                  Item.Properties itemProps,
                                                  RegistryHelperBlockExpanded registry,
                                                  ResourceKey<CreativeModeTab> itemGroup) {
        if (block.additional_information == null) return;

        var info = block.additional_information;
        if (info.path) registry.registerBlock(new PathBlockImpl(blockProps, block), block, blockId.getPath(), itemProps, itemGroup);
        else if (info.lantern) registry.registerBlock(new LanternBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
        else if (info.barrel) registry.registerBlock(new BarrelBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
        else if (info.leaves) registry.registerLeavesBlock(block, blockId.getPath(), itemProps);
        else if (info.chains) registry.registerBlock(new ChainBlock(blockProps), block, blockId.getPath(), itemProps, itemGroup);
        else if (info.cake_like) registry.registerBlock(new CakeBlockImpl(block), block, blockId.getPath(), itemProps, itemGroup);
    }

    private void registerAdditionalFeatures(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
                                             Identifier baseId,
                                             BlockBehaviour.Properties blockProps,
                                             Item.Properties itemProps,
                                             RegistryHelperBlockExpanded registry) {
        if (block.additional_information == null) return;

        Identifier id = getExtraBlockIdentifier(block.additional_information, baseId);
        registerExtraVariants(block, blockProps, itemProps, registry, id);
    }

    private void registerExtraVariants(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
                                       BlockBehaviour.Properties blockProps,
                                       Item.Properties itemProps,
                                       RegistryHelperBlockExpanded registry,
                                       Identifier baseId) {
        var info = block.additional_information;
        net.minecraft.world.level.block.state.properties.WoodType woodType = getWoodTypeForSounds(info);

        if (info.slab) {
            registry.registerBlock(new SlabImpl(block, blockProps), block,
                    Utils.appendToPath(baseId, "_slab").getPath(), CreativeModeTabs.BUILDING_BLOCKS, itemProps);
        }
        if (info.stairs) {
            registry.registerBlock(new StairsImpl(block, blockProps), block,
                    Utils.appendToPath(baseId, "_stairs").getPath(), CreativeModeTabs.BUILDING_BLOCKS, itemProps);
        }
        if (info.fence) {
            registry.registerBlock(new FenceImpl(block, blockProps), block,
                    Utils.appendToPath(baseId, "_fence").getPath(), CreativeModeTabs.BUILDING_BLOCKS, itemProps);
        }
        if (info.fenceGate) {
            registry.registerBlock(new FenceGateImpl(block, blockProps, woodType),
                    block, Utils.appendToPath(baseId, "_fence_gate").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, itemProps);
        }
        if (info.walls) {
            registry.registerBlock(new WallImpl(block, blockProps), block,
                    Utils.appendToPath(baseId, "_wall").getPath(), CreativeModeTabs.BUILDING_BLOCKS, itemProps);
        }
        if (info.pressurePlate) {
            registry.registerBlock(new PressurePlateBlock(woodType.setType(), blockProps), block,
                    Utils.appendToPath(baseId, "_pressure_plate").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, itemProps);
        }
        if (info.button) {
            registry.registerBlock(new ButtonBlock(woodType.setType(), 30, blockProps), block,
                    Utils.appendToPath(baseId, "_button").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, itemProps);
        }
        if (info.door) {
            registry.registerBlock(new DoorBlock(woodType.setType(), blockProps), block,
                    Utils.appendToPath(baseId, "_door").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, itemProps);
        }
        if (info.trapdoor) {
            registry.registerBlock(new TrapDoorBlock(woodType.setType(), blockProps), block,
                    Utils.appendToPath(baseId, "_trapdoor").getPath(), CreativeModeTabs.REDSTONE_BLOCKS, itemProps);
        }
    }

    private net.minecraft.world.level.block.state.properties.WoodType getWoodTypeForSounds(AdditionalBlockInformation info) {
        if (info.overworldLike) return WoodType.ACACIA;
        if (info.netherLike) return WoodType.CRIMSON;
        if (info.bambooLike) return WoodType.BAMBOO; // assuming you have a flag; adjust if needed
        return WoodType.CHERRY;
    }

    private Identifier getExtraBlockIdentifier(AdditionalBlockInformation info, Identifier defaultId) {
        return info.extraBlocksName.isBlank()
                ? defaultId
                : Identifier.fromNamespaceAndPath(defaultId.getNamespace(), info.extraBlocksName);
    }

    private boolean isDyable(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
        return block.additional_information != null && block.additional_information.dyable;
    }

    private boolean isSittable(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
        return block.additional_information != null && block.additional_information.sittable;
    }

    private boolean isWaterloggable(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
        return block.additional_information != null && block.additional_information.waterloggable;
    }

    private boolean isPowerable(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
        return block.information != null && block.information.powerable;
    }

    private boolean isToggleable(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
        return block.information != null && block.information.toggleable;
    }

    private void registerDyableBlock(Identifier blockId, io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
                                     BlockBehaviour.Properties props, Item.Properties itemProps,
                                     RegistryHelperBlockExpanded registry) {
        Block registered = registry.registerBlockWithoutItem(blockId.getPath(), new DyeableBlock(blockId, block, props));
        registry.registerDyeableItem(new CustomDyeableItem(block, registered, itemProps), blockId.getPath());
        REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> new DyeableBlockEntity(blockId, pos, state), registered),
                blockId.getPath() + "_be");
    }

    private void registerDyableHorizontalBlock(Identifier blockId, io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
                                               BlockBehaviour.Properties props, Item.Properties itemProps,
                                               RegistryHelperBlockExpanded registry) {
        Block registered = registry.registerBlockWithoutItem(blockId.getPath(),
                new HorizontalFacingDyableBlockImpl(blockId, block, props));
        registry.registerDyeableItem(new CustomDyeableItem(block, registered, itemProps), blockId.getPath());
        REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> new DyeableBlockEntity(blockId, pos, state), registered),
                blockId.getPath() + "_be");
    }

    private void registerSittableHorizontalBlock(Identifier blockId, io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
                                                 BlockBehaviour.Properties props, Item.Properties itemProps,
                                                 RegistryHelperBlockExpanded registry) {
        Block registered = registry.registerBlockWithoutItem(blockId.getPath(),
                new HorizontalFacingSittableBlock(block, props));
        registry.registerItem(new CustomBlockItem(block, registered, itemProps), blockId.getPath()); // or use appropriate item registration
    }

    private void registerDyableAndSittableHorizontalBlock(Identifier blockId, io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
                                                          BlockBehaviour.Properties props, Item.Properties itemProps,
                                                          RegistryHelperBlockExpanded registry) {
        Block registered = registry.registerBlockWithoutItem(blockId.getPath(),
                new HorizontalFacingSittableAndDyableBlock(blockId, block, props));
        registry.registerDyeableItem(new CustomDyeableItem(block, registered, itemProps), blockId.getPath());
        REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create(
                        (pos, state) -> new DyeableBlockEntity(blockId, pos, state), registered),
                blockId.getPath() + "_be");
    }

    private List<Identifier> getOxidationStageIds(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
        List<Identifier> ids = new ArrayList<>();
        block.oxidizable_properties.stages.forEach(stage ->
                stage.blocks.forEach(varBlock -> {
                    if (!ids.contains(varBlock.id)) ids.add(varBlock.id);
                }));
        return ids;
    }

    private void registerToContentRegistries(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Identifier blockId) {
        if (block.getBlockType() == BlockType.OXIDIZING_BLOCK) {
            getOxidationStageIds(block).forEach(id -> {
                if (ContentRegistries.BLOCKS.get(id) != null) {
                    register(ContentRegistries.BLOCKS, "block", id, block);
                }
            });
        } else {
            register(ContentRegistries.BLOCKS, "block", blockId, block);
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

package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TextureAndModelInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.minecraft.obsidian.DyableBlockEntity;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.models.JModel;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;
import java.util.Objects;

public class BlockInitThread implements Runnable {

    private final Block block;
    private final RuntimeResourcePack resourcePack;

    public BlockInitThread(RuntimeResourcePack resourcePack, Block blockIn) {
        block = blockIn;
        this.resourcePack = resourcePack;
    }

    public static int getBlockEntityColor(Block block, BlockGetter view, BlockPos pos) {
        BlockEntity entity = view.getBlockEntity(pos);
        return entity != null ? ((DyableBlockEntity) entity).getDyeColor() : block.additional_information.defaultColor;
    }

    public static void translation(Map<String, String> translated, ResourceLocation blockId, String unTranslatedType, String translatedType) {
        translated.forEach((languageId, name) -> ClientInit.addTranslation(
                blockId.getNamespace(), languageId,
                "block." + blockId.getNamespace() + "." + blockId.getPath() + unTranslatedType, name + translatedType
        ));
    }

    @Override
    public void run() {
        try {
            net.minecraft.world.level.block.Block block1 = BuiltInRegistries.BLOCK.get(block.information.name.id);
            NameInformation nameInformation = block.information.name;
            ResourceLocation blockId = nameInformation.id;
            Map<String, String> translated = nameInformation.translations;
            BlockRenderLayerMap.INSTANCE.putBlock(block1, RenderType.cutout());
            if (translated != null) {
                translated.forEach((languageId, name) -> ClientInit.addTranslation(
                        blockId.getNamespace(), languageId, nameInformation.text, name
                ));
                translation(translated, blockId, "", "");
            }
            if (block.lore != null) {
                for (TooltipInformation lore : block.lore) {
                    if (lore.text.textType.equals("translatable")) {
                        lore.text.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                                blockId.getNamespace(), languageId, lore.text.text, name
                        ));
                    }
                }
            }
            if (block.rendering != null) {
                if (block.getBlockType() != null) {
                    switch (block.getBlockType()) {
                        default -> generateBlockState(block, resourcePack, blockId);
                        case HORIZONTAL_DIRECTIONAL -> generateHorizontalFacingBlockState(block, resourcePack, blockId);
                        case DIRECTIONAL -> generateFacingBlockState(block, resourcePack, blockId);
                        case LOG, ROTATED_PILLAR, STEM -> generatePillarBlockState(block, resourcePack, blockId);
                        case FURNACE, BLAST_FURNACE, SMOKER ->
                                ARRPGenerationHelper.generateOnOffHorizontalFacingBlockState(resourcePack, blockId);
                        case BAMBOO_DOOR, NETHER_DOOR, OVERWORLD_DOOR, METAL_DOOR ->
                                ARRPGenerationHelper.generateDoorBlockState(resourcePack, blockId);
                        case BAMBOO_TRAPDOOR, NETHER_TRAPDOOR, OVERWORLD_TRAPDOOR ->
                                ARRPGenerationHelper.generateTrapdoorBlockState(resourcePack, blockId);
                        case STAIRS -> ARRPGenerationHelper.generateStairsBlockState(resourcePack, blockId);
                        case SLAB -> ARRPGenerationHelper.generateSlabBlockState(resourcePack, blockId, blockId);
                        case WALL -> ARRPGenerationHelper.generateWallBlockState(resourcePack, blockId);
                        case FENCE -> ARRPGenerationHelper.generateFenceBlockState(resourcePack, blockId);
                        case PISTON -> {
                            ARRPGenerationHelper.generatePistonBlockState(resourcePack, blockId,
                                    block.rendering.blockState.model, block.rendering.blockState.stickyModel);
                            ARRPGenerationHelper.generatePistonModels(resourcePack, blockId,
                                    block.rendering.blockModel.parent, block.rendering.blockModel.textures,
                                    block.rendering.stickyPiston.parent, block.rendering.stickyPiston.textures);
                        }
                        case OXIDIZING_BLOCK -> {
                            for (Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                for (Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                    if (variantBlock.display.blockState.model != null) {
                                        ARRPGenerationHelper.generateBasicBlockState(resourcePack, variantBlock.name.id,
                                                variantBlock.display.blockModel.parent);
                                    } else {
                                        if (variantBlock.display.blockModel != null)
                                            ARRPGenerationHelper.generateBasicBlockState(resourcePack, variantBlock.name.id,
                                                    variantBlock.display.blockModel.parent);
                                        else
                                            ARRPGenerationHelper.generateBasicBlockState(resourcePack, variantBlock.name.id,
                                                    block.rendering.blockState.model);
                                    }
                                }
                            }
                        }
                        case LANTERN -> {
                            if (block.rendering.blockState.model != null) {
                                ARRPGenerationHelper.generateLanternBlockState(resourcePack, blockId,
                                        block.rendering.blockState.model, block.rendering.blockState.hangingModel);
                            } else {
                                ARRPGenerationHelper.generateLanternBlockState(resourcePack, blockId);
                            }
                        }
                    }
                }
                if (block.rendering.blockModel != null) {
                    TextureAndModelInformation textureAndModelInformation = block.rendering.blockModel;
                    if (block.getBlockType() != null) {
                        switch (block.getBlockType()) {
                            case OXIDIZING_BLOCK:
                                for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                    for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                        textureAndModelInformation = variantBlock.display.blockModel;
                                        ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                    }
                                }
                                break;
                            case LANTERN:
                                TextureAndModelInformation hangingModelInformation = block.rendering.hangingModel;
                                ARRPGenerationHelper.generateLanternBlockModels(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures, hangingModelInformation.parent, hangingModelInformation.textures);
                                break;
                            default:
                                if (resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(blockId, "block/")) != null) {
                                    System.out.printf("Skipping model generation cause %s already exists%n", Utils.prependToPath(blockId, "block/"));
                                    return;
                                }
                                ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                        }
                    } else {
                        ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                    }
                }
                if (block.rendering.model != null) {
                    TextureAndModelInformation textureAndModelInformation = block.rendering.model;
                    if (block.additional_information != null) {
                        if (block.additional_information.slab) {
                            ARRPGenerationHelper.generateSlabBlockState(resourcePack, Utils.appendToPath(blockId, "_slab"), blockId);
//                            ArtificeGenerationHelper.generateSlabBlockModels(resourcePack, Utils.appendToPath(blockId, "_slab"), textureAndModelInformation.textures);
                            ARRPGenerationHelper.generateBlockItemModel(resourcePack, Utils.appendToPath(blockId, "_slab"), Utils.appendToPath(blockId, "_slab"));
                        }

                        if (block.additional_information.stairs) {
                            ARRPGenerationHelper.generateStairsBlockState(resourcePack, Utils.appendToPath(blockId, "_stairs"));
//                            ArtificeGenerationHelper.generateStairsBlockModels(resourcePack, Utils.appendToPath(blockId, "_stairs"), textureAndModelInformation.textures);
                            ARRPGenerationHelper.generateBlockItemModel(resourcePack, Utils.appendToPath(blockId, "_stairs"), Utils.appendToPath(blockId, "_stairs"));
                        }

                        if (block.additional_information.walls) {
                            ARRPGenerationHelper.generateWallBlockState(resourcePack, Utils.appendToPath(blockId, "_wall"));
//                            ArtificeGenerationHelper.generateWallBlockModels(resourcePack, Utils.appendToPath(blockId, "_wall"), textureAndModelInformation.textures);
                            ARRPGenerationHelper.generateBlockItemModel(resourcePack, Utils.appendToPath(blockId, "_wall"), Utils.appendToPath(blockId, "_wall_inventory"));
                        }
                    }

                    if (block.getBlockType() != null) {
                        switch (block.getBlockType()) {
                            case OXIDIZING_BLOCK:
                                for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                    for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                        textureAndModelInformation = variantBlock.display.model;
                                        ARRPGenerationHelper.generateBasicBlockState(resourcePack, Utils.prependToPath(variantBlock.name.id, "block/"));
                                        ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                    }
                                }
                                break;
                            case LANTERN:
                                TextureAndModelInformation textureAndModelInformation2 = block.rendering.hangingModel;
                                ARRPGenerationHelper.generateLanternBlockModels(resourcePack, blockId, Utils.prependToPath(blockId, "block/"), textureAndModelInformation.textures, textureAndModelInformation2.parent, textureAndModelInformation2.textures);
                                break;
                            case DIRECTIONAL:
                                ARRPGenerationHelper.generateFacingBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
                                if (!textureAndModelInformation.parent.getNamespace().equals("minecraft") && resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(blockId, "block/")) != null) {
                                    System.out.printf("Skipping model generation cause %s already exists%n", Utils.prependToPath(blockId, "block/"));
                                    return;
                                }
                                ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case HORIZONTAL_DIRECTIONAL:
                                ARRPGenerationHelper.generateHorizontalFacingBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
                                if (!textureAndModelInformation.parent.getNamespace().equals("minecraft") && resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(blockId, "block/")) != null) {
                                    System.out.printf("Skipping model generation cause %s already exists%n", Utils.prependToPath(blockId, "block/"));
                                    return;
                                }
                                ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case ROTATED_PILLAR, LOG:
                                ARRPGenerationHelper.generatePillarBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
                                if (!textureAndModelInformation.parent.getNamespace().equals("minecraft") && resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(blockId, "block/")) != null) {
                                    System.out.printf("Skipping model generation cause %s already exists%n", Utils.prependToPath(blockId, "block/"));
                                    return;
                                }
                                ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            default:
                                ARRPGenerationHelper.generateBasicBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
                                if (!textureAndModelInformation.parent.getNamespace().equals("minecraft") && resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(blockId, "block/")) != null) {
                                    System.out.printf("Skipping model generation cause %s already exists%n", Utils.prependToPath(blockId, "block/"));
                                    return;
                                }
                                ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                        }
                    }
                }

                if (block.rendering.itemModel != null) {
                    TextureAndModelInformation textureAndModelInformation = block.rendering.itemModel;
                    JModel itemModel = JModel.model(textureAndModelInformation.parent);
                    if (textureAndModelInformation.textures != null)
                        textureAndModelInformation.textures.forEach((s, location) -> itemModel.textures(JModel.textures().var(s, location.toString())));
                    resourcePack.addModel(itemModel, blockId);
                } else ARRPGenerationHelper.generateBlockItemModel1(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
            }
            if (block.additional_information != null && translated != null) {
                if (block.additional_information.slab) {
                    translation(translated, blockId, "_slab", " Slab");
                }
                if (block.additional_information.stairs) {
                    translation(translated, blockId, "_stairs", " Stairs");
                }
                if (block.additional_information.fence) {
                    translation(translated, blockId, "_fence", " Fence");
                }
                if (block.additional_information.fenceGate) {
                    translation(translated, blockId, "_fence_gate", " Fence Gate");
                }
                if (block.additional_information.walls) {
                    translation(translated, blockId, "_wall", " Wall");
                }
            }

            if (block.additional_information != null && block.additional_information.dyable) {
                net.minecraft.world.level.block.Block registeredBlock = BuiltInRegistries.BLOCK.get(nameInformation.id);
                ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
                        getBlockEntityColor(block, Objects.requireNonNull(world), pos), registeredBlock);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> stack.getOrCreateTagElement("display").contains("color") ?
                        stack.getOrCreateTagElement("display").getInt("color") : block.additional_information.defaultColor, registeredBlock.asItem());
            }
            if (block.getBlockType() != null && block.getBlockType() == Block.BlockType.DYEABLE) {
                net.minecraft.world.level.block.Block registeredBlock = BuiltInRegistries.BLOCK.get(nameInformation.id);
                ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
                        getBlockEntityColor(block, Objects.requireNonNull(world), pos), registeredBlock);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> stack.getOrCreateTagElement("display").contains("color") ?
                        stack.getOrCreateTagElement("display").getInt("color") : block.additional_information.defaultColor, registeredBlock.asItem());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateBlockState(Block block, RuntimeResourcePack resourcePack, ResourceLocation blockId) {
        if (block.rendering.blockState != null && block.rendering.blockState.model != null) {
            ARRPGenerationHelper.generateBasicBlockState(resourcePack, blockId, block.rendering.blockState.model);
        } else {
            ARRPGenerationHelper.generateBasicBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
        }
    }

    private void generatePillarBlockState(Block block, RuntimeResourcePack resourcePack, ResourceLocation blockId) {
        if (block.rendering.blockState != null && block.rendering.blockState.model != null) {
            ARRPGenerationHelper.generatePillarBlockState(resourcePack, blockId, block.rendering.blockState.model);
        } else {
            ARRPGenerationHelper.generateBasicBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
        }
    }

    private void generateHorizontalFacingBlockState(Block block, RuntimeResourcePack resourcePack, ResourceLocation blockId) {
        if (block.rendering.blockState != null && block.rendering.blockState.model != null) {
            ARRPGenerationHelper.generateHorizontalFacingBlockState(resourcePack, blockId, block.rendering.blockState.model);
        } else {
            ARRPGenerationHelper.generateBasicBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
        }
    }

    private void generateFacingBlockState(Block block, RuntimeResourcePack resourcePack, ResourceLocation blockId) {
        if (block.rendering.blockState != null && block.rendering.blockState.model != null) {
            ARRPGenerationHelper.generateFacingBlockState(resourcePack, blockId, block.rendering.blockState.model);
        } else {
            ARRPGenerationHelper.generateBasicBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
        }
    }

}

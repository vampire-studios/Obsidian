package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import io.github.vampirestudios.obsidian.api.obsidian.TextureAndModelInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.client.ARRPGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.minecraft.obsidian.DyeableBlockEntity;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;

public class BlockInitThread implements Runnable {

    private final Block block;
    private final RuntimeResourcePack resourcePack;

    public BlockInitThread(RuntimeResourcePack resourcePack, Block blockIn) {
        block = blockIn;
        this.resourcePack = resourcePack;
    }

    public static int getBlockEntityColor(Block block, BlockGetter view, BlockPos pos) {
        BlockEntity be = view.getBlockEntity(pos);
        if (be instanceof DyeableBlockEntity dye) return dye.getDyeColor();
        return block.additional_information.defaultColor;
    }

    public static void translation(Map<String, String> translated, Identifier blockId, String unTranslatedType, String translatedType) {
        translated.forEach((languageId, name) -> ClientInit.addTranslation(
                blockId.getNamespace(), languageId,
                "block." + blockId.getNamespace() + "." + blockId.getPath() + unTranslatedType, name + translatedType
        ));
    }

    @Override
    public void run() {
        try {
            NameInformation nameInformation = block.information.name;
            Identifier blockId = block.information.id;
            Map<String, String> translated = nameInformation.translations;
            if (translated != null) {
                translated.forEach((languageId, name) -> ClientInit.addTranslation(
                        blockId.getNamespace(), languageId, nameInformation.text, name
                ));
                translation(translated, blockId, "", "");
            }
            if (block.lore != null) {
                for (SpecialText lore : block.lore) {
                    if (lore.textType.equals("translatable")) {
                        lore.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                                blockId.getNamespace(), languageId, lore.text, name
                        ));
                    }
                }
            }
            if (block.rendering != null) {
                if (block.getBlockType() != null) {
                    switch (block.getBlockType()) {
						case HORIZONTAL_DIRECTIONAL -> generateHorizontalFacingBlockState(block, resourcePack, blockId);
                        case DIRECTIONAL -> generateFacingBlockState(block, resourcePack, blockId);
                        case LOG, ROTATED_PILLAR, STEM -> generatePillarBlockState(block, resourcePack, blockId);
//                        case FURNACE, BLAST_FURNACE, SMOKER ->
//                                ARRPGenerationHelper.generateOnOffHorizontalFacingBlockState(resourcePack, blockId);
//                        case DOOR ->
//                                ARRPGenerationHelper.generateDoorBlockState(resourcePack, blockId);
//                        case TRAPDOOR ->
//                                ARRPGenerationHelper.generateTrapdoorBlockState(resourcePack, blockId);
//                        case STAIRS -> ARRPGenerationHelper.generateStairsBlockState(resourcePack, blockId);
                        case SLAB -> ARRPGenerationHelper.generateSlabBlockState(resourcePack, blockId, blockId);
//                        case WALL -> ARRPGenerationHelper.generateWallBlockState(resourcePack, blockId);
//                        case FENCE -> ARRPGenerationHelper.generateFenceBlockState(resourcePack, blockId);
//                        case PISTON -> {
//                            ARRPGenerationHelper.generatePistonBlockState(resourcePack, blockId,
//                                    block.rendering.blockState.model, block.rendering.blockState.stickyModel);
//                            ARRPGenerationHelper.generatePistonModels(resourcePack, blockId,
//                                    block.rendering.blockModel.parent, block.rendering.blockModel.textures,
//                                    block.rendering.stickyPiston.parent, block.rendering.stickyPiston.textures);
//                        }
                        case OXIDIZING_BLOCK -> {
                            for (Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                for (Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                    Identifier outModelId = Utils.prependToPath(variantBlock.id, "block/");

                                    // if block_model was given as a string, use it directly
                                    if (variantBlock.display.blockModel != null
                                            && variantBlock.display.blockModel.isJsonPrimitive()
                                            && variantBlock.display.blockModel.getAsJsonPrimitive().isString()) {
                                        outModelId = Identifier.parse(variantBlock.display.blockModel.getAsString());
                                    }

                                    ARRPGenerationHelper.generateBasicBlockState(resourcePack, variantBlock.id, outModelId);
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
						default -> generateBlockState(block, resourcePack, blockId);
					}
                }
                if (block.rendering.getBlockModel() != null) {
                    TextureAndModelInformation textureAndModelInformation = block.rendering.getBlockModel();
                    if (block.getBlockType() != null) {
                        switch (block.getBlockType()) {
                            case OXIDIZING_BLOCK:
                                for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                    for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                        textureAndModelInformation = variantBlock.display.getBlockModel();
                                        ARRPGenerationHelper.generateBlockModel(resourcePack, variantBlock.id,
                                                textureAndModelInformation.parent, textureAndModelInformation.textures);
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
                                    break;
                                }
                                ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                        }
                    } else {
                        ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                    }
                }
                if (block.rendering.getModel() != null) {
                    TextureAndModelInformation textureAndModelInformation = block.rendering.getModel();
                    if (block.additional_information != null) {
                        if (block.additional_information.slab) {
                            ARRPGenerationHelper.generateSlabBlockState(resourcePack, Utils.appendToPath(blockId, "_slab"), blockId);
//                            ArtificeGenerationHelper.generateSlabBlockModels(resourcePack, Utils.appendToPath(blockId, "_slab"), textureAndModelInformation.textures);
//                            ARRPGenerationHelper.generateBlockItemModel(resourcePack, Utils.appendToPath(blockId, "_slab"), Utils.appendToPath(blockId, "_slab"));
                        }

                        if (block.additional_information.stairs) {
//                            ARRPGenerationHelper.generateStairsBlockState(resourcePack, Utils.appendToPath(blockId, "_stairs"));
//                            ArtificeGenerationHelper.generateStairsBlockModels(resourcePack, Utils.appendToPath(blockId, "_stairs"), textureAndModelInformation.textures);
//                            ARRPGenerationHelper.generateBlockItemModel(resourcePack, Utils.appendToPath(blockId, "_stairs"), Utils.appendToPath(blockId, "_stairs"));
                        }

                        if (block.additional_information.walls) {
//                            ARRPGenerationHelper.generateWallBlockState(resourcePack, Utils.appendToPath(blockId, "_wall"));
//                            ArtificeGenerationHelper.generateWallBlockModels(resourcePack, Utils.appendToPath(blockId, "_wall"), textureAndModelInformation.textures);
//                            ARRPGenerationHelper.generateBlockItemModel(resourcePack, Utils.appendToPath(blockId, "_wall"), Utils.appendToPath(blockId, "_wall_inventory"));
                        }
                    }

                    if (block.getBlockType() != null) {
                        switch (block.getBlockType()) {
                            case OXIDIZING_BLOCK:
                                for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                    for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                        textureAndModelInformation = variantBlock.display.getModel();
                                        ARRPGenerationHelper.generateBasicBlockState(resourcePack, Utils.prependToPath(variantBlock.id, "block/"));
                                        ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                    }
                                }
                                break;
                            case LANTERN:
                                TextureAndModelInformation textureAndModelInformation2 = block.rendering.hangingModel;
                                ARRPGenerationHelper.generateLanternBlockModels(resourcePack, blockId, Utils.prependToPath(blockId, "block/"), textureAndModelInformation.textures, textureAndModelInformation2.parent, textureAndModelInformation2.textures);
                                break;
                            case DIRECTIONAL:
                                ARRPGenerationHelper.generateFacingBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"), block.rendering.model_rotation_offset);
                                if (!textureAndModelInformation.parent.getNamespace().equals("minecraft") && resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(blockId, "block/")) != null) {
                                    System.out.printf("Skipping model generation cause %s already exists%n", Utils.prependToPath(blockId, "block/"));
                                    break;
                                }
                                ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case HORIZONTAL_DIRECTIONAL:
                                ARRPGenerationHelper.generateHorizontalFacingBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"), block.rendering.model_rotation_offset);
                                if (!textureAndModelInformation.parent.getNamespace().equals("minecraft") && resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(blockId, "block/")) != null) {
                                    System.out.printf("Skipping model generation cause %s already exists%n", Utils.prependToPath(blockId, "block/"));
                                    break;
                                }
                                ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case ROTATED_PILLAR, LOG:
                                ARRPGenerationHelper.generatePillarBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
                                if (!textureAndModelInformation.parent.getNamespace().equals("minecraft") && resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(blockId, "block/")) != null) {
                                    System.out.printf("Skipping model generation cause %s already exists%n", Utils.prependToPath(blockId, "block/"));
                                    break;
                                }
                                ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            default:
                                ARRPGenerationHelper.generateBasicBlockState(resourcePack, blockId, Utils.prependToPath(blockId, "block/"));
                                if (!textureAndModelInformation.parent.getNamespace().equals("minecraft") && resourcePack.getResource(PackType.CLIENT_RESOURCES, Utils.prependToPath(blockId, "block/")) != null) {
                                    System.out.printf("Skipping model generation cause %s already exists%n", Utils.prependToPath(blockId, "block/"));
                                    break;
                                }
                                ARRPGenerationHelper.generateBlockModel(resourcePack, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                        }
                    }
//                    ARRPGenerationHelper.generateBasicItemDefinition(resourcePack, block, blockId, Utils.prependToPath(blockId, "block/"));
                }

                Identifier directModelId = block.rendering.resolveItemDefinitionModelId(blockId);
                if (block.rendering.itemModel != null && block.rendering.itemModel.isJsonObject()) {
                    TextureAndModelInformation itemInfo = block.rendering.getItemModel();
                    ARRPGenerationHelper.generateItemModel(resourcePack, blockId, itemInfo.parent, itemInfo.textures);
                }
                // Generate the powered model if powered_model was given as an object
                if ((block.information.powerable || block.information.toggleable) && block.rendering.hasPoweredModelObject()) {
                    TextureAndModelInformation poweredInfo = block.rendering.getPoweredModel();
                    ARRPGenerationHelper.generateBlockModel(resourcePack, Utils.appendToPath(blockId, "_powered"),
                            poweredInfo.parent, poweredInfo.textures);
                }

                ARRPGenerationHelper.generateBasicItemDefinition(resourcePack, block, blockId, directModelId);
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

            boolean dyable = block.additional_information != null && block.additional_information.dyable;
            dyable |= block.getBlockType() == Block.BlockType.DYEABLE;
            if (dyable) {
                net.minecraft.world.level.block.Block registeredBlock = BuiltInRegistries.BLOCK.getValue(block.information.id);
                BlockColorRegistry.register(List.of(new BlockTintSource() {
                    @Override
                    public int color(BlockState state) {
                        return block.additional_information.defaultColor;
                    }

                    @Override
                    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                        if (level == null || pos == null) return block.additional_information.defaultColor;
                        return getBlockEntityColor(block, level, pos);
                    }
                }), registeredBlock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateBlockState(Block block, RuntimeResourcePack resourcePack, Identifier blockId) {
        Identifier unpoweredModelId = Utils.prependToPath(blockId, "block/");

        // if block_model was given as a string, use it directly
        if (block.rendering.blockModel != null
                && block.rendering.blockModel.isJsonPrimitive()
                && block.rendering.blockModel.getAsJsonPrimitive().isString()) {
            unpoweredModelId = Identifier.parse(block.rendering.blockModel.getAsString());
        }

        if (block.information.powerable || block.information.toggleable) {
            Identifier poweredModelId = resolvePoweredModelId(block, blockId, unpoweredModelId);
            ARRPGenerationHelper.generatePoweredBlockState(resourcePack, blockId, unpoweredModelId, poweredModelId);
        } else {
            ARRPGenerationHelper.generateBasicBlockState(resourcePack, blockId, unpoweredModelId);
        }
    }

    private void generatePillarBlockState(Block block, RuntimeResourcePack resourcePack, Identifier blockId) {
        Identifier outModelId = Utils.prependToPath(blockId, "block/");

        // if block_model was given as a string, use it directly
        if (block.rendering.blockModel != null
                && block.rendering.blockModel.isJsonPrimitive()
                && block.rendering.blockModel.getAsJsonPrimitive().isString()) {
            outModelId = Identifier.parse(block.rendering.blockModel.getAsString());
        }

        ARRPGenerationHelper.generatePillarBlockState(resourcePack, blockId, outModelId);
    }

    private void generateHorizontalFacingBlockState(Block block, RuntimeResourcePack resourcePack, Identifier blockId) {
        Identifier unpoweredModelId = Utils.prependToPath(blockId, "block/");

        // if block_model was given as a string, use it directly
        if (block.rendering.blockModel != null
                && block.rendering.blockModel.isJsonPrimitive()
                && block.rendering.blockModel.getAsJsonPrimitive().isString()) {
            unpoweredModelId = Identifier.parse(block.rendering.blockModel.getAsString());
        }

        int yOffset = block.rendering.model_rotation_offset;
        if (block.information.powerable || block.information.toggleable) {
            Identifier poweredModelId = resolvePoweredModelId(block, blockId, unpoweredModelId);
            ARRPGenerationHelper.generatePoweredHorizontalFacingBlockState(resourcePack, blockId, unpoweredModelId, poweredModelId);
        } else {
            ARRPGenerationHelper.generateHorizontalFacingBlockState(resourcePack, blockId, unpoweredModelId, yOffset);
        }
    }

    private void generateFacingBlockState(Block block, RuntimeResourcePack resourcePack, Identifier blockId) {
        Identifier outModelId = Utils.prependToPath(blockId, "block/");

        // if block_model was given as a string, use it directly
        if (block.rendering.blockModel != null
                && block.rendering.blockModel.isJsonPrimitive()
                && block.rendering.blockModel.getAsJsonPrimitive().isString()) {
            outModelId = Identifier.parse(block.rendering.blockModel.getAsString());
        }

        ARRPGenerationHelper.generateFacingBlockState(resourcePack, blockId, outModelId, block.rendering.model_rotation_offset);
    }

    /**
     * Resolves the powered model ID for a block.
     * - String powered_model → use directly
     * - Object powered_model → will be generated at block/<id>_powered
     * - No powered_model → fall back to the unpowered model (same appearance for both states)
     */
    private Identifier resolvePoweredModelId(Block block, Identifier blockId, Identifier unpoweredModelId) {
        if (block.rendering.poweredModel == null) return unpoweredModelId;
        if (block.rendering.hasPoweredModelString()) {
            return Identifier.parse(block.rendering.poweredModel.getAsString());
        }
        if (block.rendering.hasPoweredModelObject()) {
            return Utils.appendAndPrependToPath(blockId, "block/", "_powered");
        }
        return unpoweredModelId;
    }

}

package io.github.vampirestudios.obsidian.threadhandlers.assets;

import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.artifice.api.builder.assets.ModelBuilder;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TextureAndModelInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.client.ArtificeGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.minecraft.obsidian.DyableBlockEntity;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

import java.util.Map;
import java.util.Objects;

public class BlockInitThread implements Runnable {

    private final Block block;
    private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

    public BlockInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Block blockIn) {
        block = blockIn;
        this.clientResourcePackBuilder = clientResourcePackBuilder;
    }

    @Override
    public void run() {
        try {
            net.minecraft.block.Block block1 = Registry.BLOCK.get(block.information.name.id);
            NameInformation nameInformation = block.information.name;
            Identifier blockId = nameInformation.id;
            Map<String, String> translated = nameInformation.translated;
            BlockRenderLayerMap.INSTANCE.putBlock(block1, RenderLayer.getCutout());
            if (translated != null) {
                translation(translated, blockId, "", "");
            }
            if (block.display != null) {
                if (block.display.lore.length != 0) {
                    for (TooltipInformation lore : block.display.lore) {
                        if (lore.text.textType.equals("translatable")) {
                            lore.text.translated.forEach((languageId, name) -> ClientInit.addTranslation(
                            		blockId.getNamespace(), languageId, lore.text.text, name
                            ));
                        }
                    }
                }
                if (block.display.blockState != null) {
                    if(block.block_type != null) {
                        switch (block.block_type) {
                            case FURNACE, BLAST_FURNACE, SMOKER:
                                ArtificeGenerationHelper.generateOnOffHorizontalFacingBlockState(clientResourcePackBuilder, blockId);
                                break;
                            case HORIZONTAL_FACING_BLOCK:
                                if (block.display.blockModel != null) ArtificeGenerationHelper.generateHorizontalFacingBlockState(clientResourcePackBuilder, blockId,
                                        block.display.blockModel.parent);
                                else ArtificeGenerationHelper.generateHorizontalFacingBlockState(clientResourcePackBuilder, blockId);
                                break;
                            case ROTATABLE_BLOCK:
                                if (block.display.blockModel != null) ArtificeGenerationHelper.generateFacingBlockState(clientResourcePackBuilder, blockId,
                                        block.display.blockModel.parent);
                                else ArtificeGenerationHelper.generateFacingBlockState(clientResourcePackBuilder, blockId);
                                break;
                            case PILLAR, LOG:
                                if (block.display.blockModel != null) ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId,
                                        block.display.blockModel.parent);
                                else ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId);
                                break;
                            case WOODEN_DOOR, METAL_DOOR:
                                ArtificeGenerationHelper.generateDoorBlockState(clientResourcePackBuilder, blockId);
                                break;
                            case TRAPDOOR:
                                ArtificeGenerationHelper.generateTrapdoorBlockState(clientResourcePackBuilder, blockId);
                                break;
                            case STAIRS:
                                ArtificeGenerationHelper.generateStairsBlockState(clientResourcePackBuilder, blockId);
                                break;
                            case SLAB:
                                ArtificeGenerationHelper.generateSlabBlockState(clientResourcePackBuilder, blockId, blockId);
                                break;
                            case WALL:
                                ArtificeGenerationHelper.generateWallBlockState(clientResourcePackBuilder, blockId);
                                break;
                            case FENCE:
                                ArtificeGenerationHelper.generateFenceBlockState(clientResourcePackBuilder, blockId);
                                break;
                            case PISTON:
                                ArtificeGenerationHelper.generatePistonBlockState(clientResourcePackBuilder, blockId,
                                        block.display.blockState.model, block.display.blockState.stickyModel);
                                ArtificeGenerationHelper.generatePistonModels(clientResourcePackBuilder, blockId,
                                        block.display.blockModel.parent, block.display.blockModel.textures,
                                        block.display.stickyPiston.parent, block.display.stickyPiston.textures);
                                break;
                            case OXIDIZING_BLOCK:
                                for(io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                    for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                        if (variantBlock.display.blockState.model != null) {
                                            ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, variantBlock.name.id,
                                                    variantBlock.display.blockModel.parent);
                                        } else {
                                            if (variantBlock.display.blockModel != null) ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, variantBlock.name.id,
                                                    variantBlock.display.blockModel.parent);
                                            else ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, variantBlock.name.id);
                                        }
                                    }
                                }
                                break;
                            case LANTERN:
                                if (block.display.blockState.model != null) {
                                    ArtificeGenerationHelper.generateLanternBlockState(clientResourcePackBuilder, blockId,
                                            block.display.blockState.model, block.display.blockState.hangingModel);
                                } else {
                                    ArtificeGenerationHelper.generateLanternBlockState(clientResourcePackBuilder, blockId);
                                }
                                break;
                            case LOOM, BLOCK:
                            default:
                                if (block.display.blockState.model != null) {
                                    ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, blockId,
                                            block.display.blockState.model);
                                } else {
                                    if (block.display.blockModel != null) ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, blockId,
                                            block.display.blockModel.parent);
                                    else ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, blockId);
                                }
                                break;
                        }
                    } else {
                        if (block.display.blockModel.parent != null) ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, blockId,
                                block.display.blockModel.parent);
                        else ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, blockId);
                    }
                }
                if (block.display.blockModel != null) {
                    TextureAndModelInformation textureAndModelInformation = block.display.blockModel;
                    if(block.block_type != null) {
                        switch(block.block_type) {
                            case FURNACE, BLAST_FURNACE, SMOKER:
                                TextureAndModelInformation onTextureAndModelInformation = block.display.onModel;
                                TextureAndModelInformation offTextureAndModelInformation = block.display.offModel;
                                ArtificeGenerationHelper.generateOnOffBlockModels(clientResourcePackBuilder, blockId,
                                        onTextureAndModelInformation.parent, onTextureAndModelInformation.textures,
                                        offTextureAndModelInformation.parent, offTextureAndModelInformation.textures);
                                break;
                            case WOODEN_DOOR:
                                TextureAndModelInformation topTextureAndModelInformation = block.display.doorTopModel;
                                TextureAndModelInformation bottomTextureAndModelInformation = block.display.doorBottomModel;
                                TextureAndModelInformation topHingeTextureAndModelInformation = block.display.doorTopHingeModel;
                                TextureAndModelInformation bottomHingeTextureAndModelInformation = block.display.doorBottomHingeModel;
                                ArtificeGenerationHelper.generateDoorBlockModels(clientResourcePackBuilder, blockId,
                                        topTextureAndModelInformation.parent, topTextureAndModelInformation.textures,
                                        topHingeTextureAndModelInformation.parent, topHingeTextureAndModelInformation.textures,
                                        bottomTextureAndModelInformation.parent, bottomTextureAndModelInformation.textures,
                                        bottomHingeTextureAndModelInformation.parent, bottomHingeTextureAndModelInformation.textures);
                                break;
                            case METAL_DOOR:
                                TextureAndModelInformation topTextureAndModelInformation1 = block.display.doorTopModel;
                                TextureAndModelInformation bottomTextureAndModelInformation1 = block.display.doorBottomModel;
                                TextureAndModelInformation topHingeTextureAndModelInformation1 = block.display.doorTopHingeModel;
                                TextureAndModelInformation bottomHingeTextureAndModelInformation1 = block.display.doorBottomHingeModel;
                                ArtificeGenerationHelper.generateDoorBlockModels(clientResourcePackBuilder, blockId,
                                        topTextureAndModelInformation1.parent, topTextureAndModelInformation1.textures,
                                        bottomTextureAndModelInformation1.parent, bottomTextureAndModelInformation1.textures,
                                        topHingeTextureAndModelInformation1.parent, topHingeTextureAndModelInformation1.textures,
                                        bottomHingeTextureAndModelInformation1.parent, bottomHingeTextureAndModelInformation1.textures);
                                break;
                            case TRAPDOOR:
                                topTextureAndModelInformation = block.display.trapdoorTopModel;
                                TextureAndModelInformation openTextureAndModelInformation = block.display.trapdoorOpenModel;
                                bottomTextureAndModelInformation = block.display.trapdoorBottomModel;
                                ArtificeGenerationHelper.generateTrapdoorBlockModels(clientResourcePackBuilder, blockId,
                                        topTextureAndModelInformation.parent, topTextureAndModelInformation.textures,
                                        openTextureAndModelInformation.parent, openTextureAndModelInformation.textures,
                                        bottomTextureAndModelInformation.parent, bottomTextureAndModelInformation.textures);
                                break;
                            case STAIRS:
                                ArtificeGenerationHelper.generateStairsBlockModels(clientResourcePackBuilder, blockId, textureAndModelInformation.textures);
                                break;
                            case SLAB:
                                ArtificeGenerationHelper.generateSlabBlockModels(clientResourcePackBuilder, blockId, textureAndModelInformation.textures);
                                break;
                            case WALL:
                                ArtificeGenerationHelper.generateWallBlockModels(clientResourcePackBuilder, blockId, textureAndModelInformation.textures);
                                break;
                            case FENCE:
                                ArtificeGenerationHelper.generateFenceBlockModels(clientResourcePackBuilder, blockId, textureAndModelInformation.textures);
                                break;
                            case OXIDIZING_BLOCK:
                                for(io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                    for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                        textureAndModelInformation = variantBlock.display.blockModel;
                                        ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                    }
                                }
                                break;
                            case LANTERN:
                                TextureAndModelInformation textureAndModelInformation2 = block.display.hangingModel;
                                ArtificeGenerationHelper.generateLanternBlockModels(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures, textureAndModelInformation2.parent, textureAndModelInformation2.textures);
                                break;
                            case BLOCK:
                            default:
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                        }
                    } else {
                        ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                    }
                }
                if (block.display.model != null) {
                    TextureAndModelInformation textureAndModelInformation = block.display.model;
                    if (block.additional_information != null) {
                        if (block.additional_information.slab) {
                            ArtificeGenerationHelper.generateSlabBlockState(clientResourcePackBuilder, Utils.appendToPath(blockId, "_slab"), blockId);
                            ArtificeGenerationHelper.generateSlabBlockModels(clientResourcePackBuilder, Utils.appendToPath(blockId, "_slab"), textureAndModelInformation.textures);
                            ArtificeGenerationHelper.generateBlockItemModel(clientResourcePackBuilder, Utils.appendToPath(blockId, "_slab"), Utils.appendToPath(blockId, "_slab"));
                        }

                        if (block.additional_information.stairs) {
                            ArtificeGenerationHelper.generateStairsBlockState(clientResourcePackBuilder, Utils.appendToPath(blockId, "_stairs"));
                            ArtificeGenerationHelper.generateStairsBlockModels(clientResourcePackBuilder, Utils.appendToPath(blockId, "_stairs"), textureAndModelInformation.textures);
                            ArtificeGenerationHelper.generateBlockItemModel(clientResourcePackBuilder, Utils.appendToPath(blockId, "_stairs"), Utils.appendToPath(blockId, "_stairs"));
                        }

                        if (block.additional_information.walls) {
                            ArtificeGenerationHelper.generateWallBlockState(clientResourcePackBuilder, Utils.appendToPath(blockId, "_wall"));
                            ArtificeGenerationHelper.generateWallBlockModels(clientResourcePackBuilder, Utils.appendToPath(blockId, "_wall"), textureAndModelInformation.textures);
                            ArtificeGenerationHelper.generateBlockItemModel(clientResourcePackBuilder, Utils.appendToPath(blockId, "_wall"), Utils.appendToPath(blockId, "_wall_inventory"));
                        }
                    }

                    if(block.block_type != null) {
                        switch(block.block_type) {
                            case FURNACE, BLAST_FURNACE, SMOKER:
                                ArtificeGenerationHelper.generateOnOffHorizontalFacingBlockState(clientResourcePackBuilder, blockId);
                                TextureAndModelInformation onTextureAndModelInformation = block.display.onModel;
                                TextureAndModelInformation offTextureAndModelInformation = block.display.offModel;
                                ArtificeGenerationHelper.generateOnOffBlockModels(clientResourcePackBuilder, blockId,
                                        onTextureAndModelInformation.parent, onTextureAndModelInformation.textures,
                                        offTextureAndModelInformation.parent, offTextureAndModelInformation.textures);
                                break;
                            case HORIZONTAL_FACING_BLOCK:
                                ArtificeGenerationHelper.generateHorizontalFacingBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case ROTATABLE_BLOCK:
                                ArtificeGenerationHelper.generateFacingBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case PILLAR, LOG:
                                ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case WOODEN_DOOR:
                                TextureAndModelInformation topTextureAndModelInformation = block.display.doorTopModel;
                                TextureAndModelInformation bottomTextureAndModelInformation = block.display.doorBottomModel;
                                TextureAndModelInformation topHingeTextureAndModelInformation = block.display.doorTopHingeModel;
                                TextureAndModelInformation bottomHingeTextureAndModelInformation = block.display.doorBottomHingeModel;
                                ArtificeGenerationHelper.generateDoorBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateDoorBlockModels(clientResourcePackBuilder, blockId,
                                        topTextureAndModelInformation.parent, topTextureAndModelInformation.textures,
                                        topHingeTextureAndModelInformation.parent, topHingeTextureAndModelInformation.textures,
                                        bottomTextureAndModelInformation.parent, bottomTextureAndModelInformation.textures,
                                        bottomHingeTextureAndModelInformation.parent, bottomHingeTextureAndModelInformation.textures);
                                break;
                            case METAL_DOOR:
                                TextureAndModelInformation topTextureAndModelInformation1 = block.display.doorTopModel;
                                TextureAndModelInformation bottomTextureAndModelInformation1 = block.display.doorBottomModel;
                                TextureAndModelInformation topHingeTextureAndModelInformation1 = block.display.doorTopHingeModel;
                                TextureAndModelInformation bottomHingeTextureAndModelInformation1 = block.display.doorBottomHingeModel;
                                ArtificeGenerationHelper.generateDoorBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateDoorBlockModels(clientResourcePackBuilder, blockId,
                                        topTextureAndModelInformation1.parent, topTextureAndModelInformation1.textures,
                                        bottomTextureAndModelInformation1.parent, bottomTextureAndModelInformation1.textures,
                                        topHingeTextureAndModelInformation1.parent, topHingeTextureAndModelInformation1.textures,
                                        bottomHingeTextureAndModelInformation1.parent, bottomHingeTextureAndModelInformation1.textures);
                                break;
                            case TRAPDOOR:
                                topTextureAndModelInformation = block.display.trapdoorTopModel;
                                TextureAndModelInformation openTextureAndModelInformation = block.display.trapdoorOpenModel;
                                bottomTextureAndModelInformation = block.display.trapdoorBottomModel;
                                ArtificeGenerationHelper.generateTrapdoorBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateTrapdoorBlockModels(clientResourcePackBuilder, blockId,
                                        topTextureAndModelInformation.parent, topTextureAndModelInformation.textures,
                                        openTextureAndModelInformation.parent, openTextureAndModelInformation.textures,
                                        bottomTextureAndModelInformation.parent, bottomTextureAndModelInformation.textures);
                                break;
                            case STAIRS:
                                ArtificeGenerationHelper.generateStairsBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case SLAB:
                                ArtificeGenerationHelper.generateSlabBlockState(clientResourcePackBuilder, blockId, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case WALL:
                                ArtificeGenerationHelper.generateWallBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case FENCE:
                                ArtificeGenerationHelper.generateFenceBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case OXIDIZING_BLOCK:
                                for(io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                    for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                        textureAndModelInformation = variantBlock.display.model;
                                        ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, variantBlock.name.id);
                                        ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                    }
                                }
                                break;
                            case LANTERN:
                                TextureAndModelInformation textureAndModelInformation2 = block.display.hangingModel;
                                ArtificeGenerationHelper.generateLanternBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateLanternBlockModels(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures, textureAndModelInformation2.parent, textureAndModelInformation2.textures);
                                break;
                            case BLOCK:
                            default:
                                ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                        }
                    }
                    ArtificeGenerationHelper.generateBlockItemModel(clientResourcePackBuilder, blockId);
                }
                if (block.display.itemModel != null) {
                    TextureAndModelInformation textureAndModelInformation = block.display.itemModel;
                    ModelBuilder modelBuilder = new ModelBuilder().parent(textureAndModelInformation.parent);
                    if (textureAndModelInformation.textures != null)
                        textureAndModelInformation.textures.forEach(modelBuilder::texture);
                    clientResourcePackBuilder.addItemModel(nameInformation.id, modelBuilder);
                }
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

            if(block.additional_information != null && block.additional_information.dyable) {
                net.minecraft.block.Block registeredBlock = Registry.BLOCK.get(nameInformation.id);
                ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
                        getBlockEntityColor(block, Objects.requireNonNull(world), pos), registeredBlock);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> stack.getOrCreateSubNbt("display").contains("color") ?
                        stack.getOrCreateSubNbt("display").getInt("color") : block.additional_information.defaultColor, registeredBlock.asItem());
            }
            if(block.block_type != null && block.block_type == Block.BlockType.DYEABLE) {
                net.minecraft.block.Block registeredBlock = Registry.BLOCK.get(nameInformation.id);
                ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
                        getBlockEntityColor(block, Objects.requireNonNull(world), pos), registeredBlock);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> stack.getOrCreateSubNbt("display").contains("color") ?
                        stack.getOrCreateSubNbt("display").getInt("color") : block.additional_information.defaultColor, registeredBlock.asItem());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getBlockEntityColor(Block block, BlockView view, BlockPos pos) {
        BlockEntity entity = view.getBlockEntity(pos);
        return entity != null ? ((DyableBlockEntity) entity).getDyeColor() : block.additional_information.defaultColor;
    }

    public static void translation(Map<String, String> translated, Identifier blockId, String unTranslatedType, String translatedType) {
        translated.forEach((languageId, name) -> ClientInit.addTranslation(
                blockId.getNamespace(), languageId,
                "block." + blockId.getNamespace() + "." + blockId.getPath() + unTranslatedType, name + translatedType
        ));
    }

}

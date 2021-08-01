package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.google.common.collect.ImmutableMap;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TextureAndModelInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.client.ArtificeGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ChromaEntity;
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
            BlockRenderLayerMap.INSTANCE.putBlock(block1, block.information.translucent ? RenderLayer.getTranslucent() : RenderLayer.getCutoutMipped());
            if (translated != null) {
                translation(translated, blockId, "", "");
            }
            if (block.display != null) {
                if (block.display.lore.length != 0) {
                    for (TooltipInformation lore : block.display.lore) {
                        if (lore.text.textType.equals("translatable")) {
                            lore.text.translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                                    blockId.getNamespace(), ImmutableMap.of(languageId, ImmutableMap.of(lore.text.text, name))
                            ));
                        }
                    }
                }
                if (block.display.blockState != null) {
                    if(block.block_type != null) {
                        switch (block.block_type) {
                            case LOG:
                                ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId);
                                break;
                            case OXIDIZING_BLOCK:
                                for(io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                    for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                        ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, variantBlock.name.id);
                                    }
                                }
                                break;
                            default:
                                ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, blockId);
                        }
                    }
                }
                if (block.display.blockModel != null) {
                    TextureAndModelInformation textureAndModelInformation = block.display.blockModel;
                    if(block.block_type != null) {
                        if (block.block_type == Block.BlockType.LOG) {
                            ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                        } else if (block.block_type == Block.BlockType.OXIDIZING_BLOCK) {
                            for(io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                    textureAndModelInformation = variantBlock.display.blockModel;
                                    ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                }
                            }
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
                            case HORIZONTAL_FACING_BLOCK:
                                ArtificeGenerationHelper.generateHorizontalFacingBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case ROTATABLE_BLOCK:
                                ArtificeGenerationHelper.generateFacingBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case PILLAR:
                            case LOG:
                                ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case DOOR:
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
                                ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case SLAB:
                                ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case WALL:
                                ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                break;
                            case FENCE:
                                ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId);
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
                            case EIGHT_DIRECTIONAL_BLOCK:
                                ArtificeGenerationHelper.generateEightDirectionalBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
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
                    clientResourcePackBuilder.addItemModel(nameInformation.id, modelBuilder -> {
                        modelBuilder.parent(textureAndModelInformation.parent);
                        textureAndModelInformation.textures.forEach(modelBuilder::texture);
                    });
                }
            }
            if (block.additional_information != null && translated != null) {
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
                        this.getBlockEntityColor(Objects.requireNonNull(world), pos), registeredBlock);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> stack.getOrCreateSubNbt("display").contains("color") ?
                        stack.getOrCreateSubNbt("display").getInt("color") : 16777215, registeredBlock.asItem());
            }
            if(block.block_type != null && block.block_type == Block.BlockType.DYEABLE) {
                net.minecraft.block.Block registeredBlock = Registry.BLOCK.get(nameInformation.id);
                ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
                        this.getBlockEntityColor(Objects.requireNonNull(world), pos), registeredBlock);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> stack.getOrCreateSubNbt("display").contains("color") ?
                        stack.getOrCreateSubNbt("display").getInt("color") : 16777215, registeredBlock.asItem());
            }
            clientResourcePackBuilder.add();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getBlockEntityColor(BlockView view, BlockPos pos) {
        BlockEntity entity = view.getBlockEntity(pos);
        return entity != null ? ((ChromaEntity) Objects.requireNonNull(((ChromaEntity) entity).getRenderAttachmentData())).getColor() : 16777215;
    }

    private void translation(Map<String, String> translated, Identifier blockId, String unTranslatedType, String translatedType) {
        translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                blockId.getNamespace(), ImmutableMap.of(languageId, ImmutableMap.of(String.format("block.%s.%s", blockId.getNamespace(), blockId.getPath() + unTranslatedType), name + translatedType))
        ));
    }

}

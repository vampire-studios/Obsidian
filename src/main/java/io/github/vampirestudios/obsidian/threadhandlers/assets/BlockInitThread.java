package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.google.common.collect.ImmutableMap;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.api.obsidian.BlockstateInformation;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TextureAndModelInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.client.ArtificeGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.minecraft.ModBlockColor;
import io.github.vampirestudios.obsidian.minecraft.ModItemColor;
import io.github.vampirestudios.obsidian.minecraft.obsidian.DyableBlockEntity;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.REGISTRY_HELPER;

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
                    BlockstateInformation textureAndModelInformation = block.display.blockState;
                    if (block.additional_information != null) {
                        if (block.additional_information.horizontal_rotatable) {
                            ArtificeGenerationHelper.generateHorizontalFacingBlockState(clientResourcePackBuilder, blockId);
                        } else if (block.additional_information.rotatable) {
                            ArtificeGenerationHelper.generateFacingBlockState(clientResourcePackBuilder, blockId);
                        } else if (block.additional_information.pillar) {
                            ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId);
                        } else {
                            ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, blockId);
                        }
                    } else {
                        if(block.block_type != null) {
                            if (block.block_type == Block.BlockType.LOG) {
                                ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId);
                            } else if (block.block_type == Block.BlockType.OXIDIZING_BLOCK) {
                                for(io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                    for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                        textureAndModelInformation = variantBlock.display.blockState;
                                        ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, variantBlock.name);
                                    }
                                }
                            }
                        }
                        ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, blockId);
                    }
                }
                if (block.display.blockModel != null) {
                    TextureAndModelInformation textureAndModelInformation = block.display.blockModel;
                    if (block.additional_information != null) {
                        if (block.additional_information.horizontal_rotatable) {
                            ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                        } else if (block.additional_information.rotatable) {
                            ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                        } else if (block.additional_information.pillar) {
                            ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                        } else {
                            ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                        }
                    } else {
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
                        }
                        ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                    }
                }
                if (block.display.model != null) {
                    TextureAndModelInformation textureAndModelInformation = block.display.model;
                    if (block.additional_information != null) {
                        if (block.additional_information.horizontal_rotatable) {
                            ArtificeGenerationHelper.generateHorizontalFacingBlockState(clientResourcePackBuilder, blockId);
                            ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                        } else if (block.additional_information.rotatable) {
                            ArtificeGenerationHelper.generateFacingBlockState(clientResourcePackBuilder, blockId);
                            ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                        } else if (block.additional_information.pillar) {
                            ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId);
                            ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                        } else {
                            ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, blockId);
                            ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                        }
                    } else {
                        if(block.block_type != null) {
                            if (block.block_type == Block.BlockType.LOG) {
                                ArtificeGenerationHelper.generatePillarBlockState(clientResourcePackBuilder, blockId);
                                ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                            } else if (block.block_type == Block.BlockType.OXIDIZING_BLOCK) {
                                for(io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage oxidationStage : block.oxidizable_properties.stages) {
                                    for (io.github.vampirestudios.obsidian.api.obsidian.block.Block.OxidizableProperties.OxidationStage.VariantBlock variantBlock : oxidationStage.blocks) {
                                        textureAndModelInformation = variantBlock.display.blockModel;
                                        ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, variantBlock.name);
                                        ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                                    }
                                }
                            }
                        }
                        ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, blockId);
                        ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                    }
                    ArtificeGenerationHelper.generateBlockItemModel(clientResourcePackBuilder, blockId);
                }
                if (block.display.itemModel != null) {
                    TextureAndModelInformation textureAndModelInformation = block.display.itemModel;
//                    ArtificeGenerationHelper.generateBlockItemModel(clientResourcePackBuilder, blockId);
                    clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder -> {
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
                net.minecraft.block.Block registeredBlock = Registry.BLOCK.get(block.information.name.id);
                REGISTRY_HELPER.registerBlockEntity(FabricBlockEntityTypeBuilder.create((FabricBlockEntityTypeBuilder.Factory<BlockEntity>)
                                (blockPos, blockState) -> new DyableBlockEntity(block, blockPos, blockState), registeredBlock),
                        block.information.name.id.getPath() + "_be");
                ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
                        ((ModBlockColor) state.getBlock()).getColor(state, world, pos, tintIndex), registeredBlock);
                ColorProviderRegistry.ITEM.register((stack, tintIndex) ->
                        ((ModItemColor) stack.getItem()).getColor(stack, tintIndex), registeredBlock.asItem());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void translation(Map<String, String> translated, Identifier blockId, String unTranslatedType, String translatedType) {
        translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                blockId.getNamespace(), ImmutableMap.of(languageId, ImmutableMap.of(String.format("block.%s.%s", blockId.getNamespace(), blockId.getPath() + unTranslatedType), name + translatedType))
        ));
    }

}

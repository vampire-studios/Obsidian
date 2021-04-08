package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.google.common.collect.ImmutableMap;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TextureAndModelInformation;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.client.ArtificeGenerationHelper;
import io.github.vampirestudios.obsidian.client.ClientInit;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;

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
                            }
                        }
                        ArtificeGenerationHelper.generateBasicBlockState(clientResourcePackBuilder, blockId);
                        ArtificeGenerationHelper.generateBlockModel(clientResourcePackBuilder, blockId, textureAndModelInformation.parent, textureAndModelInformation.textures);
                    }
                    ArtificeGenerationHelper.generateBlockItemModel(clientResourcePackBuilder, blockId);
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

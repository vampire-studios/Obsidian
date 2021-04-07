package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.google.common.collect.ImmutableMap;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
            BlockRenderLayerMap.INSTANCE.putBlock(block1, block.information.translucent ? RenderLayer.getTranslucent() : RenderLayer.getCutoutMipped());
            if (block.information.name.translated != null) {
                block.information.name.translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                        block.information.name.id.getNamespace(),
                        ImmutableMap.of(
                                languageId,
                                ImmutableMap.of(
                                        String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath()),
                                        name
                                )
                        )
                ));
            }
            if (block.display != null) {
                if (block.display.lore.length != 0) {
                    for (TooltipInformation lore : block.display.lore) {
                        if (lore.text.textType.equals("translatable")) {
                            lore.text.translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                                    block.information.name.id.getNamespace(),
                                    ImmutableMap.of(
                                            languageId,
                                            ImmutableMap.of(
                                                    lore.text.text,
                                                    name
                                            )
                                    )
                            ));
                        }
                    }
                }
                if (block.display.model != null) {
                    if (block.additional_information != null) {
                        if (block.additional_information.horizontal_rotatable) {
                            clientResourcePackBuilder.addBlockState(block.information.name.id, blockStateBuilder -> {
                                blockStateBuilder.variant("facing=north", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")));
                                blockStateBuilder.variant("facing=south", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")).rotationY(180));
                                blockStateBuilder.variant("facing=east", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")).rotationY(90));
                                blockStateBuilder.variant("facing=west", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")).rotationY(270));
                            });
                            clientResourcePackBuilder.addBlockModel(block.information.name.id, modelBuilder -> {
                                modelBuilder.parent(block.display.model.parent);
                                block.display.model.textures.forEach(modelBuilder::texture);
                            });
                            clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder ->
                                    modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/")));
                        } else if (block.additional_information.rotatable) {
                            clientResourcePackBuilder.addBlockState(block.information.name.id, blockStateBuilder -> {
                                blockStateBuilder.variant("facing=north", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")));
                                blockStateBuilder.variant("facing=south", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")).rotationY(180));
                                blockStateBuilder.variant("facing=east", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")).rotationY(90));
                                blockStateBuilder.variant("facing=west", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")).rotationY(270));
                            });
                            clientResourcePackBuilder.addBlockModel(block.information.name.id, modelBuilder -> {
                                modelBuilder.parent(block.display.model.parent);
                                block.display.model.textures.forEach(modelBuilder::texture);
                            });
                            clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder ->
                                    modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/")));
                        } else if (block.additional_information.pillar) {
                            clientResourcePackBuilder.addBlockState(block.information.name.id, blockStateBuilder -> {
                                blockStateBuilder.variant("axis=x", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/"))
                                        .rotationX(90).rotationY(90));
                                blockStateBuilder.variant("axis=y", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")));
                                blockStateBuilder.variant("axis=z", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/"))
                                        .rotationX(90));
                            });
                            clientResourcePackBuilder.addBlockModel(block.information.name.id, modelBuilder -> {
                                modelBuilder.parent(block.display.model.parent);
                                block.display.model.textures.forEach(modelBuilder::texture);
                            });
                            clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder ->
                                    modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/")));
                        } else {
                            if(block.block_type != null) {
                                if (block.block_type == Block.BlockType.LOG) {
                                    clientResourcePackBuilder.addBlockState(block.information.name.id, blockStateBuilder -> {
                                        blockStateBuilder.variant("axis=x", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/"))
                                                .rotationX(90).rotationY(90));
                                        blockStateBuilder.variant("axis=y", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")));
                                        blockStateBuilder.variant("axis=z", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/"))
                                                .rotationX(90));
                                    });
                                    clientResourcePackBuilder.addBlockModel(block.information.name.id, modelBuilder -> {
                                        modelBuilder.parent(block.display.model.parent);
                                        block.display.model.textures.forEach(modelBuilder::texture);
                                    });
                                    clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder ->
                                            modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/")));
                                }
                            }
                            clientResourcePackBuilder.addBlockState(block.information.name.id, blockStateBuilder ->
                                    blockStateBuilder.variant("", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/"))));
                            clientResourcePackBuilder.addBlockModel(block.information.name.id, modelBuilder -> {
                                modelBuilder.parent(block.display.model.parent);
                                block.display.model.textures.forEach(modelBuilder::texture);
                            });
                            clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder -> {
                                modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/"));
                            });
                        }
                    } else {
                        clientResourcePackBuilder.addBlockState(block.information.name.id, blockStateBuilder ->
                                blockStateBuilder.variant("", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/"))));
                        clientResourcePackBuilder.addBlockModel(block.information.name.id, modelBuilder -> {
                            modelBuilder.parent(block.display.model.parent);
                            block.display.model.textures.forEach(modelBuilder::texture);
                        });
                        clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder ->
                                modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/")));
                    }
                }
            }
            if (block.additional_information != null) {
                if (block.additional_information.stairs) {
                    if (block.information.name.translated != null) {
                        block.information.name.translated.forEach((languageId, name) ->
                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder ->
                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_stairs"),
                                                name + " Stairs")));
                    }
                }
                if (block.additional_information.fence) {
                    if (block.information.name.translated != null) {
                        block.information.name.translated.forEach((languageId, name) ->
                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder ->
                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence"),
                                                name + " Fence")));
                    }
                }
                if (block.additional_information.fenceGate) {
                    if (block.information.name.translated != null) {
                        block.information.name.translated.forEach((languageId, name) ->
                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder ->
                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence_gate"),
                                                name + " Fence Gate")));
                    }
                }
                if (block.additional_information.walls) {
                    if (block.information.name.translated != null) {
                        block.information.name.translated.forEach((languageId, name) ->
                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder ->
                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_wall"),
                                                name + " Wall")));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

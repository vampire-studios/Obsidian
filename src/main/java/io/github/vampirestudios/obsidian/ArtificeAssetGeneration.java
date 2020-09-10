package io.github.vampirestudios.obsidian;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.api.TooltipInformation;
import io.github.vampirestudios.obsidian.api.block.Block;
import io.github.vampirestudios.obsidian.api.entity.Entity;
import io.github.vampirestudios.obsidian.api.item.Item;
import io.github.vampirestudios.obsidian.api.potion.Potion;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ArtificeAssetGeneration implements ClientModInitializer {

    public static List<Item> items = new ArrayList<>();
    public static List<Block> blocks = new ArrayList<>();
    public static List<Entity> entities = new ArrayList<>();
    public static List<Potion> potions = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        Artifice.registerAssets("client_side_resources", clientResourcePackBuilder -> {
            for (Item item : items) {
                item.information.name.translated.forEach((languageId, name) -> {
                    clientResourcePackBuilder.addTranslations(new Identifier(item.information.name.id.getNamespace(), languageId), translationBuilder -> {
                        translationBuilder.entry(String.format("item.%s.%s", item.information.name.id.getNamespace(), item.information.name.id.getPath()), name);
                    });
                    System.out.println(String.format("Language ID: %s Name: %s", languageId, name));
                });
                if (item.display != null && item.display.model != null) {
                    clientResourcePackBuilder.addItemModel(item.information.name.id, modelBuilder -> {
                        modelBuilder.parent(item.display.model.parent);
                        item.display.model.textures.forEach(modelBuilder::texture);
                    });
                }
            }
            for (Block block : blocks) {
                block.information.name.translated.forEach((languageId, name) -> {
                    clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath()), name);
                    });
                });
                if (block.display != null && block.display.lore.length != 0) {
                    for (TooltipInformation lore : block.display.lore) {
                        if (lore.text.type.equals("translatable")) {
                            lore.text.translated.forEach((languageId, name) -> {
                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                    translationBuilder.entry(lore.text.text, name);
                                });
                            });
                        }
                    }
                }
                if (block.display != null && block.display.model != null) {
                    if (block.additional_information != null) {
                        if (block.additional_information.rotatable) {
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
                            clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder -> {
                                modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/"));
                            });
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
                            clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder -> {
                                modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/"));
                            });
                        } else {
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
                        clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder -> {
                            modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/"));
                        });
                    }
                }
            }
        });
    }

}

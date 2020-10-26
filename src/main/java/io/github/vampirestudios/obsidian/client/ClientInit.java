package io.github.vampirestudios.obsidian.client;

import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.util.Processor;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.TooltipInformation;
import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import io.github.vampirestudios.obsidian.minecraft.EntityImplRenderer;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.text.WordUtils;

public class ClientInit implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigHelper.ENTITIES.forEach(entity -> {
            EntityType<?> entityType = Registry.ENTITY_TYPE.get(entity.identifier);
            EntityRendererRegistry.INSTANCE.register(entityType, (entityRenderDispatcher, context) -> new EntityImplRenderer(entityRenderDispatcher, entity));
        });

        Artifice.registerAssetsNew(new Identifier(Obsidian.MOD_ID, "idk"), new Processor<ArtificeResourcePack.ClientResourcePackBuilder>() {
            @Override
            public void accept(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder) {
                ConfigHelper.ENCHANTMENTS.forEach(enchantment -> {
                        clientResourcePackBuilder.addTranslations(new Identifier(enchantment.id.getNamespace(), "en_us"), translationBuilder ->
                                translationBuilder.entry(String.format("enchantment.%s.%s", enchantment.id.getNamespace(), enchantment.id.getPath()),
                                        enchantment.name));
                });
                ConfigHelper.ENTITIES.forEach(entity -> {
                    clientResourcePackBuilder.addTranslations(new Identifier(entity.identifier.getNamespace(), "en_us"), translationBuilder ->
                            translationBuilder.entry(String.format("item.%s.%s", entity.identifier.getNamespace(), entity.identifier.getPath() + "_spawn_egg"),
                                    WordUtils.capitalizeFully(entity.identifier.getPath().replace("_", " ") + " Spawn Egg")));
                    clientResourcePackBuilder.addItemModel(new Identifier(entity.identifier.getNamespace(), entity.identifier.getPath() + "_spawn_egg"), modelBuilder -> {
                        modelBuilder.parent(new Identifier("item/template_spawn_egg"));
                    });
                });
                ConfigHelper.FOODS.forEach(food -> {
                    food.information.name.translated.forEach((languageId, name) -> {
                        clientResourcePackBuilder.addTranslations(new Identifier(food.information.name.id.getNamespace(), languageId), translationBuilder -> {
                            translationBuilder.entry(String.format("item.%s.%s", food.information.name.id.getNamespace(), food.information.name.id.getPath()), name);
                        });
                    });
                    if (food.display != null && food.display.model != null) {
                        clientResourcePackBuilder.addItemModel(food.information.name.id, modelBuilder -> {
                            modelBuilder.parent(food.display.model.parent);
                            food.display.model.textures.forEach(modelBuilder::texture);
                        });
                    }
                });
                ConfigHelper.WEAPONS.forEach(weapon -> {
                    weapon.information.name.translated.forEach((languageId, name) -> {
                        clientResourcePackBuilder.addTranslations(new Identifier(weapon.information.name.id.getNamespace(), languageId), translationBuilder -> {
                            translationBuilder.entry(String.format("item.%s.%s", weapon.information.name.id.getNamespace(), weapon.information.name.id.getPath()), name);
                        });
                    });
                    if (weapon.display != null && weapon.display.model != null) {
                        clientResourcePackBuilder.addItemModel(weapon.information.name.id, modelBuilder -> {
                            modelBuilder.parent(weapon.display.model.parent);
                            weapon.display.model.textures.forEach(modelBuilder::texture);
                        });
                    }
                });
                ConfigHelper.TOOLS.forEach(tool -> {
                    tool.information.name.translated.forEach((languageId, name) -> {
                        clientResourcePackBuilder.addTranslations(new Identifier(tool.information.name.id.getNamespace(), languageId), translationBuilder -> {
                            translationBuilder.entry(String.format("item.%s.%s", tool.information.name.id.getNamespace(), tool.information.name.id.getPath()), name);
                        });
                    });
                    if (tool.display != null && tool.display.model != null) {
                        clientResourcePackBuilder.addItemModel(tool.information.name.id, modelBuilder -> {
                            modelBuilder.parent(tool.display.model.parent);
                            tool.display.model.textures.forEach(modelBuilder::texture);
                        });
                    }
                });
                ConfigHelper.ARMORS.forEach(armor -> {
                    armor.information.name.translated.forEach((languageId, name) -> {
                        clientResourcePackBuilder.addTranslations(new Identifier(armor.information.name.id.getNamespace(), languageId), translationBuilder -> {
                            translationBuilder.entry(String.format("item.%s.%s", armor.information.name.id.getNamespace(), armor.information.name.id.getPath()), name);
                        });
                    });
                    if (armor.display != null && armor.display.model != null) {
                        clientResourcePackBuilder.addItemModel(armor.information.name.id, modelBuilder -> {
                            modelBuilder.parent(armor.display.model.parent);
                            armor.display.model.textures.forEach(modelBuilder::texture);
                        });
                    }
                });
                ConfigHelper.ITEMS.forEach(item -> {
                    System.out.println(item.information.name.id);
                    item.information.name.translated.forEach((languageId, name) -> {
                        clientResourcePackBuilder.addTranslations(new Identifier(item.information.name.id.getNamespace(), languageId), translationBuilder -> {
                            System.out.println(String.format("item.%s.%s", item.information.name.id.getNamespace(), item.information.name.id.getPath()));
                            System.out.println(name);
                            translationBuilder.entry(String.format("item.%s.%s", item.information.name.id.getNamespace(), item.information.name.id.getPath()), name);
                        });
                    });
                    if (item.display != null && item.display.model != null) {
                        clientResourcePackBuilder.addItemModel(item.information.name.id, modelBuilder -> {
                            modelBuilder.parent(item.display.model.parent);
                            item.display.model.textures.forEach(modelBuilder::texture);
                        });
                    }
                });
                ConfigHelper.BLOCKS.forEach(block -> {
                    System.out.println(block.information.name.id);
                    block.information.name.translated.forEach((languageId, name) -> {
                        clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                            System.out.println(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath()));
                            System.out.println(name);
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

                    if (block.additional_information != null) {
                        if (block.additional_information.slab) {
                            block.information.name.translated.forEach((languageId, name) -> {
                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                    translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_slab"),
                                            name + " Slab");
                                });
                            });
                        }
                        if (block.additional_information.stairs) {
                            block.information.name.translated.forEach((languageId, name) -> {
                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                    translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_stairs"),
                                            name + " Stairs");
                                });
                            });
                        }
                        if (block.additional_information.fence) {
                            block.information.name.translated.forEach((languageId, name) -> {
                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                    translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence"),
                                            name + " Fence");
                                });
                            });
                        }
                        if (block.additional_information.fenceGate) {
                            block.information.name.translated.forEach((languageId, name) -> {
                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                    translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence_gate"),
                                            name + " Fence Gate");
                                });
                            });
                        }
                        if (block.additional_information.walls) {
                            block.information.name.translated.forEach((languageId, name) -> {
                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                    translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_wall"),
                                            name + " Wall");
                                });
                            });
                        }
                    }
                });
                ConfigHelper.ITEM_GROUPS.forEach(itemGroup -> {
                    itemGroup.name.translated.forEach((languageId, name) -> {
                        clientResourcePackBuilder.addTranslations(new Identifier(itemGroup.name.id.getNamespace(), languageId), translationBuilder -> {
                            System.out.println(String.format("itemGroup.%s.%s", itemGroup.name.id.getNamespace(), itemGroup.name.id.getPath()));
                            System.out.println(name);
                            translationBuilder.entry(String.format("itemGroup.%s.%s", itemGroup.name.id.getNamespace(), itemGroup.name.id.getPath()), name);
                        });
                    });
                });
            }
        });
    }

}

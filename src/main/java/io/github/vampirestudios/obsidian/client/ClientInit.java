package io.github.vampirestudios.obsidian.client;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import io.github.vampirestudios.obsidian.minecraft.obsidian.EntityImpl;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;

public class ClientInit implements ClientModInitializer {

    public static ClientInit INSTANCE;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        for (Entity entity : ConfigHelper.ENTITIES) {
            EntityType<EntityImpl> entityType = (EntityType<EntityImpl>) Registry.ENTITY_TYPE.get(entity.information.identifier);
            if (entity.information.custom_model) {
                EntityRendererRegistry.INSTANCE.register(entityType, ctx -> new JsonEntityRenderer(ctx, entity));
            } else {
                EntityRendererRegistry.INSTANCE.register(entityType, ctx -> new CustomEntityRenderer(ctx, entity));
            }
        }
        for (ItemGroup itemGroup : ConfigHelper.ITEM_GROUPS) {
            Artifice.registerAssetPack(String.format("%s:%s_item_group_assets", itemGroup.name.id.getNamespace(), itemGroup.name.id.getPath()), clientResourcePackBuilder -> {
                itemGroup.name.translated.forEach((languageId, name) ->
                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                translationBuilder.entry(String.format("itemGroup.%s.%s", itemGroup.name.id.getNamespace(), itemGroup.name.id.getPath()), name)));
                new Thread(()-> {
                    try {
                        if(FabricLoader.getInstance().isDevelopmentEnvironment()) clientResourcePackBuilder.dumpResources("testing", "assets");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        }
        ConfigHelper.BLOCKS.forEach(block -> {
            net.minecraft.block.Block block1 = Registry.BLOCK.get(block.information.name.id);
            BlockRenderLayerMap.INSTANCE.putBlock(block1, block.information.translucent ? RenderLayer.getTranslucent() : RenderLayer.getCutoutMipped());
            Artifice.registerAssetPack(String.format("%s:%s_assets", block.information.name.id.getNamespace(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                new Thread(() -> {
                    if (block.information.name.translated != null) {
                        block.information.name.translated.forEach((languageId, name) -> {
                            String blockName;
                            if (name.contains("_")) {
                                blockName = WordUtils.capitalizeFully(name.replace("_", " "));
                            } else {
                                blockName = name;
                            }
                            clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder ->
                                    translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath()), blockName));
                        });
                    }
                    if (block.display != null) {
                        if (block.display.lore.length != 0) {
                            for (TooltipInformation lore : block.display.lore) {
                                if (lore.text.textType.equals("translatable")) {
                                    lore.text.translated.forEach((languageId, name) ->
                                            clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                                    translationBuilder.entry(lore.text.text, name)));
                                }
                            }
                        }
                        if (block.display.model != null) {
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
                    try {
                        if (FabricLoader.getInstance().isDevelopmentEnvironment())
                            clientResourcePackBuilder.dumpResources("testing", "assets");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
            if (block.additional_information != null) {
                if (block.additional_information.stairs) {
                    Artifice.registerAssetPack(String.format("%s:%s_stairs_assets", block.information.name.id.getNamespace(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                        new Thread(() -> {
                            if (block.information.name.translated != null) {
                                block.information.name.translated.forEach((languageId, name) ->
                                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                                translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_stairs"),
                                                        name + " Stairs")));
                            }
                            try {
                                if (FabricLoader.getInstance().isDevelopmentEnvironment())
                                    clientResourcePackBuilder.dumpResources("testing", "assets");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    });
                }
                if (block.additional_information.fence) {
                    Artifice.registerAssetPack(String.format("%s:%s_fence_assets", block.information.name.id.getNamespace(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                        new Thread(() -> {
                            if (block.information.name.translated != null) {
                                block.information.name.translated.forEach((languageId, name) ->
                                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                                translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence"),
                                                        name + " Fence")));
                            }
                            try {
                                if (FabricLoader.getInstance().isDevelopmentEnvironment())
                                    clientResourcePackBuilder.dumpResources("testing", "assets");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    });
                }
                if (block.additional_information.fenceGate) {
                    Artifice.registerAssetPack(String.format("%s:%s_fence_gate_assets", block.information.name.id.getNamespace(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                        new Thread(() -> {
                            if (block.information.name.translated != null) {
                                block.information.name.translated.forEach((languageId, name) ->
                                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                                translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence_gate"),
                                                        name + " Fence Gate")));
                            }
                            try {
                                if (FabricLoader.getInstance().isDevelopmentEnvironment())
                                    clientResourcePackBuilder.dumpResources("testing", "assets");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    });
                }
                if (block.additional_information.walls) {
                    Artifice.registerAssetPack(String.format("%s:%s_wall_assets", block.information.name.id.getNamespace(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                        new Thread(() -> {
                            if (block.information.name.translated != null) {
                                block.information.name.translated.forEach((languageId, name) ->
                                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                                translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_wall"),
                                                        name + " Wall")));
                            }
                            try {
                                if (FabricLoader.getInstance().isDevelopmentEnvironment())
                                    clientResourcePackBuilder.dumpResources("testing", "assets");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    });
                }
            }
        });
        new Thread(() -> ConfigHelper.ITEMS.forEach(item -> {
            Identifier identifier = item.information.name.id;
            Artifice.registerAssetPack(String.format("%s:%s_item_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
                item.information.name.translated.forEach((languageId, name) ->
                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                translationBuilder.entry(String.format("item.%s.%s", item.information.name.id.getNamespace(), item.information.name.id.getPath()), name)));
                if (item.display != null && item.display.model != null) {
                    clientResourcePackBuilder.addItemModel(item.information.name.id, modelBuilder -> {
                        modelBuilder.parent(item.display.model.parent);
                        item.display.model.textures.forEach(modelBuilder::texture);
                    });
                }
                if (item.display != null && item.display.lore.length != 0) {
                    for (TooltipInformation lore : item.display.lore) {
                        if (lore.text.textType.equals("translatable")) {
                            lore.text.translated.forEach((languageId, name) ->
                                    clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                            translationBuilder.entry(lore.text.text, name)));
                        }
                    }
                }
                new Thread(() -> {
                    try {
                        if (FabricLoader.getInstance().isDevelopmentEnvironment())
                            clientResourcePackBuilder.dumpResources("testing", "assets");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        })).start();
        ConfigHelper.ARMORS.forEach(armor -> {
            Identifier identifier = armor.information.name.id;
            Artifice.registerAssetPack(String.format("%s:%s_armor_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
                armor.information.name.translated.forEach((languageId, name) ->
                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                translationBuilder.entry(String.format("item.%s.%s", armor.information.name.id.getNamespace(), armor.information.name.id.getPath()), name)));
                if (armor.display != null && armor.display.model != null) {
                    clientResourcePackBuilder.addItemModel(armor.information.name.id, modelBuilder -> {
                        modelBuilder.parent(armor.display.model.parent);
                        armor.display.model.textures.forEach(modelBuilder::texture);
                    });
                }
                if (armor.display != null && armor.display.lore.length != 0) {
                    for (TooltipInformation lore : armor.display.lore) {
                        if (lore.text.textType.equals("translatable")) {
                            lore.text.translated.forEach((languageId, name) ->
                                    clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                            translationBuilder.entry(lore.text.text, name)));
                        }
                    }
                }
                new Thread(()-> {
                    try {
                        if(FabricLoader.getInstance().isDevelopmentEnvironment()) clientResourcePackBuilder.dumpResources("testing", "assets");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        });
        ConfigHelper.WEAPONS.forEach(weapon -> {
            Identifier identifier = weapon.information.name.id;
            Artifice.registerAssetPack(String.format("%s:%s_weapon_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
                weapon.information.name.translated.forEach((languageId, name) ->
                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                translationBuilder.entry(String.format("item.%s.%s", weapon.information.name.id.getNamespace(), weapon.information.name.id.getPath()), name)));
                if (weapon.display != null && weapon.display.model != null) {
                    clientResourcePackBuilder.addItemModel(weapon.information.name.id, modelBuilder -> {
                        modelBuilder.parent(weapon.display.model.parent);
                        weapon.display.model.textures.forEach(modelBuilder::texture);
                    });
                }
                if (weapon.display != null && weapon.display.lore.length != 0) {
                    for (TooltipInformation lore : weapon.display.lore) {
                        if (lore.text.textType.equals("translatable")) {
                            lore.text.translated.forEach((languageId, name) ->
                                    clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                            translationBuilder.entry(lore.text.text, name)));
                        }
                    }
                }
                new Thread(()-> {
                    try {
                        if(FabricLoader.getInstance().isDevelopmentEnvironment()) clientResourcePackBuilder.dumpResources("testing", "assets");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        });
        ConfigHelper.TOOLS.forEach(tool -> {
            Identifier identifier = tool.information.name.id;
            Artifice.registerAssetPack(String.format("%s:%s_armor_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
                tool.information.name.translated.forEach((languageId, name) ->
                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                translationBuilder.entry(String.format("item.%s.%s", tool.information.name.id.getNamespace(), tool.information.name.id.getPath()), name)));
                if (tool.display != null && tool.display.model!= null) {
                    clientResourcePackBuilder.addItemModel(tool.information.name.id, modelBuilder -> {
                        modelBuilder.parent(tool.display.model.parent);
                        tool.display.model.textures.forEach(modelBuilder::texture);
                    });
                }
                if (tool.display != null && tool.display.lore.length != 0) {
                    for (TooltipInformation lore : tool.display.lore) {
                        if (lore.text.textType.equals("translatable")) {
                            lore.text.translated.forEach((languageId, name) ->
                                    clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                            translationBuilder.entry(lore.text.text, name)));
                        }
                    }
                }
                new Thread(()-> {
                    try {
                        if(FabricLoader.getInstance().isDevelopmentEnvironment()) clientResourcePackBuilder.dumpResources("testing", "assets");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        });
        ConfigHelper.FOODS.forEach(foodItem -> {
            Identifier identifier = foodItem.information.name.id;
            Artifice.registerAssetPack(String.format("%s:%s_food_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
                foodItem.information.name.translated.forEach((languageId, name) ->
                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                translationBuilder.entry(String.format("item.%s.%s", foodItem.information.name.id.getNamespace(), foodItem.information.name.id.getPath()), name)));
                if (foodItem.display != null && foodItem.display.model != null) {
                    clientResourcePackBuilder.addItemModel(foodItem.information.name.id, modelBuilder -> {
                        modelBuilder.parent(foodItem.display.model.parent);
                        foodItem.display.model.textures.forEach(modelBuilder::texture);
                    });
                }
                if (foodItem.display != null && foodItem.display.lore.length != 0) {
                    for (TooltipInformation lore : foodItem.display.lore) {
                        if (lore.text.textType.equals("translatable")) {
                            lore.text.translated.forEach((languageId, name) ->
                                    clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                            translationBuilder.entry(lore.text.text, name)));
                        }
                    }
                }
                new Thread(()-> {
                    try {
                        if(FabricLoader.getInstance().isDevelopmentEnvironment()) clientResourcePackBuilder.dumpResources("testing", "assets");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        });
        ConfigHelper.ENCHANTMENTS.forEach(enchantment -> {
            Identifier identifier = enchantment.name.id;
            Artifice.registerAssetPack(String.format("%s:%s_armor_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
                enchantment.name.translated.forEach((languageId, name) ->
                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                translationBuilder.entry(String.format("enchantment.%s.%s", enchantment.name.id.getNamespace(), enchantment.name.id.getPath()), name)));
                new Thread(()-> {
                    try {
                        if(FabricLoader.getInstance().isDevelopmentEnvironment()) clientResourcePackBuilder.dumpResources("testing", "assets");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        });
        ConfigHelper.SHIELDS.forEach(shield -> {
            Identifier identifier = shield.information.name.id;
            Artifice.registerAssetPack(String.format("%s:%s_shield_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
                shield.information.name.translated.forEach((languageId, name) ->
                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                translationBuilder.entry(String.format("enchantment.%s.%s", shield.information.name.id.getNamespace(), shield.information.name.id.getPath()), name)));
                if (shield.display != null && shield.display.lore.length != 0) {
                    for (TooltipInformation lore : shield.display.lore) {
                        if (lore.text.textType.equals("translatable")) {
                            lore.text.translated.forEach((languageId, name) ->
                                    clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                            translationBuilder.entry(lore.text.text, name)));
                        }
                    }
                }
                new Thread(()-> {
                    try {
                        if(FabricLoader.getInstance().isDevelopmentEnvironment()) clientResourcePackBuilder.dumpResources("testing", "assets");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        });
        ConfigHelper.ENTITIES.forEach(entity -> {
            Identifier identifier = entity.information.identifier;
            Artifice.registerAssetPack(String.format("%s:%s_entity_assets", entity.information.identifier.getNamespace(), entity.information.identifier.getPath()), clientResourcePackBuilder -> {
                clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, "en_us"), translationBuilder ->
                        translationBuilder.entry(String.format("entity.%s.%s", identifier.getNamespace(), identifier.getPath()),
                                entity.information.name));
                clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, "en_us"), translationBuilder ->
                        translationBuilder.entry(String.format("item.%s.%s_spawn_egg", identifier.getNamespace(), identifier.getPath()),
                                entity.information.name + " Spawn Egg"));
                clientResourcePackBuilder.addItemModel(new Identifier(entity.information.identifier.getNamespace(), entity.information.identifier.getPath() + "_spawn_egg"), modelBuilder ->
                        modelBuilder.parent(new Identifier("item/template_spawn_egg")));
                new Thread(()-> {
                    try {
                        if(FabricLoader.getInstance().isDevelopmentEnvironment()) clientResourcePackBuilder.dumpResources("testing", "assets");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
        });
        ConfigHelper.ELYTRAS.forEach(elytra -> {
            Identifier identifier = elytra.information.name.id;
            Artifice.registerAssetPack(String.format("%s:%s_elytra_assets", identifier.getNamespace(), identifier.getPath()), clientResourcePackBuilder -> {
                elytra.information.name.translated.forEach((languageId, name) ->
                        clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                translationBuilder.entry(String.format("item.%s.%s", elytra.information.name.id.getNamespace(), elytra.information.name.id.getPath()), name)));
                if (elytra.display != null && elytra.display.model != null) {
                    clientResourcePackBuilder.addItemModel(elytra.information.name.id, modelBuilder -> {
                        modelBuilder.parent(elytra.display.model.parent);
                        elytra.display.model.textures.forEach(modelBuilder::texture);
                    });
                }
                if (elytra.display != null && elytra.display.lore.length != 0) {
                    for (TooltipInformation lore : elytra.display.lore) {
                        if (lore.text.textType.equals("translatable")) {
                            lore.text.translated.forEach((languageId, name) ->
                                    clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                            translationBuilder.entry(lore.text.text, name)));
                        }
                    }
                }
                new Thread(()-> {
                    try {
                        if(FabricLoader.getInstance().isDevelopmentEnvironment()) clientResourcePackBuilder.dumpResources("testing", "assets");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
            LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, livingEntityRenderer, registrationHelper, context) -> {
                registrationHelper.register(new CustomElytraFeatureRenderer<>(Registry.ITEM.get(identifier), elytra, livingEntityRenderer, context.getModelLoader()));
            });
        });
    }

}

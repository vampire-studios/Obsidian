package io.github.vampirestudios.obsidian.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.*;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.configPack.ConfigHelper;
import io.github.vampirestudios.obsidian.minecraft.EntityImpl;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;

public class ClientInit implements ClientModInitializer {

    public static ClientInit INSTANCE;

    public GeometryManager geometryManager;

    public static Gson GSON_CLIENT = new GsonBuilder()
            .registerTypeAdapter(GeometryData.class, new GeometryData.Deserializer())
            .registerTypeAdapter(GeometryBone.class, new GeometryBone.Deserializer())
            .registerTypeAdapter(GeometryCuboid.class, new GeometryCuboid.Deserializer())
            .create();

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        geometryManager = new GeometryManager(MinecraftClient.getInstance().getResourceManager());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(geometryManager);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(AnimationRegistry.INSTANCE);
        ConfigHelper.ENTITIES.forEach(entity -> {
            EntityType<EntityImpl> entityType = (EntityType<EntityImpl>) Registry.ENTITY_TYPE.get(entity.identifier);
            if (entity.custom_model) {
                EntityRendererRegistry.INSTANCE.register(entityType, ctx -> new JsonEntityRenderer(ctx, entity));
            } else {
                EntityRendererRegistry.INSTANCE.register(entityType, ctx -> new CustomEntityRenderer(ctx, entity));
            }
        });
        ConfigHelper.BLOCKS.forEach(block -> {
            if (block.information.translucent) {
                Block block1 = Registry.BLOCK.get(block.information.name.id);
                BlockRenderLayerMap.INSTANCE.putBlock(block1, RenderLayer.getTranslucent());
            }
        });

        ConfigHelper.ITEM_GROUPS.forEach(itemGroup -> {
            Artifice.registerAssetPack(String.format("obsidian:%s_item_assets", itemGroup.name.id.getPath()), clientResourcePackBuilder -> {
                itemGroup.name.translated.forEach((languageId, name) -> {
                    clientResourcePackBuilder.addTranslations(new Identifier(itemGroup.name.id.getNamespace(), languageId), translationBuilder -> {
                        translationBuilder.entry(String.format("itemGroup.%s.%s", itemGroup.name.id.getNamespace(), itemGroup.name.id.getPath()), name);
                    });
                });
                try {
                    clientResourcePackBuilder.dumpResources("testing", "assets");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        ConfigHelper.BLOCKS.forEach(block -> {
            Artifice.registerAssetPack(String.format("obsidian:%s_%s_assets", block.information.name.id.getPath(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
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
                try {
                    clientResourcePackBuilder.dumpResources("testing", "assets");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            if (block.additional_information != null) {
                if (block.additional_information.stairs) {
                    Artifice.registerAssetPack(String.format("obsidian:%s_%s_stairs_assets", block.information.name.id.getPath(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                        block.information.name.translated.forEach((languageId, name) -> {
                            clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_stairs"),
                                        name + " Stairs");
                            });
                        });
                        try {
                            clientResourcePackBuilder.dumpResources("testing", "assets");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
                if (block.additional_information.fence) {
                    Artifice.registerAssetPack(String.format("obsidian:%s_%s_fence_assets", block.information.name.id.getPath(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                        block.information.name.translated.forEach((languageId, name) -> {
                            clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence"),
                                        name + " Fence");
                            });
                        });
                        try {
                            clientResourcePackBuilder.dumpResources("testing", "assets");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
                if (block.additional_information.fenceGate) {
                    Artifice.registerAssetPack(String.format("obsidian:%s_%s_fence_gate_assets", block.information.name.id.getPath(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                        block.information.name.translated.forEach((languageId, name) -> {
                            clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence_gate"),
                                        name + " Fence Gate");
                            });
                        });
                        try {
                            clientResourcePackBuilder.dumpResources("testing", "assets");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
                if (block.additional_information.walls) {
                    Artifice.registerAssetPack(String.format("obsidian:%s_%s_wall_assets", block.information.name.id.getPath(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                        block.information.name.translated.forEach((languageId, name) -> {
                            clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_wall"),
                                        name + " Wall");
                            });
                        });
                        try {
                            clientResourcePackBuilder.dumpResources("testing", "assets");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
        ConfigHelper.ITEMS.forEach(item -> {
            Artifice.registerAssetPack(String.format("obsidian:%s_item_assets", item.information.name.id.getPath()), clientResourcePackBuilder -> {
                item.information.name.translated.forEach((languageId, name) -> {
                    clientResourcePackBuilder.addTranslations(new Identifier(item.information.name.id.getNamespace(), languageId), translationBuilder -> {
                        translationBuilder.entry(String.format("item.%s.%s", item.information.name.id.getNamespace(), item.information.name.id.getPath()), name);
                    });
                });
                if (item.display != null && item.display.model != null) {
                    clientResourcePackBuilder.addItemModel(item.information.name.id, modelBuilder -> {
                        modelBuilder.parent(item.display.model.parent);
                        item.display.model.textures.forEach(modelBuilder::texture);
                    });
                }
                try {
                    clientResourcePackBuilder.dumpResources("testing", "assets");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        ConfigHelper.ARMORS.forEach(armor -> {
            Artifice.registerAssetPack(String.format("obsidian:%s_armor_assets", armor.information.name.id.getPath()), clientResourcePackBuilder -> {
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
                try {
                    clientResourcePackBuilder.dumpResources("testing", "assets");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        ConfigHelper.WEAPONS.forEach(weapon -> {
            Artifice.registerAssetPack(String.format("obsidian:%s_weapon_assets", weapon.information.name.id.getPath()), clientResourcePackBuilder -> {
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
                try {
                    clientResourcePackBuilder.dumpResources("testing", "assets");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        ConfigHelper.TOOLS.forEach(tool -> {
            Artifice.registerAssetPack(String.format("obsidian:%s_tool_assets", tool.information.name.id.getPath()), clientResourcePackBuilder -> {
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
                try {
                    clientResourcePackBuilder.dumpResources("testing", "assets");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        ConfigHelper.FOODS.forEach(foodItem -> {
            Artifice.registerAssetPack(String.format("obsidian:%s_food_assets", foodItem.information.name.id.getPath()), clientResourcePackBuilder -> {
                foodItem.information.name.translated.forEach((languageId, name) -> {
                    clientResourcePackBuilder.addTranslations(new Identifier(foodItem.information.name.id.getNamespace(), languageId), translationBuilder -> {
                        translationBuilder.entry(String.format("item.%s.%s", foodItem.information.name.id.getNamespace(), foodItem.information.name.id.getPath()), name);
                    });
                });
                if (foodItem.display != null && foodItem.display.model != null) {
                    clientResourcePackBuilder.addItemModel(foodItem.information.name.id, modelBuilder -> {
                        modelBuilder.parent(foodItem.display.model.parent);
                        foodItem.display.model.textures.forEach(modelBuilder::texture);
                    });
                }
                try {
                    clientResourcePackBuilder.dumpResources("testing", "assets");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        ConfigHelper.ENCHANTMENTS.forEach(enchantment -> {
            Artifice.registerAssetPack(String.format("obsidian:%s_enchantment_assets", enchantment.id.getPath()), clientResourcePackBuilder -> {
                clientResourcePackBuilder.addTranslations(new Identifier(enchantment.id.getNamespace(), "en_us"), translationBuilder ->
                        translationBuilder.entry(String.format("enchantment.%s.%s", enchantment.id.getNamespace(), enchantment.id.getPath()),
                                enchantment.name));
                try {
                    clientResourcePackBuilder.dumpResources("testing", "assets");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        ConfigHelper.ENTITIES.forEach(entity -> {
            Artifice.registerAssetPack(String.format("obsidian:%s_entity_assets", entity.identifier.getPath()), clientResourcePackBuilder -> {
                clientResourcePackBuilder.addTranslations(new Identifier(entity.identifier.getNamespace(), "en_us"), translationBuilder ->
                        translationBuilder.entry(String.format("item.%s.%s", entity.identifier.getNamespace(), entity.identifier.getPath() + "_spawn_egg"),
                                WordUtils.capitalizeFully(entity.identifier.getPath().replace("_", " ") + " Spawn Egg")));
                clientResourcePackBuilder.addItemModel(new Identifier(entity.identifier.getNamespace(), entity.identifier.getPath() + "_spawn_egg"), modelBuilder -> {
                    modelBuilder.parent(new Identifier("item/template_spawn_egg"));
                });
                try {
                    clientResourcePackBuilder.dumpResources("testing", "assets");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

}

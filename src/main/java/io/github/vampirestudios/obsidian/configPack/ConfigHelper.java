package io.github.vampirestudios.obsidian.configPack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.BiomeUtils;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.PlayerJoinCallback;
import io.github.vampirestudios.obsidian.api.IAddonPack;
import io.github.vampirestudios.obsidian.api.block.Block;
import io.github.vampirestudios.obsidian.api.command.Command;
import io.github.vampirestudios.obsidian.api.currency.Currency;
import io.github.vampirestudios.obsidian.api.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.entity.Entity;
import io.github.vampirestudios.obsidian.api.item.FoodItem;
import io.github.vampirestudios.obsidian.api.item.WeaponItem;
import io.github.vampirestudios.obsidian.api.potion.Potion;
import io.github.vampirestudios.obsidian.minecraft.*;
import io.github.vampirestudios.obsidian.utils.*;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ConfigHelper {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Hidden_Gems"));
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, (SimpleStringDeserializer<?>) Identifier::new)
            .setPrettyPrinting().create();
    private static final File MATERIALS_DIRECTORY = new File(FabricLoader.getInstance().getGameDirectory(), "addon_packs");
    public static final List<IAddonPack> ADDON_PACKS = new ArrayList<>();

    public static List<io.github.vampirestudios.obsidian.api.item.Item> items = new ArrayList<>();
    public static List<WeaponItem> weapons = new ArrayList<>();
    public static List<io.github.vampirestudios.obsidian.api.item.ToolItem> tools = new ArrayList<>();
    public static List<Block> blocks = new ArrayList<>();
    private static List<Potion> potions = new ArrayList<>();
    private static final List<Command> commands = new ArrayList<>();
    private static final List<Enchantment> enchantments = new ArrayList<>();

    public static void loadDefault() {
        if (!MATERIALS_DIRECTORY.exists())
            fillDefaultConfigs();
    }

    public static void loadConfig() {
        try {
            FabricLoader.getInstance().getEntrypoints("obsidian:addon_packs", IAddonPack.class).forEach(supplier -> {
                try {
                    ADDON_PACKS.add(supplier);
                    Obsidian.LOGGER.info(String.format("[Obsidian] Registering addon: %s from an entrypoint",
                            supplier.getConfigPackInfo().getInformation().getId()));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
            for (File file : Objects.requireNonNull(MATERIALS_DIRECTORY.listFiles())) {
                // Load Packs
                if (file.isDirectory()) {
                    try {
                        File packInfoFile = new File(file, "addon.info.pack");
                        if (packInfoFile.exists()) {
                            ConfigPackInfo packInfo = GSON.fromJson(new FileReader(packInfoFile), ConfigPackInfo.class);
                            ConfigPack configPack = new ConfigPack(packInfo, file);
                            ADDON_PACKS.add(configPack);
                            Obsidian.LOGGER.info(String.format("[Obsidian] Registering addon: %s", configPack.getConfigPackInfo().getInformation().getId()));
                        }
                    } catch (Exception e) {
                        Obsidian.LOGGER.error("[Obsidian] Failed to load addon pack.", e);
                    }
                } else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".zip")) {
                    try (ZipFile zipFile = new ZipFile(file)) {
                        ZipEntry packInfoEntry = zipFile.getEntry("addon.info.pack");
                        if (packInfoEntry != null) {
                            ConfigPackInfo packInfo = GSON.fromJson(new InputStreamReader(zipFile.getInputStream(packInfoEntry)), ConfigPackInfo.class);
                            ConfigPack configPack = new ConfigPack(packInfo);
                            ADDON_PACKS.add(configPack);
                            Obsidian.LOGGER.info(String.format("[Obsidian] Registering addon: %s", configPack.getConfigPackInfo().getInformation().getId()));
                        }
                    } catch (Exception e) {
                        Obsidian.LOGGER.error("[Obsidian] Failed to load addon pack.", e);
                    }
                }
            }

            String moduleText;
            if (ADDON_PACKS.size() > 1) {
                moduleText = "Loading %d packs:";
            } else {
                moduleText = "Loading %d pack:";
            }

            Obsidian.LOGGER.info(String.format("[Obsidian] " + moduleText, ADDON_PACKS.size()));

            for(IAddonPack pack : ADDON_PACKS) {
                Obsidian.LOGGER.info(String.format(" - %s", pack.getIdentifier().toString()));

                String modId = pack.getConfigPackInfo().getInformation().namespace;

                try {
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "blocks").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "blocks").toFile().listFiles())) {
                            if (file.isFile()) {
                                Block block = GSON.fromJson(new FileReader(file), Block.class);
                                try {
                                    FabricBlockSettings blockSettings = FabricBlockSettings.of(block.information.getMaterial()).sounds(block.information.getBlockSoundGroup())
                                            .strength(block.information.destroy_time, block.information.explosion_resistance).lightLevel(block.information.luminance)
                                            .drops(block.information.drop).collidable(block.information.collidable).slipperiness(block.information.slipperiness);
                                    if (block.information.dynamicBounds) {
                                        blockSettings.dynamicBounds();
                                    }
                                    if (block.information.randomTicks) {
                                        blockSettings.ticksRandomly();
                                    }
                                    net.minecraft.block.Block blockImpl = null;
                                    if(block.additional_information != null) {
                                        if(block.additional_information.rotatable) {
                                            blockImpl = RegistryUtils.register(new HorizontalFacingBlockImpl(block, blockSettings.build()), block.information.name.id,
                                                    block.information.getItem_group());
                                        }
                                        if(block.additional_information.pillar) {
                                            blockImpl = RegistryUtils.register(new PillarBlockImpl(block, blockSettings.build()), block.information.name.id,
                                                    block.information.getItem_group());
                                        }
                                        if(!block.additional_information.pillar && !block.additional_information.rotatable) {
                                            blockImpl = RegistryUtils.register(new BlockImpl(block, blockSettings.build()), block.information.name.id,
                                                    block.information.getItem_group());
                                        }
                                    } else {
                                        blockImpl = RegistryUtils.register(new BlockImpl(block, blockSettings.build()), block.information.name.id,
                                                block.information.getItem_group());
                                    }
                                    net.minecraft.block.Block finalBlockImpl = blockImpl;
                                    for(net.minecraft.block.Block block1 : BlockTags.getTagGroup().getTagOrEmpty(block.ore_information.target).values()) {
                                        System.out.println(Registry.BLOCK.getId(block1).toString());
                                    }
                                    BuiltinRegistries.BIOME.forEach(biome -> {
                                        if (block.ore_information.biomes != null) {
                                            for (String biome2 : block.ore_information.biomes) {
                                                if (BuiltinRegistries.BIOME.getId(biome).toString().equals(biome2)) {
                                                    BiomeUtils.addFeatureToBiome(biome, GenerationStep.Feature.UNDERGROUND_ORES,
                                                            Feature.ORE.configure(
                                                                    new OreFeatureConfig(
                                                                            new TagMatchRuleTest(
                                                                                    BlockTags.getTagGroup().getTagOrEmpty(block.ore_information.target)
                                                                            ),
                                                                            finalBlockImpl.getDefaultState(),
                                                                            block.ore_information.size
                                                                    )
                                                            ).decorate(
                                                                    Decorator.RANGE.configure(
                                                                            new RangeDecoratorConfig(
                                                                                    block.ore_information.config.bottom_offset,
                                                                                    block.ore_information.config.top_offset,
                                                                                    block.ore_information.config.maximum
                                                                            )
                                                                    )
                                                            ).method_30377(block.ore_information.config.maximum).spreadHorizontally().repeat(20));
                                                }
                                            }
                                        } else {
                                            BiomeUtils.addFeatureToBiome(biome, GenerationStep.Feature.UNDERGROUND_ORES,
                                                    Feature.ORE.configure(
                                                            new OreFeatureConfig(
                                                                    new TagMatchRuleTest(
                                                                            BlockTags.BASE_STONE_OVERWORLD
                                                                    ),
                                                                    finalBlockImpl.getDefaultState(),
                                                                    block.ore_information.size
                                                            )
                                                    ).method_30377(block.ore_information.config.maximum).spreadHorizontally().repeat(20)
                                            );
                                        }
                                    });
                                    Artifice.registerAssets(String.format("obsidian:%s_block_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                        block.information.name.translated.forEach((languageId, name) -> {
                                            System.out.println(languageId);
                                            clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                                translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath()), name);
                                            });
                                        });
                                        if (block.display != null && block.display.model != null) {
                                            if(block.additional_information.rotatable) {
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
                                            } else if(block.additional_information.pillar) {
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
                                        }
                                    }).dumpResources("test");
                                    System.out.println(String.format("Registered a block called %s", block.information.name.id));
                                    Artifice.registerData(String.format("hidden_gems:%s_block_data", pack.getIdentifier().getPath()), serverResourcePackBuilder ->
                                            serverResourcePackBuilder.addLootTable(block.information.name.id, lootTableBuilder -> {
                                                lootTableBuilder.type(new Identifier("block"));
                                                lootTableBuilder.pool(pool -> {
                                                    pool.rolls(1);
                                                    pool.entry(entry -> {
                                                        entry.type(new Identifier("item"));
                                                        entry.name(block.information.name.id);
                                                    });
                                                    pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {

                                                    });
                                                });
                                            })
                                    ).dumpResources("test");

                                    blocks.add(block);
                                    if (block.additional_information != null) {
                                        if (block.additional_information.slab) {
                                            RegistryUtils.register(new SlabImpl(block),
                                                    Utils.appendToPath(block.information.name.id, "_slab"), ItemGroup.BUILDING_BLOCKS);
                                            Artifice.registerAssets(String.format("obsidian:%s_slab_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                                block.information.name.translated.forEach((languageId, name) -> {
                                                    clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_slab"),
                                                                name + " Slab");
                                                    });
                                                });
                                            });
                                            Artifice.registerData(String.format("hidden_gems:%s_slab_data", pack.getIdentifier().getPath()), serverResourcePackBuilder -> {
                                                serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_slab"), lootTableBuilder -> {
                                                    lootTableBuilder.type(new Identifier("block"));
                                                    lootTableBuilder.pool(pool -> {
                                                        pool.rolls(1);
                                                        pool.entry(entry -> {
                                                            entry.type(new Identifier("item"));
                                                            entry.function(new Identifier("set_count"), function ->
                                                                function.condition(new Identifier("block_state_property"), jsonObjectBuilder -> {
                                                                    jsonObjectBuilder.add("block", Utils.appendToPath(block.information.name.id, "_slab").toString());
                                                                    JsonObject property = new JsonObject();
                                                                    property.addProperty("type", "double");
                                                                    jsonObjectBuilder.add("property", property);
                                                                })
                                                            );
                                                            entry.weight(2);
                                                            entry.function(new Identifier("explosion_decay"), function -> { });
                                                            entry.name(Utils.appendToPath(block.information.name.id, "_slab"));
                                                        });
                                                        pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> { });
                                                    });
                                                });
                                                serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name.id, "_slab"), shapedRecipeBuilder -> {
                                                    shapedRecipeBuilder.group(new Identifier(modId, "slabs"));
                                                    shapedRecipeBuilder.pattern(
                                                            "###"
                                                    );
                                                    shapedRecipeBuilder.ingredientItem('#', block.information.name.id);
                                                    shapedRecipeBuilder.result(Utils.appendToPath(block.information.name.id, "_slab"), 6);
                                                });
                                            });
                                        }
                                        if (block.additional_information.stairs) {
                                            RegistryUtils.register(new StairsImpl(block), new Identifier(modId, block.information.name.id.getPath() + "_stairs"),
                                                    ItemGroup.BUILDING_BLOCKS);
                                            Artifice.registerAssets(String.format("obsidian:%s_stairs_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                                block.information.name.translated.forEach((languageId, name) -> {
                                                    clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_stairs"),
                                                                name + " Stairs");
                                                    });
                                                });
                                            });
                                            Artifice.registerData(String.format("hidden_gems:%s_stairs_data", pack.getIdentifier().getPath()), serverResourcePackBuilder -> {
                                                serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_stairs"), lootTableBuilder -> {
                                                    lootTableBuilder.type(new Identifier("block"));
                                                    lootTableBuilder.pool(pool -> {
                                                        pool.rolls(1);
                                                        pool.entry(entry -> {
                                                            entry.type(new Identifier("item"));
                                                            entry.name(Utils.appendToPath(block.information.name.id, "_stairs"));
                                                        });
                                                        pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {

                                                        });
                                                    });
                                                });
                                                serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name.id, "_stairs"), shapedRecipeBuilder -> {
                                                    shapedRecipeBuilder.group(new Identifier(modId, "stairs"));
                                                    shapedRecipeBuilder.pattern(
                                                            "#  ",
                                                            "## ",
                                                            "###"
                                                    );
                                                    shapedRecipeBuilder.ingredientItem('#', block.information.name.id);
                                                    shapedRecipeBuilder.result(Utils.appendToPath(block.information.name.id, "_stairs"), 4);
                                                });
                                            });
                                        }
                                        if (block.additional_information.fence) {
                                            RegistryUtils.register(new FenceImpl(block),
                                                    new Identifier(modId, block.information.name.id.getPath() + "_fence"), ItemGroup.DECORATIONS);
                                            Artifice.registerAssets(String.format("obsidian:%s_fence_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                                block.information.name.translated.forEach((languageId, name) -> {
                                                    clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence"),
                                                                name + " Fence");
                                                    });
                                                });
                                            });
                                            Artifice.registerData(String.format("hidden_gems:%s_fence_data", pack.getIdentifier().getPath()), serverResourcePackBuilder -> {
                                                serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_fence"), lootTableBuilder -> {
                                                    lootTableBuilder.type(new Identifier("block"));
                                                    lootTableBuilder.pool(pool -> {
                                                        pool.rolls(1);
                                                        pool.entry(entry -> {
                                                            entry.type(new Identifier("item"));
                                                            entry.name(Utils.appendToPath(block.information.name.id, "_fence"));
                                                        });
                                                        pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {

                                                        });
                                                    });
                                                });
                                                serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name.id, "_fence"), shapedRecipeBuilder -> {
                                                    shapedRecipeBuilder.group(new Identifier(modId, "fences"));
                                                    shapedRecipeBuilder.pattern(
                                                            "W#W",
                                                            "W#W"
                                                    );
                                                    shapedRecipeBuilder.ingredientItem('W', block.information.name.id);
                                                    shapedRecipeBuilder.ingredientItem('#', new Identifier("stick"));
                                                    shapedRecipeBuilder.result(Utils.appendToPath(block.information.name.id, "_fence"), 3);
                                                });
                                            });
                                        }
                                        if (block.additional_information.fenceGate) {
                                            RegistryUtils.register(new FenceGateImpl(block),
                                                    Utils.appendToPath(block.information.name.id, "_fence_gate"), ItemGroup.REDSTONE);
                                            Artifice.registerAssets(String.format("obsidian:%s_fence_gate_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                                block.information.name.translated.forEach((languageId, name) -> {
                                                    clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence_gate"),
                                                                name + " Fence Gate");
                                                    });
                                                });
                                            });
                                            Artifice.registerData(String.format("hidden_gems:%s_fence_gate_data", pack.getIdentifier().getPath()), serverResourcePackBuilder -> {
                                                serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_fence_gate"), lootTableBuilder -> {
                                                    lootTableBuilder.type(new Identifier("block"));
                                                    lootTableBuilder.pool(pool -> {
                                                        pool.rolls(1);
                                                        pool.entry(entry -> {
                                                            entry.type(new Identifier("item"));
                                                            entry.name(new Identifier(modId, block.information.name.id.getPath() + "_fence_gate"));
                                                        });
                                                        pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> { });
                                                    });
                                                });
                                                serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name.id, "_fence_gate"), shapedRecipeBuilder -> {
                                                    shapedRecipeBuilder.group(new Identifier(modId, "fence_gates"));
                                                    shapedRecipeBuilder.pattern(
                                                            "#W#",
                                                            "#W#"
                                                    );
                                                    shapedRecipeBuilder.ingredientItem('W', block.information.name.id);
                                                    shapedRecipeBuilder.ingredientItem('#', new Identifier("stick"));
                                                    shapedRecipeBuilder.result(Utils.appendToPath(block.information.name.id, "_fence_gate"), 3);
                                                });
                                            });
                                        }
                                        if (block.additional_information.walls) {
                                            RegistryUtils.register(new WallImpl(block),
                                                    Utils.appendToPath(block.information.name.id, "_wall"), ItemGroup.DECORATIONS);
                                            Artifice.registerAssets(String.format("obsidian:%s_wall_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                                block.information.name.translated.forEach((languageId, name) -> {
                                                    clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_wall"),
                                                                name + " Wall");
                                                    });
                                                });
                                            });
                                            Artifice.registerData(String.format("hidden_gems:%s_wall_data", pack.getIdentifier().getPath()), serverResourcePackBuilder ->
                                                serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name.id, "_wall"), lootTableBuilder -> {
                                                    lootTableBuilder.type(new Identifier("block"));
                                                    lootTableBuilder.pool(pool -> {
                                                        pool.rolls(1);
                                                        pool.entry(entry -> {
                                                            entry.type(new Identifier("item"));
                                                            entry.name(Utils.appendToPath(block.information.name.id, "_wall"));
                                                        });
                                                        pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> { });
                                                    });
                                                })
                                            );
                                        }
                                    }
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register block %s.", block.information.name.id));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "items").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "items").toFile().listFiles())) {
                            if (file.isFile()) {
                                io.github.vampirestudios.obsidian.api.item.Item item = GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.item.Item.class);
                                try {
                                    RegistryUtils.registerItem(new ItemImpl(item, new Item.Settings().group(item.information.getItemGroup())/*.rarity(item.information.getRarity())*/
                                            .maxCount(item.information.max_count)), item.information.name.id);
                                    /*ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, list) -> {
                                        list.clear();
                                        list.add(item.information.name.getName(false));

                                        if (item.display != null && item.display.lore.length != 0) {
                                            for (TooltipInformation tooltipInformation : item.display.lore) {
                                                list.add(tooltipInformation.getTextType(tooltipInformation.text));
                                            }
                                        }
                                    });*/
                                    Artifice.registerAssets(String.format("obsidian:%s_item_assets", item.information.name.id.getPath()), clientResourcePackBuilder -> {
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
                                    }).dumpResources("test");
                                    System.out.println(String.format("Registered an item called %s", item.information.name.id));
                                    items.add(item);
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register item %s.", item.information.name.id));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "items", "tools").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "items", "tools").toFile().listFiles())) {
                            if (file.isFile()) {
                                io.github.vampirestudios.obsidian.api.item.ToolItem tool = GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.item.ToolItem.class);
                                try {
                                    ToolMaterial material = new ToolMaterial() {
                                        @Override
                                        public int getDurability() {
                                            return tool.material.durability;
                                        }

                                        @Override
                                        public float getMiningSpeedMultiplier() {
                                            return tool.material.miningSpeed;
                                        }

                                        @Override
                                        public float getAttackDamage() {
                                            return tool.material.attackDamage;
                                        }

                                        @Override
                                        public int getMiningLevel() {
                                            return tool.material.miningLevel;
                                        }

                                        @Override
                                        public int getEnchantability() {
                                            return tool.material.enchantability;
                                        }

                                        @Override
                                        public Ingredient getRepairIngredient() {
                                            return Ingredient.ofItems(Registry.ITEM.get(tool.material.repairItem));
                                        }
                                    };
                                    switch (tool.toolType) {
                                        case "pickaxe":
                                            RegistryUtils.registerItem(new PickaxeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                    .group(tool.information.getItemGroup())/*.rarity(tool.information.getRarity())*/.maxCount(tool.information.max_count)),
                                                    tool.information.name.id);
                                            break;
                                        case "shovel":
                                            RegistryUtils.registerItem(new ShovelItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                    .group(tool.information.getItemGroup())/*.rarity(tool.information.getRarity())*/.maxCount(tool.information.max_count)),
                                                    tool.information.name.id);
                                            break;
                                        case "hoe":
                                            RegistryUtils.registerItem(new HoeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                            .group(tool.information.getItemGroup())/*.rarity(tool.information.getRarity())*/.maxCount(tool.information.max_count)),
                                                    tool.information.name.id);
                                            break;
                                        case "axe":
                                            RegistryUtils.registerItem(new AxeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                    .group(tool.information.getItemGroup())/*.rarity(tool.information.getRarity())*/.maxCount(tool.information.max_count)),
                                                    tool.information.name.id);
                                            break;
                                    }
                                    /*ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, list) -> {
                                        list.clear();
                                        list.add(tool.information.name.getName(false));

                                        if (tool.display != null && tool.display.lore.length != 0) {
                                            for (TooltipInformation tooltipInformation : tool.display.lore) {
                                                list.add(tooltipInformation.getTextType(tooltipInformation.text));
                                            }
                                        }
                                    });*/
                                    Artifice.registerAssets(String.format("obsidian:%s_tool_assets", tool.information.name.id.getPath()), clientResourcePackBuilder -> {
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
                                    }).dumpResources("test");
                                    System.out.println(String.format("Registered a tool called %s", tool.information.name));
                                    tools.add(tool);
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register tool %s.", tool.information.name));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "items", "weapons").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "items", "weapons").toFile().listFiles())) {
                            if (file.isFile()) {
                                WeaponItem weapon = GSON.fromJson(new FileReader(file), WeaponItem.class);
                                try {
                                    ToolMaterial material = new ToolMaterial() {
                                        @Override
                                        public int getDurability() {
                                            return weapon.material.durability;
                                        }

                                        @Override
                                        public float getMiningSpeedMultiplier() {
                                            return weapon.material.miningSpeed;
                                        }

                                        @Override
                                        public float getAttackDamage() {
                                            return weapon.material.attackDamage;
                                        }

                                        @Override
                                        public int getMiningLevel() {
                                            return weapon.material.miningLevel;
                                        }

                                        @Override
                                        public int getEnchantability() {
                                            return weapon.material.enchantability;
                                        }

                                        @Override
                                        public Ingredient getRepairIngredient() {
                                            return Ingredient.ofItems(Registry.ITEM.get(weapon.material.repairItem));
                                        }
                                    };
                                    RegistryUtils.registerItem(new MeleeWeaponImpl(weapon, material, weapon.attackDamage, weapon.attackSpeed, new Item.Settings()
                                            .group(weapon.information.getItemGroup())
//                                            .rarity(weapon.information.getRarity())
                                            .maxCount(weapon.information.max_count)), weapon.information.name.id);
                                    /*ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, list) -> {
                                        list.clear();
                                        list.add(weapon.information.name.getName(false));

                                        if (weapon.display != null && weapon.display.lore.length != 0) {
                                            for (TooltipInformation tooltipInformation : weapon.display.lore) {
                                                list.add(tooltipInformation.getTextType(tooltipInformation.text));
                                            }
                                        }
                                    });*/
                                    Artifice.registerAssets(String.format("obsidian:%s_weapon_assets", weapon.information.name.id.getPath()), clientResourcePackBuilder -> {
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
                                    }).dumpResources("test");
                                    System.out.println(String.format("Registered a weapon called %s", weapon.information.name.id));
                                    weapons.add(weapon);
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register weapon %s.", weapon.information.name.id));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "items", "food").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "items", "food").toFile().listFiles())) {
                            if (file.isFile()) {
                                FoodItem foodItem = GSON.fromJson(new FileReader(file), FoodItem.class);
                                try {
                                    FoodComponent foodComponent = foodItem.food_information.getBuilder().build();
                                    Registry.register(Registry.ITEM, foodItem.information.name.id, new ItemImpl(foodItem, new Item.Settings()
                                            .group(foodItem.information.getItemGroup())
                                            .maxCount(foodItem.information.max_count)
                                            .maxDamage(foodItem.information.use_duration)
//                                            .rarity(foodItem.information.getRarity())
                                            .food(foodComponent)));
                                    /*ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, list) -> {
                                        list.clear();
                                        list.add(foodItem.information.name.getName(false));

                                        if (foodItem.display != null && foodItem.display.lore.length != 0) {
                                            for (TooltipInformation tooltipInformation : foodItem.display.lore) {
                                                list.add(tooltipInformation.getTextType(tooltipInformation.text));
                                            }
                                        }
                                    });*/
                                    Artifice.registerAssets(String.format("obsidian:%s_food_assets", foodItem.information.name.id.getPath()), clientResourcePackBuilder -> {
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
                                    }).dumpResources("test");
                                    System.out.println(String.format("Registered a food called %s", foodItem.information.name.id));
                                    items.add(foodItem);
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register food %s.", foodItem.information.name.id));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "potions").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "potions").toFile().listFiles())) {
                            if (file.isFile()) {
                                Potion potion = GSON.fromJson(new FileReader(file), Potion.class);
                                try {
                                    if (potion == null) continue;
                                    Registry.register(Registry.POTION, potion.name,
                                            new net.minecraft.potion.Potion(new StatusEffectInstance(potion.getEffectType(), potion.getEffects().duration * 20, potion.getEffects().amplifier)));
                                    System.out.println(String.format("Registered a potion effect called %s", potion.name));

                                    potions.add(potion);
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register food %s.", potion.name));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "commands").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "commands").toFile().listFiles())) {
                            if (file.isFile()) {
                                Command command = GSON.fromJson(new FileReader(file), Command.class);
                                try {
                                    if(command == null) continue;

                                    // Using a lambda
                                    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
                                        // This command will be registered regardless of the server being dedicated or integrated
                                        CommandImpl.register(command, dispatcher);
                                    });
                                    commands.add(command);
                                    System.out.println(String.format("Registered a command called %s", command.name));
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register command %s.", command.name));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "enchantments").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "enchantments").toFile().listFiles())) {
                            if (file.isFile()) {
                                Enchantment enchantment = GSON.fromJson(new FileReader(file), Enchantment.class);
                                try {
                                    if(enchantment == null) continue;
                                    Registry.register(Registry.ENCHANTMENT, enchantment.id, new EnchantmentImpl(enchantment));
                                    Artifice.registerAssets(Utils.appendToPath(enchantment.id, "_lang_assets"), clientResourcePackBuilder -> {
                                        clientResourcePackBuilder.addTranslations(new Identifier(enchantment.id.getNamespace(), "en_us"), translationBuilder ->
                                                translationBuilder.entry(String.format("enchantment.%s.%s", enchantment.id.getNamespace(), enchantment.id.getPath()),
                                                        enchantment.name));
                                    });
                                    enchantments.add(enchantment);
                                    System.out.println(String.format("Registered an enchantment called %s", enchantment.name));
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register enchantment %s.", enchantment.name));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "status_effects").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "status_effects").toFile().listFiles())) {
                            if (file.isFile()) {
                                Command command = GSON.fromJson(new FileReader(file), Command.class);
                                try {
                                    if(command == null) continue;

                                    // Using a lambda
                                    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
                                        // This command will be registered regardless of the server being dedicated or integrated
                                        CommandImpl.register(command, dispatcher);
                                    });
                                    commands.add(command);
                                    System.out.println(String.format("Registered a command called %s", command.name));
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register command %s.", command.name));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "entities").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "entities").toFile().listFiles())) {
                            if (file.isFile()) {
                                Entity entity = GSON.fromJson(new FileReader(file), Entity.class);
                                try {
                                    if(entity == null) continue;
                                    String baseColor = entity.spawn_egg.base_color.replace("#", "").replace("0x", "");
                                    String overlayColor = entity.spawn_egg.overlay_color.replace("#", "").replace("0x", "");

                                    EntityType<EntityImpl> entityType = EntityRegistryBuilder.<EntityImpl>createBuilder(entity.identifier)
                                            .entity((type, world) -> new EntityImpl(type, world, entity))
                                            .category(entity.components.getCategory())
                                            .dimensions(EntityDimensions.fixed(entity.components.collision_box.width, entity.components.collision_box.height))
                                            .summonable(entity.summonable)
                                            .hasEgg(entity.spawnable)
                                            .egg(Integer.parseInt(baseColor, 16), Integer.parseInt(overlayColor, 16))
                                            .build();
                                    FabricDefaultAttributeRegistry.register(entityType, EntityUtils.createGenericEntityAttributes(entity.components.health.max));
                                    Artifice.registerAssets(Utils.appendToPath(entity.identifier, "_assets"), clientResourcePackBuilder -> {
                                        clientResourcePackBuilder.addTranslations(new Identifier(entity.identifier.getNamespace(), "en_us"), translationBuilder ->
                                                translationBuilder.entry(String.format("item.%s.%s", entity.identifier.getNamespace(), entity.identifier.getPath() + "_spawn_egg"),
                                                        WordUtils.capitalizeFully(entity.identifier.getPath().replace("_", " ") + " Spawn Egg")));
                                        clientResourcePackBuilder.addItemModel(new Identifier(entity.identifier.getNamespace(), entity.identifier.getPath() + "_spawn_egg"), modelBuilder -> {
                                            modelBuilder.parent(new Identifier("item/template_spawn_egg"));
                                        });
                                    });
                                    EntityRendererRegistry.INSTANCE.register(entityType, (entityRenderDispatcher, context) -> new EntityImplRenderer(entityRenderDispatcher, entity));
                                    System.out.println(String.format("Registered an entity called %s", entity.identifier.toString()));
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register entity %s.", entity.identifier.toString()));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "currency").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "currency").toFile().listFiles())) {
                            if (file.isFile()) {
                                Currency currency = GSON.fromJson(new FileReader(file), Currency.class);
                                try {
                                    if(currency == null) continue;
                                    PlayerJoinCallback.EVENT.register(player -> {
                                        Scoreboard scoreboard = player.getScoreboard();
                                        if (!scoreboard.containsObjective(currency.name.toLowerCase())) {
                                            scoreboard.addObjective(currency.name.toLowerCase(), ScoreboardCriterion.DUMMY, new LiteralText(currency.name), ScoreboardCriterion.RenderType.INTEGER);
                                        }
                                    });
                                    Obsidian.LOGGER.info(String.format("Registered a currency called %s", currency.name));
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register currency %s.", currency.name));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.exit(0);
        }
    }

    private static void fillDefaultConfigs() {
        MATERIALS_DIRECTORY.mkdirs();
    }

}
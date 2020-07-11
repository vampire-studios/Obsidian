package io.github.vampirestudios.obsidian.configPack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.IAddonPack;
import io.github.vampirestudios.obsidian.api.TextureInformation;
import io.github.vampirestudios.obsidian.api.block.Block;
import io.github.vampirestudios.obsidian.api.command.Command;
import io.github.vampirestudios.obsidian.api.item.FoodItem;
import io.github.vampirestudios.obsidian.api.item.WeaponItem;
import io.github.vampirestudios.obsidian.api.potion.Potion;
import io.github.vampirestudios.obsidian.minecraft.*;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import io.github.vampirestudios.obsidian.utils.SimpleStringDeserializer;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
                } else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".mcpack")) {
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
                                            .strength(block.information.hardness, block.information.resistance).lightLevel(block.information.luminance)
                                            .drops(block.information.drop).collidable(block.information.collidable).slipperiness(block.information.slipperiness);
                                    if (block.information.dynamicBounds) {
                                        blockSettings.dynamicBounds();
                                    }
                                    if (block.information.randomTicks) {
                                        blockSettings.ticksRandomly();
                                    }
                                    if(block.additionalInformation.rotatable) {
                                        RegistryUtils.register(new HorizontalFacingBlockImpl(block, blockSettings.build()), block.information.name,
                                                block.information.getItemGroup());
                                    } else if(block.additionalInformation.pillar) {
                                        RegistryUtils.register(new PillarBlockImpl(block, blockSettings.build()), block.information.name,
                                                block.information.getItemGroup());
                                    } else {
                                        RegistryUtils.register(new BlockImpl(block, blockSettings.build()), block.information.name,
                                                block.information.getItemGroup());
                                    }
                                    Artifice.registerAssets(String.format("obsidian:%s_block_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                        clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.getNamespace(), "en_us"), translationBuilder ->
                                                translationBuilder.entry(String.format("block.%s.%s", block.information.name.getNamespace(), block.information.name.getPath()),
                                                        block.information.displayName));
                                        if (block.information.texturesAndModels != null) {
                                            if(block.additionalInformation.rotatable) {
                                                clientResourcePackBuilder.addBlockState(block.information.name, blockStateBuilder -> {
                                                    blockStateBuilder.variant("facing=north", variant -> variant.model(Utils.prependToPath(block.information.name, "block/")));
                                                    blockStateBuilder.variant("facing=south", variant -> variant.model(Utils.prependToPath(block.information.name, "block/")).rotationY(180));
                                                    blockStateBuilder.variant("facing=east", variant -> variant.model(Utils.prependToPath(block.information.name, "block/")).rotationY(90));
                                                    blockStateBuilder.variant("facing=west", variant -> variant.model(Utils.prependToPath(block.information.name, "block/")).rotationY(270));
                                                });
                                                clientResourcePackBuilder.addBlockModel(block.information.name, modelBuilder -> {
                                                    modelBuilder.parent(block.information.texturesAndModels.modelType);
                                                    for(TextureInformation texture : block.information.texturesAndModels.textures) {
                                                        modelBuilder.texture(texture.textureName, texture.texturePath);
                                                    }
                                                });
                                                clientResourcePackBuilder.addItemModel(block.information.name, modelBuilder -> {
                                                    modelBuilder.parent(Utils.prependToPath(block.information.name, "block/"));
                                                });
                                            } else if(block.additionalInformation.pillar) {
                                                clientResourcePackBuilder.addBlockState(block.information.name, blockStateBuilder -> {
                                                    blockStateBuilder.variant("axis=x", variant -> variant.model(Utils.prependToPath(block.information.name, "block/"))
                                                            .rotationX(90).rotationY(90));
                                                    blockStateBuilder.variant("axis=y", variant -> variant.model(Utils.prependToPath(block.information.name, "block/")));
                                                    blockStateBuilder.variant("axis=z", variant -> variant.model(Utils.prependToPath(block.information.name, "block/"))
                                                            .rotationX(90));
                                                });
                                                clientResourcePackBuilder.addBlockModel(block.information.name, modelBuilder -> {
                                                    modelBuilder.parent(block.information.texturesAndModels.modelType);
                                                    for(TextureInformation texture : block.information.texturesAndModels.textures) {
                                                        modelBuilder.texture(texture.textureName, texture.texturePath);
                                                    }
                                                });
                                                clientResourcePackBuilder.addItemModel(block.information.name, modelBuilder -> {
                                                    modelBuilder.parent(Utils.prependToPath(block.information.name, "block/"));
                                                });
                                            } else {
                                                clientResourcePackBuilder.addBlockState(block.information.name, blockStateBuilder ->
                                                        blockStateBuilder.variant("", variant -> variant.model(Utils.prependToPath(block.information.name, "block/"))));
                                                clientResourcePackBuilder.addBlockModel(block.information.name, modelBuilder -> {
                                                    modelBuilder.parent(block.information.texturesAndModels.modelType);
                                                    for(String texture : ) {
                                                        modelBuilder.texture(block.information.texturesAndModels.textures., texture.texturePath);
                                                    }
                                                });
                                                clientResourcePackBuilder.addItemModel(block.information.name, modelBuilder -> {
                                                    modelBuilder.parent(Utils.prependToPath(block.information.name, "block/"));
                                                });
                                            }
                                        }
                                    }).dumpResources("test");
                                    System.out.println(String.format("Registered a block called %s", block.information.name));
                                    Artifice.registerData(String.format("hidden_gems:%s_block_data", pack.getIdentifier().getPath()), serverResourcePackBuilder ->
                                            serverResourcePackBuilder.addLootTable(block.information.name, lootTableBuilder -> {
                                                lootTableBuilder.type(new Identifier("block"));
                                                lootTableBuilder.pool(pool -> {
                                                    pool.rolls(1);
                                                    pool.entry(entry -> {
                                                        entry.type(new Identifier("item"));
                                                        entry.name(block.information.name);
                                                    });
                                                    pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {

                                                    });
                                                });
                                            })
                                    ).dumpResources("test");
                                    blocks.add(block);
                                    if (block.additionalInformation != null) {
                                        if (block.additionalInformation.slab) {
                                            RegistryUtils.register(new SlabImpl(block),
                                                    Utils.appendToPath(block.information.name, "_slab"), ItemGroup.BUILDING_BLOCKS);
                                            Artifice.registerAssets(String.format("obsidian:%s_slab_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.getNamespace(), "en_us"), translationBuilder ->
                                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.getNamespace(), block.information.name.getPath() + "_slab"),
                                                                block.information.displayName + " Slab"));
                                            });
                                            Artifice.registerData(String.format("hidden_gems:%s_slab_data", pack.getIdentifier().getPath()), serverResourcePackBuilder -> {
                                                serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name, "_slab"), lootTableBuilder -> {
                                                    lootTableBuilder.type(new Identifier("block"));
                                                    lootTableBuilder.pool(pool -> {
                                                        pool.rolls(1);
                                                        pool.entry(entry -> {
                                                            entry.type(new Identifier("item"));
                                                            entry.function(new Identifier("set_count"), function ->
                                                                function.condition(new Identifier("block_state_property"), jsonObjectBuilder -> {
                                                                    jsonObjectBuilder.add("block", Utils.appendToPath(block.information.name, "_slab").toString());
                                                                    JsonObject property = new JsonObject();
                                                                    property.addProperty("type", "double");
                                                                    jsonObjectBuilder.add("property", property);
                                                                })
                                                            );
                                                            entry.weight(2);
                                                            entry.function(new Identifier("explosion_decay"), function -> { });
                                                            entry.name(Utils.appendToPath(block.information.name, "_slab"));
                                                        });
                                                        pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> { });
                                                    });
                                                });
                                                serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name, "_slab"), shapedRecipeBuilder -> {
                                                    shapedRecipeBuilder.group(new Identifier(modId, "slabs"));
                                                    shapedRecipeBuilder.pattern(
                                                            "###"
                                                    );
                                                    shapedRecipeBuilder.ingredientItem('#', block.information.name);
                                                    shapedRecipeBuilder.result(Utils.appendToPath(block.information.name, "_slab"), 6);
                                                });
                                            });
                                        }
                                        if (block.additionalInformation.stairs) {
                                            RegistryUtils.register(new StairsImpl(block), new Identifier(modId, block.information.name.getPath() + "_stairs"),
                                                    ItemGroup.BUILDING_BLOCKS);
                                            Artifice.registerAssets(String.format("obsidian:%s_stairs_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.getNamespace(), "en_us"), translationBuilder ->
                                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.getNamespace(), block.information.name.getPath() + "_stairs"),
                                                                block.information.displayName + " Stairs"));
                                            });
                                            Artifice.registerData(String.format("hidden_gems:%s_stairs_data", pack.getIdentifier().getPath()), serverResourcePackBuilder -> {
                                                serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name, "_stairs"), lootTableBuilder -> {
                                                    lootTableBuilder.type(new Identifier("block"));
                                                    lootTableBuilder.pool(pool -> {
                                                        pool.rolls(1);
                                                        pool.entry(entry -> {
                                                            entry.type(new Identifier("item"));
                                                            entry.name(Utils.appendToPath(block.information.name, "_stairs"));
                                                        });
                                                        pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {

                                                        });
                                                    });
                                                });
                                                serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name, "_stairs"), shapedRecipeBuilder -> {
                                                    shapedRecipeBuilder.group(new Identifier(modId, "stairs"));
                                                    shapedRecipeBuilder.pattern(
                                                            "#  ",
                                                            "## ",
                                                            "###"
                                                    );
                                                    shapedRecipeBuilder.ingredientItem('#', block.information.name);
                                                    shapedRecipeBuilder.result(Utils.appendToPath(block.information.name, "_stairs"), 4);
                                                });
                                            });
                                        }
                                        if (block.additionalInformation.fence) {
                                            RegistryUtils.register(new FenceImpl(block),
                                                    new Identifier(modId, block.information.name.getPath() + "_fence"), ItemGroup.DECORATIONS);
                                            Artifice.registerAssets(String.format("obsidian:%s_fence_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.getNamespace(), "en_us"), translationBuilder ->
                                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.getNamespace(), block.information.name.getPath() + "_fence"),
                                                                block.information.displayName + " Fence"));
                                            });
                                            Artifice.registerData(String.format("hidden_gems:%s_fence_data", pack.getIdentifier().getPath()), serverResourcePackBuilder -> {
                                                serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name, "_fence"), lootTableBuilder -> {
                                                    lootTableBuilder.type(new Identifier("block"));
                                                    lootTableBuilder.pool(pool -> {
                                                        pool.rolls(1);
                                                        pool.entry(entry -> {
                                                            entry.type(new Identifier("item"));
                                                            entry.name(Utils.appendToPath(block.information.name, "_fence"));
                                                        });
                                                        pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> {

                                                        });
                                                    });
                                                });
                                                serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name, "_fence"), shapedRecipeBuilder -> {
                                                    shapedRecipeBuilder.group(new Identifier(modId, "fences"));
                                                    shapedRecipeBuilder.pattern(
                                                            "W#W",
                                                            "W#W"
                                                    );
                                                    shapedRecipeBuilder.ingredientItem('W', block.information.name);
                                                    shapedRecipeBuilder.ingredientItem('#', new Identifier("stick"));
                                                    shapedRecipeBuilder.result(Utils.appendToPath(block.information.name, "_fence"), 3);
                                                });
                                            });
                                        }
                                        if (block.additionalInformation.fenceGate) {
                                            RegistryUtils.register(new FenceGateImpl(block),
                                                    Utils.appendToPath(block.information.name, "_fence_gate"), ItemGroup.REDSTONE);
                                            Artifice.registerAssets(String.format("obsidian:%s_fence_gate_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.getNamespace(), "en_us"), translationBuilder ->
                                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.getNamespace(), block.information.name.getPath() + "_fence_gate"),
                                                                block.information.displayName + " Fence Gate"));
                                            });
                                            Artifice.registerData(String.format("hidden_gems:%s_fence_gate_data", pack.getIdentifier().getPath()), serverResourcePackBuilder -> {
                                                serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name, "_fence_gate"), lootTableBuilder -> {
                                                    lootTableBuilder.type(new Identifier("block"));
                                                    lootTableBuilder.pool(pool -> {
                                                        pool.rolls(1);
                                                        pool.entry(entry -> {
                                                            entry.type(new Identifier("item"));
                                                            entry.name(new Identifier(modId, block.information.name.getPath() + "_fence_gate"));
                                                        });
                                                        pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> { });
                                                    });
                                                });
                                                serverResourcePackBuilder.addShapedRecipe(Utils.appendToPath(block.information.name, "_fence_gate"), shapedRecipeBuilder -> {
                                                    shapedRecipeBuilder.group(new Identifier(modId, "fence_gates"));
                                                    shapedRecipeBuilder.pattern(
                                                            "#W#",
                                                            "#W#"
                                                    );
                                                    shapedRecipeBuilder.ingredientItem('W', block.information.name);
                                                    shapedRecipeBuilder.ingredientItem('#', new Identifier("stick"));
                                                    shapedRecipeBuilder.result(Utils.appendToPath(block.information.name, "_fence_gate"), 3);
                                                });
                                            });
                                        }
                                        if (block.additionalInformation.walls) {
                                            RegistryUtils.register(new WallImpl(block),
                                                    Utils.appendToPath(block.information.name, "_wall"), ItemGroup.DECORATIONS);
                                            Artifice.registerAssets(String.format("obsidian:%s_wall_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                                clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.getNamespace(), "en_us"), translationBuilder ->
                                                        translationBuilder.entry(String.format("block.%s.%s", block.information.name.getNamespace(), block.information.name.getPath() + "_wall"),
                                                                block.information.displayName + " Wall"));
                                            });
                                            Artifice.registerData(String.format("hidden_gems:%s_wall_data", pack.getIdentifier().getPath()), serverResourcePackBuilder ->
                                                serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name, "_wall"), lootTableBuilder -> {
                                                    lootTableBuilder.type(new Identifier("block"));
                                                    lootTableBuilder.pool(pool -> {
                                                        pool.rolls(1);
                                                        pool.entry(entry -> {
                                                            entry.type(new Identifier("item"));
                                                            entry.name(Utils.appendToPath(block.information.name, "_wall"));
                                                        });
                                                        pool.condition(new Identifier("survives_explosion"), jsonObjectBuilder -> { });
                                                    });
                                                })
                                            );
                                        }
                                    }
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register block %s.", block.information.name));
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
                                    RegistryUtils.registerItem(new ItemImpl(item, new Item.Settings().group(item.information.getItemGroup()).rarity(item.information.getRarity())
                                            .maxCount(item.information.maxStackSize)), item.information.name);
                                    Artifice.registerAssets(String.format("obsidian:%s_item_assets", pack.getIdentifier().getPath()), clientResourcePackBuilder -> {
                                        clientResourcePackBuilder.addTranslations(new Identifier(item.information.name.getNamespace(), "en_us"), translationBuilder ->
                                                translationBuilder.entry(String.format("item.%s.%s", item.information.name.getNamespace(), item.information.name.getPath()),
                                                        item.information.displayName));
                                        if (item.information.texturesAndModels != null) {
                                            clientResourcePackBuilder.addItemModel(item.information.name, modelBuilder -> {
                                                modelBuilder.parent(item.information.texturesAndModels.modelType);
                                                for(TextureInformation texture : item.information.texturesAndModels.textures) {
                                                    modelBuilder.texture(texture.textureName, texture.texturePath);
                                                }
                                            });
                                        }
                                    }).dumpResources("test");
                                    System.out.println(String.format("Registered an item called %s", item.information.name));
                                    items.add(item);
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register item %s.", item.information.name));
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
                                                    .group(tool.information.getItemGroup()).rarity(tool.information.getRarity()).maxCount(tool.information.maxStackSize)),
                                                    tool.information.name);
                                            break;
                                        case "shovel":
                                            RegistryUtils.registerItem(new ShovelItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                    .group(tool.information.getItemGroup()).rarity(tool.information.getRarity()).maxCount(tool.information.maxStackSize)),
                                                    tool.information.name);
                                            break;
                                        case "hoe":
                                            RegistryUtils.registerItem(new HoeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                            .group(tool.information.getItemGroup()).rarity(tool.information.getRarity()).maxCount(tool.information.maxStackSize)),
                                                    tool.information.name);
                                            break;
                                        case "axe":
                                            RegistryUtils.registerItem(new AxeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                    .group(tool.information.getItemGroup()).rarity(tool.information.getRarity()).maxCount(tool.information.maxStackSize)),
                                                    tool.information.name);
                                            break;
                                    }
                                    Artifice.registerAssets(String.format("obsidian:%s_tool_assets", tool.information.name.getPath()), clientResourcePackBuilder -> {
                                        clientResourcePackBuilder.addTranslations(new Identifier(tool.information.name.getNamespace(), "en_us"), translationBuilder ->
                                                translationBuilder.entry(String.format("item.%s.%s", tool.information.name.getNamespace(), tool.information.name.getPath()),
                                                        tool.information.displayName));
                                        if (tool.information.texturesAndModels != null) {
                                            clientResourcePackBuilder.addItemModel(tool.information.name, modelBuilder -> {
                                                modelBuilder.parent(tool.information.texturesAndModels.modelType);
                                                for(TextureInformation texture : tool.information.texturesAndModels.textures) {
                                                    modelBuilder.texture(texture.textureName, texture.texturePath);
                                                }
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
                                    RegistryUtils.registerItem(new SwordItemImpl(weapon, material, weapon.attackDamage, weapon.attackSpeed, new Item.Settings()
                                            .group(weapon.information.getItemGroup()).rarity(weapon.information.getRarity())
                                            .maxCount(weapon.information.maxStackSize)), weapon.information.name);
                                    Artifice.registerAssets(String.format("obsidian:%s_weapon_assets", weapon.information.name.getPath()), clientResourcePackBuilder -> {
                                        clientResourcePackBuilder.addTranslations(new Identifier(weapon.information.name.getNamespace(), "en_us"), translationBuilder ->
                                                translationBuilder.entry(String.format("item.%s.%s", weapon.information.name.getNamespace(), weapon.information.name.getPath()),
                                                        weapon.information.displayName));
                                        if (weapon.information.texturesAndModels != null) {
                                            clientResourcePackBuilder.addItemModel(weapon.information.name, modelBuilder -> {
                                                modelBuilder.parent(weapon.information.texturesAndModels.modelType);
                                                for(TextureInformation texture : weapon.information.texturesAndModels.textures) {
                                                    modelBuilder.texture(texture.textureName, texture.texturePath);
                                                }
                                            });
                                        }
                                    }).dumpResources("test");
                                    System.out.println(String.format("Registered a weapon called %s", weapon.information.name));
                                    weapons.add(weapon);
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register weapon %s.", weapon.information.name));
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
                                    FoodComponent foodComponent = foodItem.components.food.getBuilder().build();
                                    Registry.register(Registry.ITEM, foodItem.information.name, new ItemImpl(foodItem, new Item.Settings()
                                            .group(foodItem.information.getItemGroup())
                                            .maxCount(foodItem.information.maxStackSize)
                                            .maxDamage(foodItem.components.use_duration)
                                            .rarity(foodItem.information.getRarity())
                                            .food(foodComponent)));
                                    Artifice.registerAssets(String.format("obsidian:%s_food_assets", foodItem.information.name.getPath()), clientResourcePackBuilder -> {
                                        clientResourcePackBuilder.addTranslations(new Identifier(foodItem.information.name.getNamespace(), "en_us"), translationBuilder ->
                                                translationBuilder.entry(String.format("item.%s.%s", foodItem.information.name.getNamespace(), foodItem.information.name.getPath()),
                                                        foodItem.information.displayName));
                                        if (foodItem.information.texturesAndModels != null) {
                                            clientResourcePackBuilder.addItemModel(foodItem.information.name, modelBuilder -> {
                                                modelBuilder.parent(foodItem.information.texturesAndModels.modelType);
                                                for(TextureInformation texture : foodItem.information.texturesAndModels.textures) {
                                                    modelBuilder.texture(texture.textureName, texture.texturePath);
                                                }
                                            });
                                        }
                                    }).dumpResources("test");
                                    System.out.println(String.format("Registered a food called %s", foodItem.information.name));
                                    items.add(foodItem);
                                } catch (Exception e) {
                                    Obsidian.LOGGER.error(String.format("[Obsidian] Failed to register food %s.", foodItem.information.name));
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
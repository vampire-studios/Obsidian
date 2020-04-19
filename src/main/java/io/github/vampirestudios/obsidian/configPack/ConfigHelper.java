package io.github.vampirestudios.obsidian.configPack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.hidden_gems.HiddenGems;
import io.github.vampirestudios.obsidian.PlayerPlacementHandlers;
import io.github.vampirestudios.obsidian.SimpleStringDeserializer;
import io.github.vampirestudios.obsidian.api.IAddonPack;
import io.github.vampirestudios.obsidian.api.block.Block;
import io.github.vampirestudios.obsidian.api.entity.Entity;
import io.github.vampirestudios.obsidian.api.item.FoodItem;
import io.github.vampirestudios.obsidian.api.item.WeaponItem;
import io.github.vampirestudios.obsidian.api.potion.Potion;
import io.github.vampirestudios.obsidian.api.world.Biome;
import io.github.vampirestudios.obsidian.api.world.Dimension;
import io.github.vampirestudios.obsidian.client.resource.AddonResourcePackCreator;
import io.github.vampirestudios.obsidian.minecraft.*;
import io.github.vampirestudios.vampirelib.utils.Utils;
import io.github.vampirestudios.vampirelib.utils.registry.RegistryUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.biomes.v1.NetherBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensionType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;
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
    public static List<Entity> entities = new ArrayList<>();
    private static List<Potion> potions = new ArrayList<>();
    private static final List<Dimension> dimensions = new ArrayList<>();
    private static final List<Biome> biomes = new ArrayList<>();

    public static void loadDefault() {
        if (!MATERIALS_DIRECTORY.exists())
            fillDefaultConfigs();
    }

    public static void loadConfig() {
        try {
            FabricLoader.getInstance().getEntrypoints("hidden_gems:addon_packs", IAddonPack.class).forEach(supplier -> {
                try {
                    ADDON_PACKS.add(supplier);
                    HiddenGems.LOGGER.info(String.format("[Hidden Gems] Registering addon: %s from an entrypoint",
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
                            HiddenGems.LOGGER.info(String.format("[Hidden Gems] Registering addon: %s", configPack.getConfigPackInfo().getInformation().getId()));
                        }
                    } catch (Exception e) {
                        HiddenGems.LOGGER.error("[Hidden Gems] Failed to load addon pack.", e);
                    }
                } else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".mcpack")) {
                    try (ZipFile zipFile = new ZipFile(file)) {
                        ZipEntry packInfoEntry = zipFile.getEntry("addon.info.pack");
                        if (packInfoEntry != null) {
                            ConfigPackInfo packInfo = GSON.fromJson(new InputStreamReader(zipFile.getInputStream(packInfoEntry)), ConfigPackInfo.class);
                            ConfigPack configPack = new ConfigPack(packInfo);
                            ADDON_PACKS.add(configPack);
                            HiddenGems.LOGGER.info(String.format("[Hidden Gems] Registering addon: %s", configPack.getConfigPackInfo().getInformation().getId()));
                        }
                    } catch (Exception e) {
                        HiddenGems.LOGGER.error("[Hidden Gems] Failed to load addon pack.", e);
                    }
                }
            }

            String moduleText;
            if (ADDON_PACKS.size() > 1) {
                moduleText = "Loading %d packs:";
            } else {
                moduleText = "Loading %d pack:";
            }

            HiddenGems.LOGGER.info(String.format("[Hidden Gems] " + moduleText, ADDON_PACKS.size()));

            for(IAddonPack pack : ADDON_PACKS) {
                HiddenGems.LOGGER.info(String.format(" - %s", pack.getIdentifier().toString()));

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
                                    RegistryUtils.register(new BlockImpl(block, blockSettings.build()), block.information.name,
                                            block.information.getItemGroup());
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
                                    )/*.dumpResources(Paths.get("saves", MinecraftClient.getInstance().world.getLevelProperties().getLevelName(), "datapacks", pack.getIdentifier().getPath()).toString())*/;
                                    blocks.add(block);
                                    if (block.additionalInformation != null) {
                                        if (block.additionalInformation.slab) {
                                            RegistryUtils.register(new SlabImpl(block),
                                                    Utils.appendToPath(block.information.name, "_slab"), ItemGroup.BUILDING_BLOCKS);
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
                                            });/*.dumpResources(Paths.get("saves", MinecraftClient.getInstance().world.getLevelProperties().getLevelName(), "datapacks", pack.getIdentifier().getPath()).toString());*/
                                        }
                                        if (block.additionalInformation.stairs) {
                                            RegistryUtils.register(new StairsImpl(block), new Identifier(modId, block.information.name + "_stairs"),
                                                    ItemGroup.BUILDING_BLOCKS);
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
                                            });/*.dumpResources(Paths.get("saves", MinecraftClient.getInstance().world.getLevelProperties().getLevelName(), "datapacks", pack.getIdentifier().getPath()).toString());*/
                                        }
                                        if (block.additionalInformation.fence) {
                                            RegistryUtils.register(new FenceImpl(block),
                                                    new Identifier(modId, block.information.name + "_fence"), ItemGroup.DECORATIONS);
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
                                                    shapedRecipeBuilder.group(new Identifier(modId, "'fences"));
                                                    shapedRecipeBuilder.pattern(
                                                            "W#W",
                                                            "W#W"
                                                    );
                                                    shapedRecipeBuilder.ingredientItem('W', block.information.name);
                                                    shapedRecipeBuilder.ingredientItem('#', new Identifier("stick"));
                                                    shapedRecipeBuilder.result(Utils.appendToPath(block.information.name, "_fence"), 3);
                                                });
                                            });/*.dumpResources(Paths.get("saves", MinecraftClient.getInstance().world.getLevelProperties().getLevelName(), "datapacks", pack.getIdentifier().getPath()).toString());*/
                                        }
                                        if (block.additionalInformation.fenceGate) {
                                            RegistryUtils.register(new FenceGateImpl(block),
                                                    Utils.appendToPath(block.information.name, "_fence_gate"), ItemGroup.REDSTONE);
                                            Artifice.registerData(String.format("hidden_gems:%s_fence_gate_data", pack.getIdentifier().getPath()), serverResourcePackBuilder -> {
                                                serverResourcePackBuilder.addLootTable(Utils.appendToPath(block.information.name, "_fence_gate"), lootTableBuilder -> {
                                                    lootTableBuilder.type(new Identifier("block"));
                                                    lootTableBuilder.pool(pool -> {
                                                        pool.rolls(1);
                                                        pool.entry(entry -> {
                                                            entry.type(new Identifier("item"));
                                                            entry.name(new Identifier(modId, block.information.name + "_fence_gate"));
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
                                            });/*.dumpResources(Paths.get("saves", MinecraftClient.getInstance().world.getLevelProperties().getLevelName(), "datapacks", pack.getIdentifier().getPath()).toString());*/
                                        }
                                        if (block.additionalInformation.walls) {
                                            RegistryUtils.register(new WallImpl(block),
                                                    Utils.appendToPath(block.information.name, "_wall"), ItemGroup.DECORATIONS);
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
                                            );/*.dumpResources(Paths.get("saves", MinecraftClient.getInstance().world.getLevelProperties().getLevelName(), "datapacks", pack.getIdentifier().getPath()).toString());*/
                                        }
                                    }
                                } catch (Exception e) {
                                    HiddenGems.LOGGER.error(String.format("[Hidden Gems] Failed to register block %s.", block.information.name));
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
                                    RegistryUtils.registerItem(new Item(new Item.Settings().group(item.information.getItemGroup()).rarity(item.information.getRarity())
                                            .maxCount(item.information.max_count)), item.information.name);
                                    System.out.println(String.format("Registered an item called %s", item.information.name));
                                    items.add(item);
                                } catch (Exception e) {
                                    HiddenGems.LOGGER.error(String.format("[Hidden Gems] Failed to register item %s.", item.information.name));
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
                                            RegistryUtils.registerItem(new PickaxeItemImpl(material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                    .group(tool.information.getItemGroup()).rarity(tool.information.getRarity()).maxCount(tool.information.max_count)),
                                                    tool.information.name);
                                            break;
                                        case "shovel":
                                            RegistryUtils.registerItem(new ShovelItem(material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                    .group(tool.information.getItemGroup()).rarity(tool.information.getRarity()).maxCount(tool.information.max_count)),
                                                    tool.information.name);
                                            break;
                                        case "hoe":
                                            RegistryUtils.registerItem(new HoeItemImpl(material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                            .group(tool.information.getItemGroup()).rarity(tool.information.getRarity()).maxCount(tool.information.max_count)),
                                                    tool.information.name);
                                            break;
                                        case "axe":
                                            RegistryUtils.registerItem(new AxeItemImpl(material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                    .group(tool.information.getItemGroup()).rarity(tool.information.getRarity()).maxCount(tool.information.max_count)),
                                                    tool.information.name);
                                            break;
                                    }
                                    System.out.println(String.format("Registered a tool called %s", tool.information.name));
                                    tools.add(tool);
                                } catch (Exception e) {
                                    HiddenGems.LOGGER.error(String.format("[Hidden Gems] Failed to register tool %s.", tool.information.name));
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
                                    RegistryUtils.registerItem(new SwordItem(material, weapon.attackDamage, weapon.attackSpeed, new Item.Settings()
                                            .group(weapon.information.getItemGroup()).rarity(weapon.information.getRarity())
                                            .maxCount(weapon.information.max_count)), weapon.information.name);
                                    System.out.println(String.format("Registered a weapon called %s", weapon.information.name));
                                    weapons.add(weapon);
                                } catch (Exception e) {
                                    HiddenGems.LOGGER.error(String.format("[Hidden Gems] Failed to register weapon %s.", weapon.information.name));
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
                                    Registry.register(Registry.ITEM, foodItem.information.name, new Item(new Item.Settings()
                                            .group(foodItem.information.getItemGroup())
                                            .maxCount(foodItem.information.getMaxCount())
                                            .maxDamage(foodItem.components.use_duration)
                                            .rarity(foodItem.information.getRarity())
                                            .food(foodComponent)));
                                    System.out.println(String.format("Registered a food called %s", foodItem.information.name));
                                    items.add(foodItem);
                                } catch (Exception e) {
                                    HiddenGems.LOGGER.error(String.format("[Hidden Gems] Failed to register food %s.", foodItem.information.name));
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
                                    HiddenGems.LOGGER.error(String.format("[Hidden Gems] Failed to register food %s.", potion.name));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "biomes").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "biomes").toFile().listFiles())) {
                            if (file.isFile()) {
                                Biome biome = GSON.fromJson(new FileReader(file), Biome.class);
                                try {
                                    if(biome == null) continue;
                                    BiomeImpl biome1 = new BiomeImpl(biome);
                                    Registry.register(Registry.BIOME, biome.name, biome1);
                                    if(biome.dimension.equals("overworld")) {
                                        OverworldBiomes.addContinentalBiome(biome1, OverworldClimate.TEMPERATE, 1.0);
                                    }
                                    if(biome.dimension.equals("nether")) {
                                        NetherBiomes.addNetherBiome(biome1);
                                    }
                                    biomes.add(biome);
                                    System.out.println(String.format("Registered a biome called %s", biome.name));
                                } catch (Exception e) {
                                    HiddenGems.LOGGER.error(String.format("[Hidden Gems] Failed to register biome %s.", biome.name));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                            pack.getConfigPackInfo().getInformation().namespace, "dimensions").toFile().exists()) {
                        for (File file : Objects.requireNonNull(Paths.get(MATERIALS_DIRECTORY.getPath(), pack.getIdentifier().getPath(), "data",
                                pack.getConfigPackInfo().getInformation().namespace, "dimensions").toFile().listFiles())) {
                            if (file.isFile()) {
                                Dimension dimension = GSON.fromJson(new FileReader(file), Dimension.class);
                                try {
                                    if(dimension == null) continue;
                                    Set<net.minecraft.world.biome.Biome> biomes = new LinkedHashSet<>();
                                    for (Identifier name : dimension.biomes) {
                                        biomes.add(Registry.BIOME.get(name));
                                    }

                                    FabricDimensionType.Builder builder = FabricDimensionType.builder()
                                            .biomeAccessStrategy(HorizontalVoronoiBiomeAccessType.INSTANCE)
                                            .skyLight(dimension.hasSkyLight)
                                            .factory((world, dimensionType) -> new DimensionImpl(dimension, world, dimensionType, biomes))
                                            .defaultPlacer(PlayerPlacementHandlers.SURFACE_WORLD.getEntityPlacer());

                                    builder.buildAndRegister(dimension.name);
                                    dimensions.add(dimension);
                                    System.out.println(String.format("Registered a dimension called %s", dimension.name));
                                } catch (Exception e) {
                                    HiddenGems.LOGGER.error(String.format("[Hidden Gems] Failed to register dimension %s.", dimension.name));
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
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            MinecraftClient.getInstance().getResourcePackManager().registerProvider(new AddonResourcePackCreator());
    }

    private static void fillDefaultConfigs() {
        MATERIALS_DIRECTORY.mkdirs();
    }

}
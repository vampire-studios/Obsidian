package io.github.vampirestudios.obsidian.configPack;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.BiomeUtils;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.SimpleBowItem;
import io.github.vampirestudios.obsidian.api.SimpleCrossbowItem;
import io.github.vampirestudios.obsidian.api.SimpleTridentItem;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelper;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.cauldronTypes.CauldronType;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import io.github.vampirestudios.obsidian.api.obsidian.currency.Currency;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.BreathableComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.CollisionBoxComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.HealthComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.MovementComponent;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import io.github.vampirestudios.obsidian.api.obsidian.item.ShieldItem;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import io.github.vampirestudios.obsidian.api.obsidian.potion.Potion;
import io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.utils.EntityRegistryBuilder;
import io.github.vampirestudios.obsidian.utils.EntityUtils;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import io.github.vampirestudios.obsidian.utils.Utils;
import io.github.vampirestudios.vampirelib.api.ShieldRegistry;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Property;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ConfigHelper {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Obsidian"));
    public static final File OBSIDIAN_ADDON_DIRECTORY = new File(FabricLoader.getInstance().getGameDirectory(), "obsidian_addons");
    public static final List<IAddonPack> OBSIDIAN_ADDONS = new ArrayList<>();
    public static RegistryHelper REGISTRY_HELPER;
    public static final int PACK_VERSION = 2;

    public static List<io.github.vampirestudios.obsidian.api.obsidian.item.Item> ITEMS = new ArrayList<>();
    public static List<FoodItem> FOODS = new ArrayList<>();
    public static List<WeaponItem> WEAPONS = new ArrayList<>();
    public static List<RangedWeaponItem> RANGED_WEAPONS = new ArrayList<>();
    public static List<io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem> TOOLS = new ArrayList<>();
    public static List<Block> BLOCKS = new ArrayList<>();
    public static List<Potion> POTIONS = new ArrayList<>();
    public static List<Command> COMMANDS = new ArrayList<>();
    public static List<StatusEffect> STATUS_EFFECTS = new ArrayList<>();
    public static List<Enchantment> ENCHANTMENTS = new ArrayList<>();
    public static List<io.github.vampirestudios.obsidian.api.obsidian.ItemGroup> ITEM_GROUPS = new ArrayList<>();
    public static List<Entity> ENTITIES = new ArrayList<>();
    public static List<io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem> ARMORS = new ArrayList<>();
    public static List<CauldronType> CAULDRON_TYPES = new ArrayList<>();
    public static List<ShieldItem> SHIELDS = new ArrayList<>();

    public static void loadDefaultObsidianAddons() {
        if (!OBSIDIAN_ADDON_DIRECTORY.exists())
            createObsidianAddonsFolder();
    }

    public static void register(File file) {
        if (file.isDirectory()) {
            try {
                File packInfoFile = new File(file, "addon.info.pack");
                if (packInfoFile.exists()) {
                    ObsidianAddonInfo obsidianAddonInfo = Obsidian.GSON.fromJson(new FileReader(packInfoFile), ObsidianAddonInfo.class);
                    ObsidianAddon obsidianAddon = new ObsidianAddon(obsidianAddonInfo, file);
                    if (!OBSIDIAN_ADDONS.contains(obsidianAddon) && obsidianAddon.getConfigPackInfo().addonVersion == PACK_VERSION) {
                        OBSIDIAN_ADDONS.add(obsidianAddon);
                        Obsidian.LOGGER.info(String.format("[Obsidian] Registering obsidian addon: %s", obsidianAddon.getConfigPackInfo().displayName));
                    } else {
                        Obsidian.LOGGER.info(String.format("[Obsidian] Found incompatible obsidian addon: %s with a version of %d", obsidianAddon.getConfigPackInfo().displayName, obsidianAddon.getConfigPackInfo().addonVersion));
                    }
                }
            } catch (Exception e) {
                Obsidian.LOGGER.error("[Obsidian] Failed to load obsidian addon!", e);
            }
        } else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".zip")) {
            try (ZipFile zipFile = new ZipFile(file)) {
                ZipEntry packInfoEntry = zipFile.getEntry("addon.info.pack");
                if (packInfoEntry != null) {
                    ObsidianAddonInfo obsidianAddonInfo = Obsidian.GSON.fromJson(new InputStreamReader(zipFile.getInputStream(packInfoEntry)), ObsidianAddonInfo.class);
                    ObsidianAddon obsidianAddon = new ObsidianAddon(obsidianAddonInfo);
                    if (!OBSIDIAN_ADDONS.contains(obsidianAddon) && obsidianAddon.getConfigPackInfo().addonVersion == PACK_VERSION) {
                        OBSIDIAN_ADDONS.add(obsidianAddon);
                        Obsidian.LOGGER.info(String.format("[Obsidian] Registering obsidian addon: %s", obsidianAddon.getConfigPackInfo().displayName));
                    } else {
                        Obsidian.LOGGER.info(String.format("[Obsidian] Found incompatible obsidian addon: %s with a version of %d", obsidianAddon.getConfigPackInfo().displayName, obsidianAddon.getConfigPackInfo().addonVersion));
                    }
                }
            } catch (Exception e) {
                Obsidian.LOGGER.error("[Obsidian] Failed to load obsidian addon!", e);
            }
        }
    }

    public static void loadObsidianAddons() {
        try {
            FabricLoader.getInstance().getEntrypoints("obsidian:obsidian_packs", IAddonPack.class).forEach(supplier -> {
                try {
                    OBSIDIAN_ADDONS.add(supplier);
                    Obsidian.LOGGER.info(String.format("[Obsidian] Registering an obsidian addon: %s from an entrypoint",
                            supplier.getConfigPackInfo().displayName));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
            for (File file : Objects.requireNonNull(OBSIDIAN_ADDON_DIRECTORY.listFiles())) {
                // Load Packs
                register(file);
            }
            String moduleText;
            if (OBSIDIAN_ADDONS.size() > 1) {
                moduleText = "Loading %d obsidian addons:";
            } else {
                moduleText = "Loading %d obsidian addon:";
            }

            Obsidian.LOGGER.info(String.format("[Obsidian] " + moduleText, OBSIDIAN_ADDONS.size()));

            for(IAddonPack pack : OBSIDIAN_ADDONS) {
                Obsidian.LOGGER.info(String.format(" - %s", pack.getConfigPackInfo().displayName));

                String modId = pack.getConfigPackInfo().namespace;
                String path = OBSIDIAN_ADDON_DIRECTORY.getPath() + "/" + pack.getConfigPackInfo().folderName + "/content/" + pack.getConfigPackInfo().namespace;
                System.out.println(path);
                REGISTRY_HELPER = RegistryHelper.createRegistryHelper(modId);

                try {
                    parseItemGroup(path);
                    parseBlock(modId, path);
                    parseBasicItems(path);
                    parseArmor(path);
                    parseTools(path);
                    parseRangedWeapons(path);
                    parseWeapons(path);
                    parseFood(path);
                    parsePotions(path);
                    parseCommands(path);
                    parseEnchantments(path);
                    parseStatusEffects(path);
                    parseEntities(path);
                    parseCurrencies(path);
                    parseCauldronTypes(path);
                    parseShields(path);
//                    parseElytras(path);
//                    parseVillagerProfessions(path);
//                    parseVillagerBiomeType(path);
//                    parseVillagerTrades(path);
//                    parseFluids(path);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

                /*ObsidianAddon addon = (ObsidianAddon) pack;
                try {
                    FabricLoader loader = FabricLoader.getInstance();
                    Method addMod = net.fabricmc.loader.FabricLoader.class.getDeclaredMethod("addMod", ModCandidate.class);
                    addMod.setAccessible(true);
                    ModCandidate candidate = new ModCandidate(new ObsidianAddonModMetadata(addon), UrlUtil.asUrl(addon.getFile()), 0, false);
                    addMod.invoke(loader, candidate);
                    Optional<ModContainer> optional = loader.getModContainer(addon.getConfigPackInfo().namespace);
                    if(optional.isPresent()) {
                        Method setupRootPath = net.fabricmc.loader.ModContainer.class.getDeclaredMethod("setupRootPath");
                        setupRootPath.setAccessible(true);
                        setupRootPath.invoke(optional.get());
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }*/

//                ResourceManagerHelperImpl.registerBuiltinResourcePack(new Identifier("obsidian", pack.getConfigPackInfo().namespace), pack.getConfigPackInfo().namespace, FabricLoader.getInstance().getModContainer(addon.getConfigPackInfo().namespace).get(), ResourcePackActivationType.ALWAYS_ENABLED);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.exit(0);
        }
    }

    private static void createObsidianAddonsFolder() {
        OBSIDIAN_ADDON_DIRECTORY.mkdirs();
    }

    public static BlockState getState(net.minecraft.block.Block block, Map<String, String> jsonProperties) {
        BlockState blockstate = block.getDefaultState();
        Collection<Property<?>> properties = blockstate.getProperties();
        for(Property property : properties) {
            String propertyName = property.getName();
            if(jsonProperties.containsKey(propertyName)) {
                String valueName = jsonProperties.get(propertyName);
                Optional valueOpt = property.parse(valueName);
                if(valueOpt.isPresent()) {
                    Comparable value = (Comparable) valueOpt.get();
                    blockstate = blockstate.with(property, value);
                } else {
                    System.err.printf("Property[%s=%s] doesn't exist for %s%n", propertyName, valueName, block.toString());
                }
                jsonProperties.remove(propertyName);
            }
        }
        if(!jsonProperties.isEmpty()) {
            Joiner joiner = Joiner.on(", ");
            System.err.printf("The following properties do not exist in %s: %s%n", block.toString(), joiner.join(jsonProperties.keySet()));
        }
        return blockstate;
    }

    private static void parseItemGroup(String path) throws FileNotFoundException {
        if (Paths.get(path, "item_groups").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "item_groups").toFile().listFiles())) {
                if (file.isFile()) {
                    io.github.vampirestudios.obsidian.api.obsidian.ItemGroup itemGroup = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.ItemGroup.class);
                    try {
                        if(itemGroup == null) continue;
                        ItemGroup itemGroup1 = FabricItemGroupBuilder.create(itemGroup.name.id)
                                .icon(() -> new ItemStack(Registry.ITEM.get(itemGroup.icon)))
                                .build();
                        Registry.register(Obsidian.ITEM_GROUP_REGISTRY, itemGroup.name.id, itemGroup1);
                        register(ITEM_GROUPS, "block", itemGroup.name.id.toString(), itemGroup);
                    } catch (Exception e) {
                        failedRegistering("item group", itemGroup.name.id.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseCauldronTypes(String path) throws FileNotFoundException {
        if (Paths.get(path, "cauldron_types").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "cauldron_types").toFile().listFiles())) {
                if (file.isFile()) {
                    CauldronType cauldronType = Obsidian.GSON.fromJson(new FileReader(file), CauldronType.class);
                    try {
                        if(cauldronType == null) continue;
                        CauldronBehavior cauldronBehavior = (state, world, pos, player, hand, stack) -> {
                            BlockState blockState = getState(Registry.BLOCK.get(cauldronType.blockstate.block), cauldronType.blockstate.properties);
                            return CauldronBehavior.fillCauldron(world, pos, player, hand, stack, blockState, Registry.SOUND_EVENT.get(cauldronType.sound_event));
                        };
                        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.put(Registry.ITEM.get(cauldronType.item), cauldronBehavior);
                        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.put(Registry.ITEM.get(cauldronType.item), cauldronBehavior);
                        CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.put(Registry.ITEM.get(cauldronType.item), cauldronBehavior);
                        CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR.put(Registry.ITEM.get(cauldronType.item), cauldronBehavior);
                        register(CAULDRON_TYPES, "cauldron_type", cauldronType.name, cauldronType);
                    } catch (Exception e) {
                        failedRegistering("cauldron_types", cauldronType.name, e);
                    }
                }
            }
        }
    }

    private static void parseShields(String path) throws FileNotFoundException {
        if (Paths.get(path, "shields").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "shields").toFile().listFiles())) {
                if (file.isFile()) {
                    ShieldItem shieldItem = Obsidian.GSON.fromJson(new FileReader(file), ShieldItem.class);
                    try {
                        if(shieldItem == null) continue;
                        ShieldItemImpl shieldItemImpl = new ShieldItemImpl(shieldItem, new Item.Settings().group(shieldItem.information.getItemGroup()));
                        REGISTRY_HELPER.registerItem(shieldItemImpl, shieldItem.information.name.id.getPath());
                        ShieldRegistry.INSTANCE.add(shieldItemImpl);
                        FabricModelPredicateProviderRegistry.register(shieldItemImpl, Utils.appendToPath(shieldItem.information.name.id, "_blocking"), (stack, world, entity, seed) ->
                                entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
                        register(SHIELDS, "shield", shieldItem.information.name.id.toString(), shieldItem);
                    } catch (Exception e) {
                        failedRegistering("shield", shieldItem.information.name.id.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseBlock(String modId, String path) throws FileNotFoundException {
        if (Paths.get(path, "blocks").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "blocks").toFile().listFiles())) {
                if (file.isFile()) {
                    Block block = Obsidian.GSON.fromJson(new FileReader(file), Block.class);
                    try {
                        FabricBlockSettings blockSettings = FabricBlockSettings.of(block.information.getMaterial()).sounds(block.information.getBlockSoundGroup())
                                .strength(block.information.destroy_time, block.information.explosion_resistance).drops(block.information.drop)
                                .collidable(block.information.collidable).slipperiness(block.information.slipperiness).emissiveLighting((state, world, pos) ->
                                        block.information.is_emissive).nonOpaque();
                        if (block.information.dynamicBounds) {
                            blockSettings.dynamicBounds();
                        }
                        if (block.information.randomTicks) {
                            blockSettings.ticksRandomly();
                        }
                        if (block.information.translucent) {
                            blockSettings.nonOpaque();
                        }
                        if (block.information.is_bouncy) {
                            blockSettings.jumpVelocityMultiplier(block.information.jump_velocity_modifier);
                        }
                        if (block.information.is_light_block) {
                            blockSettings.lightLevel(value -> block.information.luminance);
                        }
                        net.minecraft.block.Block blockImpl;
                        if(block.additional_information != null) {
                            if(block.additional_information.rotatable) {
                                blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new FacingBlockImpl(block, blockSettings), block.information.name.id.getPath());
                            } else if(block.additional_information.horizontal_rotatable) {
                                blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new HorizontalFacingBlockImpl(block, blockSettings), block.information.name.id.getPath());
                            } else if(block.additional_information.pillar) {
                                blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new PillarBlockImpl(block, blockSettings), block.information.name.id.getPath());
                            } else if(block.additional_information.path) {
                                blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new PathBlockImpl(blockSettings, block), block.information.name.id.getPath());
                            } else if(block.additional_information.lantern) {
                                blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new LanternBlock(blockSettings), block.information.name.id.getPath());
                            } else if(block.additional_information.barrel) {
                                blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new BarrelBlock(blockSettings), block.information.name.id.getPath());
                            } else if(block.additional_information.leaves) {
                                blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new LeavesBaseBlock(), block.information.name.id.getPath());
                            } else if(block.additional_information.chains) {
                                blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new ChainBlock(blockSettings), block.information.name.id.getPath());
                            } else if(block.additional_information.cake_like) {
                                blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new CakeBlockImpl(block), block.information.name.id.getPath());
                            } else {
                                blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new BlockImpl(block, blockSettings), block.information.name.id.getPath());
                            }
                        } else {
                            blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new BlockImpl(block, blockSettings), block.information.name.id.getPath());
                        }
                        Item.Settings settings = new Item.Settings().group(block.information.getItemGroup());
                        if (block.food_information != null) {
                            FoodComponent foodComponent = block.food_information.getBuilder().build();
                            settings.food(foodComponent);
                        }
                        if (block.information.fireproof) {
                            settings.fireproof();
                        }
                        REGISTRY_HELPER.registerItem(new CustomBlockItem(block, blockImpl, settings), block.information.name.id.getPath());
                        net.minecraft.block.Block finalBlockImpl = blockImpl;

                        if (block.ore_information != null) {
                            RuleTest test;
                            if (block.ore_information.test_type.equals("tag")) {
                                Tag<net.minecraft.block.Block> tag = BlockTags.getTagGroup().getTag(block.ore_information.target_state.block);
                                test = new TagMatchRuleTest(tag == null ? BlockTags.BASE_STONE_OVERWORLD : tag);
                            } else if (block.ore_information.test_type.equals("blockstate")) {
                                test = new BlockStateMatchRuleTest(getState(Registry.BLOCK.get(block.ore_information.target_state.block), block.ore_information.target_state.properties));
                            } else {
                                test = new BlockMatchRuleTest(Registry.BLOCK.get(block.ore_information.target_state.block));
                            }
                            ConfiguredFeature<?, ?> feature = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Utils.appendToPath(block.information.name.id, "_ore_feature"),
                                    Feature.ORE.configure(
                                            new OreFeatureConfig(
                                                    test,
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
                                    ).rangeOf(block.ore_information.config.minimum, block.ore_information.config.maximum).spreadHorizontally().repeat(20));
                            BuiltinRegistries.BIOME.forEach(biome -> {
                                if (block.ore_information.biomes != null) {
                                    for (String biome2 : block.ore_information.biomes) {
                                        if (BuiltinRegistries.BIOME.getId(biome).toString().equals(biome2)) {
                                            BiomeUtils.addFeatureToBiome(biome, GenerationStep.Feature.UNDERGROUND_ORES, feature);
                                        }
                                    }
                                } else {
                                    BiomeUtils.addFeatureToBiome(biome, GenerationStep.Feature.UNDERGROUND_ORES, feature);
                                }
                            });
                        }
                        Artifice.registerDataPack(String.format("%s:%s_data", block.information.name.id.getNamespace(), block.information.name.id.getPath()), serverResourcePackBuilder ->
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
                        );
                        if (block.additional_information != null) {
                            if (block.additional_information.slab) {
                                REGISTRY_HELPER.registerBlock(new SlabImpl(block),
                                        Utils.appendToPath(block.information.name.id, "_slab").getPath(), ItemGroup.BUILDING_BLOCKS);
                                Artifice.registerDataPack(String.format("%s:%s_slab_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
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
                                REGISTRY_HELPER.registerBlock(new StairsImpl(block), new Identifier(modId, block.information.name.id.getPath() + "_stairs").getPath(),
                                        ItemGroup.BUILDING_BLOCKS);
                                Artifice.registerDataPack(String.format("%s:%s_stairs_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
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
                                REGISTRY_HELPER.registerBlock(new FenceImpl(block),
                                        new Identifier(modId, block.information.name.id.getPath() + "_fence").getPath(), ItemGroup.DECORATIONS);
                                Artifice.registerDataPack(String.format("%s:%s_fence_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
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
                                REGISTRY_HELPER.registerBlock(new FenceGateImpl(block),
                                        Utils.appendToPath(block.information.name.id, "_fence_gate").getPath(), ItemGroup.REDSTONE);
                                Artifice.registerDataPack(String.format("%s:%s_fence_gate_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
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
                                REGISTRY_HELPER.registerBlock(new WallImpl(block),
                                        Utils.appendToPath(block.information.name.id, "_wall").getPath(), ItemGroup.DECORATIONS);
                                Artifice.registerDataPack(String.format("%s:%s_wall_data", block.information.name.id.getPath(), block.information.name.id.getPath()), serverResourcePackBuilder ->
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
                        register(BLOCKS, "block", block.information.name.id.toString(), block);
                    } catch (Exception e) {
                        failedRegistering("block", block.information.name.id.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseBasicItems(String path) throws FileNotFoundException {
        if (Paths.get(path, "items").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "items").toFile().listFiles())) {
                if (file.isFile()) {
                    io.github.vampirestudios.obsidian.api.obsidian.item.Item item = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
                    try {
                        RegistryUtils.registerItem(new ItemImpl(item, new Item.Settings().group(item.information.getItemGroup())
                                .maxCount(item.information.max_count)), item.information.name.id);
                        register(ITEMS, "item", item.information.name.id.toString(), item);
                    } catch (Exception e) {
                        failedRegistering("item", item.information.name.id.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseArmor(String path) throws FileNotFoundException {
        if (Paths.get(path, "items", "armor").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "items", "armor").toFile().listFiles())) {
                if (file.isFile()) {
                    io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem armor = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
                    try {
                        CustomArmorMaterial material = new CustomArmorMaterial(armor.material);
                        RegistryUtils.registerItem(new ArmorItemImpl(material, armor, new Item.Settings()
                                        .group(armor.information.getItemGroup()).maxCount(armor.information.max_count)),
                                armor.information.name.id);
                        register(ARMORS, "armor", armor.information.name.id.toString(), armor);
                    } catch (Exception e) {
                        failedRegistering("armor", armor.information.name.id.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseTools(String path) throws FileNotFoundException {
        if (Paths.get(path, "items", "tools").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "items", "tools").toFile().listFiles())) {
                if (file.isFile()) {
                    io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem tool = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem.class);
                    try {
                        CustomToolMaterial material = new CustomToolMaterial(tool.material);
                        switch (tool.toolType) {
                            case "pickaxe":
                                RegistryUtils.registerItem(new PickaxeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                .group(tool.information.getItemGroup()).maxCount(tool.information.max_count)),
                                        tool.information.name.id);
                                break;
                            case "shovel":
                                RegistryUtils.registerItem(new ShovelItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                .group(tool.information.getItemGroup()).maxCount(tool.information.max_count)),
                                        tool.information.name.id);
                                break;
                            case "hoe":
                                RegistryUtils.registerItem(new HoeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                .group(tool.information.getItemGroup()).maxCount(tool.information.max_count)),
                                        tool.information.name.id);
                                break;
                            case "axe":
                                RegistryUtils.registerItem(new AxeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, new Item.Settings()
                                                .group(tool.information.getItemGroup()).maxCount(tool.information.max_count)),
                                        tool.information.name.id);
                                break;
                            case "bow":
                                RegistryUtils.registerItem(new SimpleBowItem(new Item.Settings().group(tool.information.getItemGroup())
                                        .maxCount(tool.information.max_count)), tool.information.name.id);
                                break;
                            case "crossbow":
                                RegistryUtils.registerItem(new SimpleCrossbowItem(new Item.Settings().group(tool.information.getItemGroup())
                                        .maxCount(tool.information.max_count)), tool.information.name.id);
                                break;
                            case "trident":
                                RegistryUtils.registerItem(new SimpleTridentItem(new Item.Settings().group(tool.information.getItemGroup())
                                        .maxCount(tool.information.max_count)), tool.information.name.id);
                                break;
                        }
                        register(TOOLS, "tool", tool.information.name.id.toString(), tool);
                    } catch (Exception e) {
                        failedRegistering("tool", tool.information.name.id.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseRangedWeapons(String path) throws FileNotFoundException {
        if (Paths.get(path, "items", "weapons", "ranged").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "items", "weapons", "ranged").toFile().listFiles())) {
                if (file.isFile()) {
                    RangedWeaponItem rangedWeapon = Obsidian.GSON.fromJson(new FileReader(file), RangedWeaponItem.class);
                    try {
                        switch (rangedWeapon.weapon_type) {
                            case "bow":
                                RegistryUtils.registerItem(new SimpleBowItem(new Item.Settings().group(rangedWeapon.information.getItemGroup())
                                        .maxCount(rangedWeapon.information.max_count)), rangedWeapon.information.name.id);
                                break;
                            case "crossbow":
                                RegistryUtils.registerItem(new SimpleCrossbowItem(new Item.Settings().group(rangedWeapon.information.getItemGroup())
                                        .maxCount(rangedWeapon.information.max_count)), rangedWeapon.information.name.id);
                                break;
                            case "trident":
                                RegistryUtils.registerItem(new SimpleTridentItem(new Item.Settings().group(rangedWeapon.information.getItemGroup())
                                        .maxCount(rangedWeapon.information.max_count)), rangedWeapon.information.name.id);
                                break;
                        }
                        register(RANGED_WEAPONS, "ranged_weapon", rangedWeapon.information.name.id.toString(), rangedWeapon);
                    } catch (Exception e) {
                        failedRegistering("ranged_weapon", rangedWeapon.information.name.id.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseWeapons(String path) throws FileNotFoundException {
        if (Paths.get(path, "items", "weapons").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "items", "weapons").toFile().listFiles())) {
                if (file.isFile()) {
                    WeaponItem weapon = Obsidian.GSON.fromJson(new FileReader(file), WeaponItem.class);
                    try {
                        CustomToolMaterial material = new CustomToolMaterial(weapon.material);
                        RegistryUtils.registerItem(new MeleeWeaponImpl(weapon, material, weapon.attackDamage, weapon.attackSpeed, new Item.Settings()
                                .group(weapon.information.getItemGroup())
                                .maxCount(weapon.information.max_count)), weapon.information.name.id);
                        register(WEAPONS, "weapon", weapon.information.name.id.toString(), weapon);
                    } catch (Exception e) {
                        failedRegistering("weapon", weapon.information.name.id.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseFood(String path) throws FileNotFoundException {
        if (Paths.get(path, "items", "food").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "items", "food").toFile().listFiles())) {
                if (file.isFile()) {
                    FoodItem foodItem = Obsidian.GSON.fromJson(new FileReader(file), FoodItem.class);
                    try {
                        FoodComponent foodComponent = foodItem.food_information.getBuilder().build();
                        Registry.register(Registry.ITEM, foodItem.information.name.id, new ItemImpl(foodItem, new Item.Settings()
                                .group(foodItem.information.getItemGroup())
                                .maxCount(foodItem.information.max_count)
                                .maxDamage(foodItem.information.use_duration)
                                .food(foodComponent)));
                        register(FOODS, "food", foodItem.information.name.id.toString(), foodItem);
                    } catch (Exception e) {
                        failedRegistering("food", foodItem.information.name.id.toString(), e);
                    }
                }
            }
        }
    }

    private static void parsePotions(String path) throws FileNotFoundException {
        if (Paths.get(path, "potions").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "potions").toFile().listFiles())) {
                if (file.isFile()) {
                    Potion potion = Obsidian.GSON.fromJson(new FileReader(file), Potion.class);
                    try {
                        if (potion == null) continue;
                        Registry.register(Registry.POTION, potion.name,
                                new net.minecraft.potion.Potion(new StatusEffectInstance(potion.getEffectType(), potion.getEffects().duration * 20, potion.getEffects().amplifier)));
                        register(POTIONS, "potion", potion.name.toString(), potion);
                    } catch (Exception e) {
                        failedRegistering("potion", potion.name.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseCommands(String path) throws FileNotFoundException {
        if (Paths.get(path, "commands").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "commands").toFile().listFiles())) {
                if (file.isFile()) {
                    Command command = Obsidian.GSON.fromJson(new FileReader(file), Command.class);
                    try {
                        if(command == null) continue;
                        // Using a lambda
                        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
                            // This command will be registered regardless of the server being dedicated or integrated
                            CommandImpl.register(command, dispatcher);
                        });
                        register(COMMANDS, "command", command.name, command);
                    } catch (Exception e) {
                        failedRegistering("command", command.name, e);
                    }
                }
            }
        }
    }

    private static void parseEnchantments(String path) throws FileNotFoundException {
        if (Paths.get(path, "enchantments").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "enchantments").toFile().listFiles())) {
                if (file.isFile()) {
                    Enchantment enchantment = Obsidian.GSON.fromJson(new FileReader(file), Enchantment.class);
                    try {
                        if(enchantment == null) continue;
                        Registry.register(Registry.ENCHANTMENT, enchantment.name.id, new EnchantmentImpl(enchantment));
                        register(ENCHANTMENTS, "enchantment", enchantment.name.id.getPath(), enchantment);
                    } catch (Exception e) {
                        failedRegistering("enchantment", enchantment.name.id.getPath(), e);
                    }
                }
            }
        }
    }

    private static void parseStatusEffects(String path) throws FileNotFoundException {
        if (Paths.get(path, "status_effects").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "status_effects").toFile().listFiles())) {
                if (file.isFile()) {
                    StatusEffect statusEffect = Obsidian.GSON.fromJson(new FileReader(file), StatusEffect.class);
                    try {
                        if(statusEffect == null) continue;
                        String color1 = statusEffect.color.replace("#", "").replace("0x", "");
                        Registry.register(Registry.STATUS_EFFECT, statusEffect.name.id, new StatusEffectImpl(statusEffect.getStatusEffectType(), Integer.parseInt(color1, 16)));
                        register(STATUS_EFFECTS, "status effect", statusEffect.name.translated.get("en_us"), statusEffect);
                    } catch (Exception e) {
                        failedRegistering("status effect", statusEffect.name.translated.get("en_us"), e);
                    }
                }
            }
        }
    }

    private static void parseEntities(String path) throws FileNotFoundException {
        if (Paths.get(path, "entities").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "entities").toFile().listFiles())) {
                if (file.isFile()) {
                    JsonObject entityJson = Obsidian.GSON.fromJson(new FileReader(file), JsonObject.class);
                    Entity entity = Obsidian.GSON.fromJson(entityJson, Entity.class);
                    try {
                        if(entity == null) continue; // TODO: add error log here
                        String baseColor = entity.information.spawn_egg.base_color.replace("#", "").replace("0x", "");
                        String overlayColor = entity.information.spawn_egg.overlay_color.replace("#", "").replace("0x", "");
                        entity.components = new HashMap<>();
                        JsonObject components = JsonHelper.getObject(entityJson, "components");
                        for (Map.Entry<String, JsonElement> entry : components.entrySet()) {
                            Identifier identifier = new Identifier(entry.getKey());
                            Class<? extends Component> componentClass = Obsidian.ENTITY_COMPONENT_REGISTRY.getOrEmpty(identifier).orElseThrow(() -> new JsonParseException("Unknown component \"" + entry.getKey() + "\" defined in entity json"));

                            entity.components.put(identifier.toString(), Obsidian.GSON.fromJson(entry.getValue(), componentClass));
                        }

                        CollisionBoxComponent collisionBoxComponent = null;
                        Component c = entity.components.get("minecraft:collision_box");
                        if (c instanceof CollisionBoxComponent) {
                            collisionBoxComponent = (CollisionBoxComponent) c;
                        }
                        HealthComponent healthComponent = null;
                        c = entity.components.get("minecraft:health");
                        if (c instanceof HealthComponent) {
                            healthComponent = (HealthComponent) c;
                        }

                        MovementComponent movementComponent = null;
                        c = entity.components.get("minecraft:movement");
                        if (c instanceof MovementComponent) {
                            movementComponent = (MovementComponent) c;
                        }

                        BreathableComponent breathableComponent = null;
                        c = entity.components.get("minecraft:breathable");
                        if (c instanceof BreathableComponent) {
                            breathableComponent = (BreathableComponent) c;
                        }

                        assert collisionBoxComponent != null;
                        assert movementComponent != null;
                        HealthComponent finalHealthComponent = healthComponent;
                        BreathableComponent finalBreathableComponent = breathableComponent;
                        EntityType<EntityImpl> entityType = EntityRegistryBuilder.<EntityImpl>createBuilder(entity.information.identifier)
                                .entity((type, world) -> new EntityImpl(type, world, entity, finalHealthComponent.value, finalBreathableComponent))
                                .category(entity.entity_components.getCategory())
                                .dimensions(EntityDimensions.fixed(collisionBoxComponent.width, collisionBoxComponent.height))
                                .summonable(entity.information.summonable)
                                .hasEgg(entity.information.spawnable)
                                .egg(Integer.parseInt(baseColor, 16), Integer.parseInt(overlayColor, 16))
                                .build();
                        FabricDefaultAttributeRegistry.register(entityType, EntityUtils.createGenericEntityAttributes(finalHealthComponent.max, movementComponent.value));
                        register(ENTITIES, "entity", entity.information.identifier.toString(), entity);
                    } catch (Exception e) {
                        failedRegistering("entity", entity.information.identifier.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseCurrencies(String path) throws FileNotFoundException {
        if (Paths.get(path, "currency").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "currency").toFile().listFiles())) {
                if (file.isFile()) {
                    Currency currency = Obsidian.GSON.fromJson(new FileReader(file), Currency.class);
                    try {
                        if(currency == null) continue;
                        /*PlayerJoinCallback.EVENT.register(player -> {
                            Scoreboard scoreboard = player.getScoreboard();
                            if (!scoreboard.containsObjective(currency.name.toLowerCase())) {
                                scoreboard.addObjective(currency.name.toLowerCase(), ScoreboardCriterion.DUMMY, new LiteralText(currency.name), ScoreboardCriterion.RenderType.INTEGER);
                            }
                        });*/
                        Obsidian.LOGGER.info(String.format("Registered a currency called %s", currency.name));
                    } catch (Exception e) {
                        failedRegistering("currency", currency.name, e);
                    }
                }
            }
        }
    }

    private static <T> void register(List<T> list, String type, String name, T idk) {
        list.add(idk);
        Obsidian.LOGGER.info("[Obsidian] Registered a {} {}.", type, name);
    }

    private static void failedRegistering(String type, String name, Exception e) {
        e.printStackTrace();
        Obsidian.LOGGER.error("[Obsidian] Failed to register {} {}.", type, name);
    }

}

package io.github.vampirestudios.obsidian.configPack;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.BiomeUtils;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.PlayerJoinCallback;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.Keybinding;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import io.github.vampirestudios.obsidian.api.obsidian.currency.Currency;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.CollisionBoxComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.HealthComponent;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import io.github.vampirestudios.obsidian.api.obsidian.particle.Particle;
import io.github.vampirestudios.obsidian.api.obsidian.potion.Potion;
import io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect;
import io.github.vampirestudios.obsidian.api.obsidian.template.BlockTemplate;
import io.github.vampirestudios.obsidian.minecraft.*;
import io.github.vampirestudios.obsidian.utils.*;
import io.github.vampirestudios.vampirelib.utils.registry.RegistryHelper;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.recipe.Ingredient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Property;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.BlockStateMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
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

public class ConfigHelperClient {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Hidden_Gems"));
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, (SimpleStringDeserializer<?>) Identifier::new)
            .setPrettyPrinting().create();
    public static final File MATERIALS_DIRECTORY = new File(FabricLoader.getInstance().getGameDirectory(), "addon_packs");
    public static final List<IAddonPack> ADDON_PACKS = new ArrayList<>();
    public static final List<IAddonPack> ENABLED_ADDON_PACKS = new ArrayList<>();
    public static RegistryHelper REGISTRY_HELPER;

    public static List<io.github.vampirestudios.obsidian.api.obsidian.item.Item> items = new ArrayList<>();
    public static List<WeaponItem> weapons = new ArrayList<>();
    public static List<io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem> tools = new ArrayList<>();
    public static List<Block> blocks = new ArrayList<>();
    private static final List<Potion> potions = new ArrayList<>();
    private static final List<Command> commands = new ArrayList<>();
    private static final List<StatusEffect> statusEffects = new ArrayList<>();
    private static final List<Enchantment> enchantments = new ArrayList<>();
    private static final List<io.github.vampirestudios.obsidian.api.obsidian.ItemGroup> itemGroups = new ArrayList<>();
    public static List<io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem> armors = new ArrayList<>();
    public static Map<String, BlockTemplate> BLOCK_TEMPLATES = new HashMap<>();
    public static Map<String, io.github.vampirestudios.obsidian.api.obsidian.item.Item> ITEM_TEMPLATES = new HashMap<>();

    public static void loadDefault() {
        if (!MATERIALS_DIRECTORY.exists())
            fillDefaultConfigs();
    }

    public static void register(File file) {
        if (file.isDirectory()) {
            try {
                File packInfoFile = new File(file, "addon.info.pack");
                if (packInfoFile.exists()) {
                    ObsidianAddonInfo packInfo = GSON.fromJson(new FileReader(packInfoFile), ObsidianAddonInfo.class);
                    ObsidianAddon obsidianAddon = new ObsidianAddon(packInfo, file);
                    if (!ADDON_PACKS.contains(obsidianAddon)) {
                        ADDON_PACKS.add(obsidianAddon);
                    }
                    Obsidian.LOGGER.info(String.format("[Obsidian] Registering addon: %s", obsidianAddon.getConfigPackInfo().id));
                }
            } catch (Exception e) {
                Obsidian.LOGGER.error("[Obsidian] Failed to load addon pack.", e);
            }
        } else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".zip")) {
            try (ZipFile zipFile = new ZipFile(file)) {
                ZipEntry packInfoEntry = zipFile.getEntry("addon.info.pack");
                if (packInfoEntry != null) {
                    ObsidianAddonInfo packInfo = GSON.fromJson(new InputStreamReader(zipFile.getInputStream(packInfoEntry)), ObsidianAddonInfo.class);
                    ObsidianAddon obsidianAddon = new ObsidianAddon(packInfo);
                    if (!ADDON_PACKS.contains(obsidianAddon)) {
                        ADDON_PACKS.add(obsidianAddon);
                    }
                    Obsidian.LOGGER.info(String.format("[Obsidian] Registering addon: %s", obsidianAddon.getConfigPackInfo().id));
                }
            } catch (Exception e) {
                Obsidian.LOGGER.error("[Obsidian] Failed to load addon pack.", e);
            }
        }
    }

    public static void loadConfig() {
        try {
            FabricLoader.getInstance().getEntrypoints("obsidian:addon_packs", IAddonPack.class).forEach(supplier -> {
                try {
                    ADDON_PACKS.add(supplier);
                    Obsidian.LOGGER.info(String.format("[Obsidian] Registering addon: %s from an entrypoint",
                            supplier.getConfigPackInfo().id));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
            for (File file : Objects.requireNonNull(MATERIALS_DIRECTORY.listFiles())) {
                // Load Packs
                register(file);
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

                String modId = pack.getConfigPackInfo().namespace;
                String path = MATERIALS_DIRECTORY.getPath() + "/" + pack.getIdentifier().getPath() + "/content/" + pack.getConfigPackInfo().namespace;
                REGISTRY_HELPER = RegistryHelper.createRegistryHelper(modId);

                try {
                    parseItemGroup(path);
                    parseParticle(path);
                    parseBlock(pack, modId, path);
                    parseBasicItems(path);
                    parseArmor(path);
                    parseTools(path);
                    parseWeapons(path);
                    parseFood(path);
                    parsePotions(path);
                    parseKeybinding(path);
                    parseCommands(path);
                    parseEnchantments(path);
                    parseStatusEffects(path);
                    parseEntities(path);
                    parseCurrencies(path);
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
                    System.err.println(String.format("Property[%s=%s] doesn't exist for %s", propertyName, valueName, block.toString()));
                }
                jsonProperties.remove(propertyName);
            }
        }
        if(!jsonProperties.isEmpty()) {
            Joiner joiner = Joiner.on(", ");
            System.err.println(String.format("The following properties do not exist in %s: %s", block.toString(), joiner.join(jsonProperties.keySet())));
        }
        return blockstate;
    }

    private static void parseItemGroup(String path) throws FileNotFoundException {
        if (Paths.get(path, "item_groups").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "item_groups").toFile().listFiles())) {
                if (file.isFile()) {
                    io.github.vampirestudios.obsidian.api.obsidian.ItemGroup itemGroup = GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.ItemGroup.class);
                    try {
                        if(itemGroup == null) continue;
                        FabricItemGroupBuilder.create(itemGroup.name.id)
                                .icon(() -> new ItemStack(Registry.ITEM.get(itemGroup.icon)))
                                .build();
                        Artifice.registerAssetPack(String.format("obsidian:%s_item_assets", itemGroup.name.id.getPath()), clientResourcePackBuilder -> {
                            itemGroup.name.translated.forEach((languageId, name) -> {
                                clientResourcePackBuilder.addTranslations(new Identifier(itemGroup.name.id.getNamespace(), languageId), translationBuilder -> {
                                    translationBuilder.entry(String.format("itemGroup.%s.%s", itemGroup.name.id.getNamespace(), itemGroup.name.id.getPath()), name);
                                });
                            });
                        });
                        register(itemGroups, "block", itemGroup.name.id.toString(), itemGroup);
                    } catch (Exception e) {
                        failedRegistering("item group", itemGroup.name.id.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseParticle(String path) throws FileNotFoundException {
        if (Paths.get(path, "particles").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "particles").toFile().listFiles())) {
                if (file.isFile()) {
                    Particle particle = GSON.fromJson(new FileReader(file), Particle.class);
                    try {
                        if(particle == null) continue;
                        DefaultParticleType particleType = FabricParticleTypes.simple(particle.always_spawn);
                        Registry.register(Registry.PARTICLE_TYPE, particle.id, particleType);
                        ParticleFactoryRegistry.getInstance().register(particleType, fabricSpriteProvider ->
                                new ParticleImpl.Factory(particle, fabricSpriteProvider));
                        Artifice.registerAssetPack(Utils.appendToPath(particle.id, "_assets"), clientResourcePackBuilder -> {
                            clientResourcePackBuilder.addParticle(particle.id, particleBuilder -> particleBuilder.texture(particle.texture));
                        });
                        System.out.println("Registered a particle called " + particle.id.toString());
                    } catch (Exception e) {
                        failedRegistering("particle", particle.id.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseBlock(IAddonPack pack, String modId, String path) throws FileNotFoundException {
        if (Paths.get(path, "blocks").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "blocks").toFile().listFiles())) {
                if (file.isFile()) {
                    Block block = GSON.fromJson(new FileReader(file), Block.class);
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
                        if (block.information.is_bouncy) {
                            blockSettings.jumpVelocityMultiplier(block.information.jump_velocity_modifier);
                        }
                        if (block.information.is_light_block) {
                            blockSettings.lightLevel(value -> block.information.luminance);
                        }
                        net.minecraft.block.Block blockImpl = null;
                        if(block.additional_information != null) {
                            if(block.additional_information.rotatable) {
                                blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new HorizontalFacingBlockImpl(block, blockSettings), block.information.name.id.getPath());
                            }
                            if(block.additional_information.pillar) {
                                blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new PillarBlockImpl(block, blockSettings), block.information.name.id.getPath());
                            }
                            if(!block.additional_information.pillar && !block.additional_information.rotatable) {
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
                                    ).rangeOf(block.ore_information.config.maximum).spreadHorizontally().repeat(20));
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
                        Artifice.registerAssetPack(String.format("obsidian:%s_%s_assets", pack.getIdentifier().getPath(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
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
                        });
                        Artifice.registerDataPack(String.format("obsidian:%s_%s_data", pack.getIdentifier().getPath(), block.information.name.id.getPath()), serverResourcePackBuilder ->
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
                        register(blocks, "block", block.information.name.id.toString(), block);
                        if (block.additional_information != null) {
                            if (block.additional_information.slab) {
                                REGISTRY_HELPER.registerBlock(new SlabImpl(block),
                                        Utils.appendToPath(block.information.name.id, "_slab").getPath(), ItemGroup.BUILDING_BLOCKS);
                                Artifice.registerAssetPack(String.format("obsidian:%s_%s_slab_assets", pack.getIdentifier().getPath(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                                    block.information.name.translated.forEach((languageId, name) -> {
                                        clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                            translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_slab"),
                                                    name + " Slab");
                                        });
                                    });
                                });
                                Artifice.registerDataPack(String.format("obsidian:%s_%s_slab_data", pack.getIdentifier().getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
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
                                Artifice.registerAssetPack(String.format("obsidian:%s_%s_stairs_assets", pack.getIdentifier().getPath(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                                    block.information.name.translated.forEach((languageId, name) -> {
                                        clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                            translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_stairs"),
                                                    name + " Stairs");
                                        });
                                    });
                                });
                                Artifice.registerDataPack(String.format("obsidian:%s_%s_stairs_data", pack.getIdentifier().getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
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
                                Artifice.registerAssetPack(String.format("obsidian:%s_%s_fence_assets", pack.getIdentifier().getPath(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                                    block.information.name.translated.forEach((languageId, name) -> {
                                        clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                            translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence"),
                                                    name + " Fence");
                                        });
                                    });
                                });
                                Artifice.registerDataPack(String.format("obsidian:%s_%s_fence_data", pack.getIdentifier().getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
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
                                Artifice.registerAssetPack(String.format("obsidian:%s_%s_fence_gate_assets", pack.getIdentifier().getPath(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                                    block.information.name.translated.forEach((languageId, name) -> {
                                        clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                            translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence_gate"),
                                                    name + " Fence Gate");
                                        });
                                    });
                                });
                                Artifice.registerDataPack(String.format("obsidian:%s_%s_fence_gate_data", pack.getIdentifier().getPath(), block.information.name.id.getPath()), serverResourcePackBuilder -> {
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
                                Artifice.registerAssetPack(String.format("obsidian:%s_%s_wall_assets", pack.getIdentifier().getPath(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
                                    block.information.name.translated.forEach((languageId, name) -> {
                                        clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder -> {
                                            translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_wall"),
                                                    name + " Wall");
                                        });
                                    });
                                });
                                Artifice.registerDataPack(String.format("obsidian:%s_%s_wall_data", pack.getIdentifier().getPath(), block.information.name.id.getPath()), serverResourcePackBuilder ->
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
                    io.github.vampirestudios.obsidian.api.obsidian.item.Item item = GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
                    try {
                        RegistryUtils.registerItem(new ItemImpl(item, new Item.Settings().group(item.information.getItemGroup())
                                .maxCount(item.information.max_count)), item.information.name.id);
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
                        });
                        register(items, "item", item.information.name.id.toString(), item);
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
                    io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem armor = GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
                    try {
                        ArmorMaterial material = new ArmorMaterial() {
                            @Override
                            public int getDurability(EquipmentSlot slot) {
                                return armor.material.durability;
                            }

                            @Override
                            public int getProtectionAmount(EquipmentSlot slot) {
                                return armor.material.protection_amount;
                            }

                            @Override
                            public int getEnchantability() {
                                return armor.material.enchantability;
                            }

                            @Override
                            public SoundEvent getEquipSound() {
                                return Registry.SOUND_EVENT.get(armor.material.sound_event);
                            }

                            @Override
                            public Ingredient getRepairIngredient() {
                                return Ingredient.ofItems(Registry.ITEM.get(armor.material.repair_item));
                            }

                            @Override
                            public String getName() {
                                return armor.material.name;
                            }

                            @Override
                            public float getToughness() {
                                return armor.material.toughness;
                            }

                            @Override
                            public float getKnockbackResistance() {
                                return armor.material.knockback_resistance;
                            }
                        };
                        Item item = RegistryUtils.registerItem(new ArmorItemImpl(material, armor, new Item.Settings()
                                        .group(armor.information.getItemGroup()).maxCount(armor.information.max_count)),
                                armor.information.name.id);
//                        ArmorTextureRegistry.register((entity, stack, slot, secondLayer, suffix) ->
//                                armor.material.texture, item);
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
                        });
                        register(armors, "armor", armor.information.name.id.toString(), armor);
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
                    io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem tool = GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem.class);
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
                        }
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
                        });
                        register(tools, "tool", tool.information.name.id.toString(), tool);
                    } catch (Exception e) {
                        failedRegistering("tool", tool.information.name.id.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseWeapons(String path) throws FileNotFoundException {
        if (Paths.get(path, "items", "weapons").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "items", "weapons").toFile().listFiles())) {
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
                                .maxCount(weapon.information.max_count)), weapon.information.name.id);
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
                        });
                        register(weapons, "weapon", weapon.information.name.id.toString(), weapon);
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
                    FoodItem foodItem = GSON.fromJson(new FileReader(file), FoodItem.class);
                    try {
                        FoodComponent foodComponent = foodItem.food_information.getBuilder().build();
                        Registry.register(Registry.ITEM, foodItem.information.name.id, new ItemImpl(foodItem, new Item.Settings()
                                .group(foodItem.information.getItemGroup())
                                .maxCount(foodItem.information.max_count)
                                .maxDamage(foodItem.information.use_duration)
                                .food(foodComponent)));
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
                        });
                        register(items, "food item", foodItem.information.name.id.toString(), foodItem);
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
                    Potion potion = GSON.fromJson(new FileReader(file), Potion.class);
                    try {
                        if (potion == null) continue;
                        Registry.register(Registry.POTION, potion.name,
                                new net.minecraft.potion.Potion(new StatusEffectInstance(potion.getEffectType(), potion.getEffects().duration * 20, potion.getEffects().amplifier)));
                        register(potions, "potion", potion.name.toString(), potion);
                    } catch (Exception e) {
                        failedRegistering("potion", potion.name.toString(), e);
                    }
                }
            }
        }
    }

    private static void parseKeybinding(String path) throws FileNotFoundException {
        if (Paths.get(path, "keybindings").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "keybindings").toFile().listFiles())) {
                if (file.isFile()) {
                    Keybinding keyBinding = GSON.fromJson(new FileReader(file), Keybinding.class);
                    try {
                        if (keyBinding == null) continue;
                        KeyBindingHelper.registerKeyBinding(new KeybindingImpl(keyBinding));
                        register("keybinding", keyBinding.translationKey);
                    } catch (Exception e) {
                        failedRegistering("keybinding", keyBinding.translationKey, e);
                    }
                }
            }
        }
    }

    private static void parseCommands(String path) throws FileNotFoundException {
        if (Paths.get(path, "commands").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "commands").toFile().listFiles())) {
                if (file.isFile()) {
                    Command command = GSON.fromJson(new FileReader(file), Command.class);
                    try {
                        if(command == null) continue;
                        // Using a lambda
                        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
                            // This command will be registered regardless of the server being dedicated or integrated
                            CommandImpl.register(command, dispatcher);
                        });
                        register(commands, "command", command.name, command);
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
                    Enchantment enchantment = GSON.fromJson(new FileReader(file), Enchantment.class);
                    try {
                        if(enchantment == null) continue;
                        Registry.register(Registry.ENCHANTMENT, enchantment.id, new EnchantmentImpl(enchantment));
                        Artifice.registerAssetPack(Utils.appendToPath(enchantment.id, "_lang_assets"), clientResourcePackBuilder -> {
                            clientResourcePackBuilder.addTranslations(new Identifier(enchantment.id.getNamespace(), "en_us"), translationBuilder ->
                                    translationBuilder.entry(String.format("enchantment.%s.%s", enchantment.id.getNamespace(), enchantment.id.getPath()),
                                            enchantment.name));
                        });
                        register(enchantments, "enchantment", enchantment.name, enchantment);
                    } catch (Exception e) {
                        failedRegistering("enchantment", enchantment.name, e);
                    }
                }
            }
        }
    }

    private static void parseStatusEffects(String path) throws FileNotFoundException {
        if (Paths.get(path, "status_effects").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "status_effects").toFile().listFiles())) {
                if (file.isFile()) {
                    StatusEffect statusEffect = GSON.fromJson(new FileReader(file), StatusEffect.class);
                    try {
                        if(statusEffect == null) continue;
                        String color1 = statusEffect.color.replace("#", "").replace("0x", "");
                        Registry.register(Registry.STATUS_EFFECT, statusEffect.name.id, new StatusEffectImpl(statusEffect.getStatusEffectType(), Integer.parseInt(color1, 16)));
                        register(statusEffects, "status effect", statusEffect.name.translated.get("en_us"), statusEffect);
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
                    Entity entity = GSON.fromJson(new FileReader(file), Entity.class);
                    try {
                        if(entity == null) continue;
                        String baseColor = entity.information.spawn_egg.base_color.replace("#", "").replace("0x", "");
                        String overlayColor = entity.information.spawn_egg.overlay_color.replace("#", "").replace("0x", "");
                        CollisionBoxComponent collisionBoxComponent = (CollisionBoxComponent) entity.components.get(new Identifier("collision_box"));
                        HealthComponent healthComponent = (HealthComponent) entity.components.get(new Identifier("collision_box"));

                        EntityType<EntityImpl> entityType = EntityRegistryBuilder.<EntityImpl>createBuilder(entity.information.identifier)
                                .entity((type, world) -> new EntityImpl(type, world, entity, healthComponent.value))
                                .category(entity.entity_components.getCategory())
                                .dimensions(EntityDimensions.fixed(collisionBoxComponent.width, collisionBoxComponent.height))
                                .summonable(entity.information.summonable)
                                .hasEgg(entity.information.spawnable)
                                .egg(Integer.parseInt(baseColor, 16), Integer.parseInt(overlayColor, 16))
                                .build();
                        FabricDefaultAttributeRegistry.register(entityType, EntityUtils.createGenericEntityAttributes(healthComponent.max));
//                        EntityRendererRegistry.INSTANCE.register(entityType, (entityRenderDispatcher, context) -> new EntityImplRenderer(entityRenderDispatcher, entity));
                        System.out.printf("Registered an entity called %s%n", entity.information.identifier.toString());
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
                        failedRegistering("currency", currency.name, e);
                    }
                }
            }
        }
    }

    private static void parseBlockTemplates(String path) throws FileNotFoundException {
        if (Paths.get(path, "templates", "blocks").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "templates", "blocks").toFile().listFiles())) {
                if (file.isFile()) {
                    BlockTemplate blockTemplate = GSON.fromJson(new FileReader(file), BlockTemplate.class);
                    try {
                        if(blockTemplate == null) continue;
                        BLOCK_TEMPLATES.put(blockTemplate.name, blockTemplate);
                        Obsidian.LOGGER.info(String.format("Registered a block template called %s", blockTemplate.information.name.id));
                    } catch (Exception e) {
                        failedRegistering("block template", blockTemplate.information.name.id.toString(), e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static <T> void register(List<T> list, String type, String name, T idk) {
        list.add(idk);
        Obsidian.LOGGER.info("[Obsidian] Registered a {} {}.", type, name);
    }

    private static void register(String type, String name) {
        Obsidian.LOGGER.info("[Obsidian] Registered a {} {}.", type, name);
    }

    private static void failedRegistering(String type, String name, Exception e) {
        e.printStackTrace();
        Obsidian.LOGGER.error("[Obsidian] Failed to register {} {}.", type, name);
    }

}
package io.github.vampirestudios.obsidian.configPack;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.bedrock.BedrockAddon;
import io.github.vampirestudios.obsidian.api.bedrock.IBedrockAddon;
import io.github.vampirestudios.obsidian.api.bedrock.ManifestFile;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import io.github.vampirestudios.obsidian.api.obsidian.currency.Currency;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import io.github.vampirestudios.obsidian.api.obsidian.potion.Potion;
import io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect;
import io.github.vampirestudios.obsidian.minecraft.*;
import io.github.vampirestudios.obsidian.utils.EntityRegistryBuilder;
import io.github.vampirestudios.obsidian.utils.EntityUtils;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import io.github.vampirestudios.obsidian.utils.SimpleStringDeserializer;
import io.github.vampirestudios.vampirelib.utils.registry.RegistryHelper;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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

public class BedrockAddonLoader {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Obsidian: Bedrock"));
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, (SimpleStringDeserializer<?>) Identifier::new)
            .setPrettyPrinting().create();
    public static final File BEDROCK_ADDON_DIRECTORY = new File(FabricLoader.getInstance().getGameDirectory(), "bedrock_addons");
    public static final List<IBedrockAddon> BEDROCK_ADDONS = new ArrayList<>();
    public static RegistryHelper REGISTRY_HELPER;

    public static List<io.github.vampirestudios.obsidian.api.obsidian.item.Item> ITEMS = new ArrayList<>();
    public static List<FoodItem> FOODS = new ArrayList<>();
    public static List<WeaponItem> WEAPONS = new ArrayList<>();
    public static List<io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem> TOOLS = new ArrayList<>();
    public static List<Block> BLOCKS = new ArrayList<>();
    public static final List<Potion> POTIONS = new ArrayList<>();
    public static final List<Command> COMMANDS = new ArrayList<>();
    public static final List<StatusEffect> STATUS_EFFECTS = new ArrayList<>();
    public static final List<Enchantment> ENCHANTMENTS = new ArrayList<>();
    public static final List<io.github.vampirestudios.obsidian.api.obsidian.ItemGroup> ITEM_GROUPS = new ArrayList<>();
    public static final List<Entity> ENTITIES = new ArrayList<>();
    public static List<io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem> ARMORS = new ArrayList<>();

    public static void loadDefaultBedrockAddons() {
        if (!BEDROCK_ADDON_DIRECTORY.exists())
            createBedrockAddonsFolder();
    }

    public static void register(File file) {
        if (file.isDirectory()) {
            try {
                File manifestFile = new File(file, "manifest.json");
                if(manifestFile.exists()) {
                    ManifestFile packInfo = GSON.fromJson(new FileReader(manifestFile), ManifestFile.class);
                    BedrockAddon configPack = new BedrockAddon(packInfo, file);
                    if (!BEDROCK_ADDONS.contains(configPack)) {
                        BEDROCK_ADDONS.add(configPack);
                    }
                    Obsidian.BEDROCK_LOGGER.info(String.format("[Obsidian] Registering bedrock addon: %s", configPack.getManifestFile().header.name));
                }
            } catch (Exception e) {
                Obsidian.BEDROCK_LOGGER.error("[Obsidian] Failed to load bedrock addon!", e);
            }
        } else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".zip")) {
            try (ZipFile zipFile = new ZipFile(file)) {
                ZipEntry manifestFileEntry = zipFile.getEntry("manifest.json");
                if (manifestFileEntry != null) {
                    ManifestFile manifestFile = GSON.fromJson(new InputStreamReader(zipFile.getInputStream(manifestFileEntry)), ManifestFile.class);
                    BedrockAddon bedrockAddon = new BedrockAddon(manifestFile, file);
                    if (!BEDROCK_ADDONS.contains(bedrockAddon)) {
                        BEDROCK_ADDONS.add(bedrockAddon);
                    }
                    Obsidian.BEDROCK_LOGGER.info(String.format("[Obsidian] Registering bedrock addon: %s", bedrockAddon.getManifestFile().header.name));
                }
            } catch (Exception e) {
                Obsidian.BEDROCK_LOGGER.error("[Obsidian] Failed to load bedrock addon from zip file!", e);
            }
        }/* else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".mcaddon")) {
            File convertedFile = changeExtension(file, "zip");
            if (convertedFile.isFile() && convertedFile.getName().toLowerCase(Locale.ROOT).endsWith(".mcpack")) {
                File convertedFile2 = changeExtension(convertedFile, "zip");
                try (ZipFile zipFile = new ZipFile(convertedFile)) {
                    ZipEntry packInfoEntry = zipFile.getEntry("addon.info.pack");
                    ZipEntry manifestFileEntry = zipFile.getEntry("manifest.json");
                    if (packInfoEntry != null) {
                        ObsidianAddonInfo obsidianAddonInfo = GSON.fromJson(new InputStreamReader(zipFile.getInputStream(packInfoEntry)), ObsidianAddonInfo.class);
                        ObsidianAddon obsidianAddon = new ObsidianAddon(obsidianAddonInfo, convertedFile);
                        if (!OBSIDIAN_ADDONS.contains(obsidianAddon) && obsidianAddon.getConfigPackInfo().addonVersion == PACK_VERSION) {
                            OBSIDIAN_ADDONS.add(obsidianAddon);
                        }
                        Obsidian.BEDROCK_LOGGER.info(String.format("[Obsidian] Registering obsidian addon: %s", obsidianAddon.getConfigPackInfo().displayName));
                    } else if (manifestFileEntry != null) {
                        ManifestFile manifestFile = GSON.fromJson(new InputStreamReader(zipFile.getInputStream(manifestFileEntry)), ManifestFile.class);
                        BedrockAddon bedrockAddon = new BedrockAddon(manifestFile, convertedFile);
                        if (!BEDROCK_ADDONS.contains(bedrockAddon)) {
                            BEDROCK_ADDONS.add(bedrockAddon);
                        }
                        Obsidian.BEDROCK_LOGGER.info(String.format("[Obsidian] Registering bedrock addon: %s", bedrockAddon.getManifestFile().header.name));
                    }
                } catch (Exception e) {
                    Obsidian.BEDROCK_LOGGER.error("[Obsidian] Failed to load addon!", e);
                }
            }
        }*/
    }

    public static File changeExtension(File f, String newExtension) {
        int i = f.getName().lastIndexOf('.');
        String name = f.getName().substring(0,i);
        return new File(f.getParent(), name + newExtension);
    }

    public static void loadBedrockAddons() {
        try {
            FabricLoader.getInstance().getEntrypoints("obsidian:bedrock_addons", IBedrockAddon.class).forEach(supplier -> {
                try {
                    BEDROCK_ADDONS.add(supplier);
                    Obsidian.BEDROCK_LOGGER.info(String.format("Registering a bedrock addon: %s from an entrypoint",
                            supplier.getManifestFile().header.name));
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
            for (File file : Objects.requireNonNull(BEDROCK_ADDON_DIRECTORY.listFiles())) {
                // Load Packs
                register(file);
            }
            String moduleText;
            if (BEDROCK_ADDONS.size() > 1) {
                moduleText = "Loading %d bedrock addons:";
            } else {
                moduleText = "Loading %d bedrock addon:";
            }

            Obsidian.BEDROCK_LOGGER.info(String.format(moduleText, BEDROCK_ADDONS.size()));

            for(IBedrockAddon pack : BEDROCK_ADDONS) {
                Obsidian.BEDROCK_LOGGER.info(String.format(" - %s", pack.getManifestFile().header.name));

                String modId = pack.getManifestFile().header.identifier.getNamespace();
                String path = BEDROCK_ADDON_DIRECTORY.getPath() + "/" + pack.getIdentifier().getPath();
                REGISTRY_HELPER = RegistryHelper.createRegistryHelper(modId);

                try {
                    parseItemGroup(path);
                    parseBlock(pack, modId, path);
                    parseBasicItems(path);
                    parseArmor(path);
                    parseTools(path);
                    parseWeapons(path);
                    parseFood(path);
                    parsePotions(path);
                    parseCommands(path);
                    parseEnchantments(path);
                    parseStatusEffects(path);
//                    parseEntities(path);
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

    private static void createBedrockAddonsFolder() {
        BEDROCK_ADDON_DIRECTORY.mkdirs();
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

    private static void parseBlock(IBedrockAddon pack, String modId, String path) throws FileNotFoundException {
        /*if (Paths.get(path, "blocks").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "blocks").toFile().listFiles())) {
                if (file.isFile()) {
                    BaseBlock block = GSON.fromJson(new FileReader(file), BaseBlock.class);
                    try {
                        FabricBlockSettings blockSettings = FabricBlockSettings.of(block.information.getMaterial()).sounds(block.information.getBlockSoundGroup())
                                .strength(block.information.destroy_time, block.information.explosion_resistance).drops(block.information.drop)
                                .collidable(block.information.collidable).slipperiness(block.information.slipperiness).emissiveLighting((state, world, pos) ->
                                        block.information.is_emissive).nonOpaque();
                        net.minecraft.block.Block blockImpl = REGISTRY_HELPER.registerBlock(new BlockImpl(block, blockSettings), block.information.name.id.getPath());
                        register(BLOCKS, "block", block.information.name.id.toString(), block);
                    } catch (Exception e) {
                        failedRegistering("block", block.information.name.id.toString(), e);
                    }
                }
            }
        }*/
    }

    private static void parseBasicItems(String path) throws FileNotFoundException {
        if (Paths.get(path, "items").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "items").toFile().listFiles())) {
                if (file.isFile()) {
                    io.github.vampirestudios.obsidian.api.obsidian.item.Item item = GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
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
                        register(TOOLS, "tool", tool.information.name.id.toString(), tool);
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
                    FoodItem foodItem = GSON.fromJson(new FileReader(file), FoodItem.class);
                    try {
                        FoodComponent foodComponent = foodItem.food_information.getBuilder().build();
                        Registry.register(Registry.ITEM, foodItem.information.name.id, new ItemImpl(foodItem, new Item.Settings()
                                .group(foodItem.information.getItemGroup())
                                .maxCount(foodItem.information.max_count)
                                .maxDamage(foodItem.information.use_duration)
                                .food(foodComponent)));
                        register(FOODS, "food item", foodItem.information.name.id.toString(), foodItem);
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
                    Command command = GSON.fromJson(new FileReader(file), Command.class);
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
                    Enchantment enchantment = GSON.fromJson(new FileReader(file), Enchantment.class);
                    try {
                        if(enchantment == null) continue;
                        Registry.register(Registry.ENCHANTMENT, enchantment.id, new EnchantmentImpl(enchantment));
                        register(ENCHANTMENTS, "enchantment", enchantment.name, enchantment);
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
                    Entity entity = GSON.fromJson(new FileReader(file), Entity.class);
                    try {
                        if(entity == null) continue;
                        String baseColor = entity.spawn_egg.base_color.replace("#", "").replace("0x", "");
                        String overlayColor = entity.spawn_egg.overlay_color.replace("#", "").replace("0x", "");

                        EntityType<EntityImpl> entityType = EntityRegistryBuilder.<EntityImpl>createBuilder(entity.identifier)
                                .entity((type, world) -> new EntityImpl(type, world, entity))
                                .category(entity.entity_components.getCategory())
                                .dimensions(EntityDimensions.fixed(entity.entity_components.collision_box.width, entity.entity_components.collision_box.height))
                                .summonable(entity.summonable)
                                .hasEgg(entity.spawnable)
                                .egg(Integer.parseInt(baseColor, 16), Integer.parseInt(overlayColor, 16))
                                .build();
                        FabricDefaultAttributeRegistry.register(entityType, EntityUtils.createGenericEntityAttributes(entity.entity_components.health.max));
                        register(ENTITIES, "entity", entity.identifier.toString(), entity);
                    } catch (Exception e) {
                        failedRegistering("entity", entity.identifier.toString(), e);
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
                        /*PlayerJoinCallback.EVENT.register(player -> {
                            Scoreboard scoreboard = player.getScoreboard();
                            if (!scoreboard.containsObjective(currency.name.toLowerCase())) {
                                scoreboard.addObjective(currency.name.toLowerCase(), ScoreboardCriterion.DUMMY, new LiteralText(currency.name), ScoreboardCriterion.RenderType.INTEGER);
                            }
                        });*/
                        Obsidian.BEDROCK_LOGGER.info(String.format("Registered a currency called %s", currency.name));
                    } catch (Exception e) {
                        failedRegistering("currency", currency.name, e);
                    }
                }
            }
        }
    }

    private static <T> void register(List<T> list, String type, String name, T idk) {
        list.add(idk);
        Obsidian.BEDROCK_LOGGER.info("[Obsidian] Registered a {} {}.", type, name);
    }

    private static void failedRegistering(String type, String name, Exception e) {
        e.printStackTrace();
        Obsidian.BEDROCK_LOGGER.error("[Obsidian] Failed to register {} {}.", type, name);
    }

}
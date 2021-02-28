package io.github.vampirestudios.obsidian.configPack;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.SimpleBowItem;
import io.github.vampirestudios.obsidian.api.SimpleCrossbowItem;
import io.github.vampirestudios.obsidian.api.SimpleTridentItem;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
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
import io.github.vampirestudios.obsidian.api.obsidian.item.*;
import io.github.vampirestudios.obsidian.api.obsidian.potion.Potion;
import io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.utils.*;
import io.github.vampirestudios.vampirelib.api.ShieldRegistry;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
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
    public static List<Elytra> ELYTRAS = new ArrayList<>();
    public static List<CauldronType> CAULDRON_TYPES = new ArrayList<>();
    public static List<ShieldItem> SHIELDS = new ArrayList<>();

    public static void loadDefaultObsidianAddons() {
        if (!OBSIDIAN_ADDON_DIRECTORY.exists())
            createObsidianAddonsFolder();
    }

    public static void register(File file, String mainFile) {
        if (file.isDirectory()) {
            try {
                File packInfoFile = new File(file, mainFile);
                if (packInfoFile.exists()) {
                    Utils.registerAddon(new FileReader(packInfoFile), file);
                }
            } catch (Exception e) {
                Obsidian.LOGGER.error("[Obsidian] Failed to load obsidian addon!", e);
            }
        } else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".zip")) {
            try (ZipFile zipFile = new ZipFile(file)) {
                ZipEntry packInfoEntry = zipFile.getEntry(mainFile);
                if (packInfoEntry != null) {
                   Utils.registerAddon(new InputStreamReader(zipFile.getInputStream(packInfoEntry)));
                }
            } catch (Exception e) {
                Obsidian.LOGGER.error("[Obsidian] Failed to load obsidian addon from zip!", e);
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
                register(file, "addon.info.pack");
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

                init(new ModIdAndAddonPath(modId, path));

//                ObsidianAddon addon = (ObsidianAddon) pack;
//                Utils.registerMod(addon, addon.getFile(), addon.getConfigPackInfo().namespace);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.exit(0);
        }
    }

    private static void init(ModIdAndAddonPath id) {
        try {
            Obsidian.ADDON_MODULE_REGISTRY.forEach(addonModule -> loadAddonModule(id, addonModule));
            String path = id.getPath();
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
            parseShields(path);
//            parseElytras(path);
//            parseVillagerProfessions(path);
//            parseVillagerBiomeType(path);
//            parseVillagerTrades(path);
//            parseFluids(path);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
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

    private static void loadAddonModule(ModIdAndAddonPath id, AddonModule addonModule) {
        if (Paths.get(id.getPath(), addonModule.getType()).toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(id.getPath(), addonModule.getType()).toFile().listFiles())) {
                if (file.isFile()) {
                    try {
                        addonModule.init(file, id);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
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

    public static <T> void register(List<T> list, String type, String name, T idk) {
        list.add(idk);
        Obsidian.LOGGER.info("[Obsidian] Registered a {} {}.", type, name);
    }

    public static void failedRegistering(String type, String name, Exception e) {
        e.printStackTrace();
        Obsidian.LOGGER.error("[Obsidian] Failed to register {} {}.", type, name);
    }

}

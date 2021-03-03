package io.github.vampirestudios.obsidian.configPack;

import com.google.common.base.Joiner;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModuleVersionIndependent;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelper;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.cauldronTypes.CauldronType;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.item.*;
import io.github.vampirestudios.obsidian.api.obsidian.potion.Potion;
import io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect;
import io.github.vampirestudios.obsidian.api.obsidian.villager.VillagerBiomeType;
import io.github.vampirestudios.obsidian.api.obsidian.villager.VillagerProfession;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;

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
    public static List<VillagerProfession> VILLAGER_PROFESSIONS = new ArrayList<>();
    public static List<VillagerBiomeType> VILLAGER_BIOME_TYPES = new ArrayList<>();

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
            REGISTRY_HELPER = RegistryHelper.createRegistryHelper(modId);

            init(new ModIdAndAddonPath(modId, path));
        }
    }

    private static void init(ModIdAndAddonPath id) {
        try {
            Obsidian.ADDON_MODULE_REGISTRY.forEach(addonModule -> loadAddonModule(id, addonModule));
            Obsidian.ADDON_MODULE_VERSION_INDEPENDENT_REGISTRY.forEach(addonModule -> loadAddonModule(id, addonModule));
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
//        System.out.println("Id: %s" + id.getPath() + " Type: " + addonModule.getType());
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

    private static void loadAddonModule(ModIdAndAddonPath id, AddonModuleVersionIndependent addonModule) {
        if (Paths.get(id.getPath(), addonModule.getType()).toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(id.getPath(), addonModule.getType()).toFile().listFiles())) {
                if (file.isFile()) {
                    try {
                        addonModule.init(file, id, "obsidian");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /*private static void parseCurrencies(String path) throws FileNotFoundException {
        if (Paths.get(path, "currency").toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(path, "currency").toFile().listFiles())) {
                if (file.isFile()) {
                    Currency currency = Obsidian.GSON.fromJson(new FileReader(file), Currency.class);
                    try {
                        if(currency == null) continue;
                        *//*PlayerJoinCallback.EVENT.register(player -> {
                            Scoreboard scoreboard = player.getScoreboard();
                            if (!scoreboard.containsObjective(currency.name.toLowerCase())) {
                                scoreboard.addObjective(currency.name.toLowerCase(), ScoreboardCriterion.DUMMY, new LiteralText(currency.name), ScoreboardCriterion.RenderType.INTEGER);
                            }
                        });*//*
                        Obsidian.LOGGER.info(String.format("Registered a currency called %s", currency.name));
                    } catch (Exception e) {
                        failedRegistering("currency", currency.name, e);
                    }
                }
            }
        }
    }*/

    public static <T> void register(List<T> list, String type, String name, T idk) {
        list.add(idk);
        Obsidian.LOGGER.info("[Obsidian] Registered a {} {}.", type, name);
    }

    public static void failedRegistering(String type, String name, Exception e) {
        e.printStackTrace();
        Obsidian.LOGGER.error("[Obsidian] Failed to register {} {}.", type, name);
    }

}

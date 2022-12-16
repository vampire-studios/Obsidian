package io.github.vampirestudios.obsidian.configPack;

import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.SyntaxError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.bedrock.BedrockAddon;
import io.github.vampirestudios.obsidian.api.bedrock.IBedrockAddon;
import io.github.vampirestudios.obsidian.api.bedrock.ManifestFile;
import io.github.vampirestudios.obsidian.api.bedrock.block.BaseBlock;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import io.github.vampirestudios.obsidian.api.obsidian.potion.Potion;
import io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.SimpleStringDeserializer;
import io.github.vampirestudios.vampirelib.utils.registry.RegistryHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    public static final File BEDROCK_ADDON_DIRECTORY = new File(FabricLoader.getInstance().getGameDir().toFile(), "bedrock_addons");
    public static final Map<IBedrockAddon, String> BEDROCK_ADDONS = new HashMap<>();
    public static final Map<String, IBedrockAddon> TEMP_BEDROCK_ADDONS = new HashMap<>();
    public static final List<Potion> POTIONS = new ArrayList<>();
    public static final List<Command> COMMANDS = new ArrayList<>();
    public static final List<StatusEffect> STATUS_EFFECTS = new ArrayList<>();
    public static final List<Enchantment> ENCHANTMENTS = new ArrayList<>();
    public static final List<io.github.vampirestudios.obsidian.api.obsidian.ItemGroup> ITEM_GROUPS = new ArrayList<>();
    public static final List<Entity> ENTITIES = new ArrayList<>();
    public static RegistryHelper REGISTRY_HELPER;
    public static List<io.github.vampirestudios.obsidian.api.bedrock.item.Item> ITEMS = new ArrayList<>();
    public static List<FoodItem> FOODS = new ArrayList<>();
    public static List<WeaponItem> WEAPONS = new ArrayList<>();
    public static List<io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem> TOOLS = new ArrayList<>();
    public static List<BaseBlock> BLOCKS = new ArrayList<>();
    public static List<io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem> ARMORS = new ArrayList<>();

    public static void loadDefaultBedrockAddons() {
        if (!BEDROCK_ADDON_DIRECTORY.exists())
            createBedrockAddonsFolder();
    }

    public static void register(File file) {
        if (file.isDirectory()) {
            try {
                File manifestFile = new File(file, "manifest.json");
                if (manifestFile.exists()) {
                    ManifestFile packInfo = GSON.fromJson(new FileReader(manifestFile), ManifestFile.class);
                    BedrockAddon configPack = new BedrockAddon(packInfo, file);
                    if (!BEDROCK_ADDONS.containsKey(configPack)) {
                        /*TEMP_BEDROCK_ADDONS.put(configPack.getManifestFile().header.uuid, configPack);
                        boolean dependenciesLoaded = false;
                        Map<String, int[]> dependenciesInfo = new HashMap<>();
                        for (ManifestFile.Dependencies dependency : configPack.getManifestFile().dependencies) {
                            dependenciesInfo.put(dependency.uuid, dependency.version);
                        }
                        List<Boolean> dependenciesExistsList = new ArrayList<>();
                        List<String> dependenciesUUIDList = new ArrayList<>();
                        dependenciesInfo.forEach((s, ints) -> {
                            dependenciesExistsList.add(BEDROCK_ADDONS.containsValue(s));
                            dependenciesUUIDList.add(s);
                        });
                        if (!dependenciesExistsList.contains(false)) dependenciesLoaded = true;

                        if (dependenciesLoaded) BEDROCK_ADDONS.put(configPack, configPack.getManifestFile().header.uuid);
                        else {
                            for (String uuid : dependenciesUUIDList) {
                                if (TEMP_BEDROCK_ADDONS.containsKey(uuid)) BEDROCK_ADDONS.put(TEMP_BEDROCK_ADDONS.get(uuid), uuid);
                            }
                        }
                        dependenciesUUIDList.forEach(TEMP_BEDROCK_ADDONS::remove);*/
                        BEDROCK_ADDONS.put(configPack, configPack.getManifestFile().header.uuid);
                    }
                    Obsidian.BEDROCK_LOGGER.info(String.format("[Obsidian] Registering bedrock addon: %s (Type: %s)", configPack.getManifestFile().header.name, configPack.getManifestFile().modules[0].type));
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
                    if (!BEDROCK_ADDONS.containsKey(bedrockAddon)) {
                        /*boolean dependenciesLoaded = false;
                        Map<String, int[]> dependenciesInfo = new HashMap<>();
                        for (ManifestFile.Dependencies dependency : bedrockAddon.getManifestFile().dependencies) {
                            dependenciesInfo.put(dependency.uuid, dependency.version);
                        }
                        List<Boolean> dependenciesExistsList = new ArrayList<>();
                        dependenciesInfo.forEach((s, ints) -> dependenciesExistsList.add(BEDROCK_ADDONS.containsValue(s)));
                        if (!dependenciesExistsList.contains(false)) dependenciesLoaded = true;

                        if (dependenciesLoaded) */
                        BEDROCK_ADDONS.put(bedrockAddon, bedrockAddon.getManifestFile().header.uuid);
                    }
                    Obsidian.BEDROCK_LOGGER.info(String.format("[Obsidian] Registering bedrock addon: %s (Type: %s)", bedrockAddon.getManifestFile().header.name, bedrockAddon.getManifestFile().modules[0].type));
                }
            } catch (Exception e) {
                Obsidian.BEDROCK_LOGGER.error("[Obsidian] Failed to load bedrock addon from zip file!", e);
            }
        }
    }

    public static void loadBedrockAddons() {
        try {
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

            for (IBedrockAddon pack : BEDROCK_ADDONS.keySet()) {
                Obsidian.BEDROCK_LOGGER.info(String.format(" - %s", pack.getManifestFile().header.name));

                String modId = pack.getManifestFile().header.identifier.getNamespace();
                String path = BEDROCK_ADDON_DIRECTORY.getPath() + "/" + pack.getIdentifier().getPath();
                REGISTRY_HELPER = RegistryHelper.createRegistryHelper(modId);

                try {
                    Registries.ADDON_MODULE_REGISTRY.forEach(addonModule -> loadAddonModule(pack, new BasicAddonInfo(modId, path), addonModule));
//                    parseItemGroup(addonPath);
//                    parseBlock(addonPath);
//                    parseBasicItems(addonPath);
//                    parseArmor(addonPath);
//                    parseTools(addonPath);
//                    parseWeapons(addonPath);
//                    parseFood(addonPath);
//                    parsePotions(addonPath);
//                    parseCommands(addonPath);
//                    parseEnchantments(addonPath);
//                    parseStatusEffects(addonPath);
//                    parseEntities(addonPath);
//                    parseCurrencies(addonPath);
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

    private static void loadAddonModule(IAddonPack bedrockAddon, BasicAddonInfo id, AddonModule addonModule) {
        if (Paths.get(id.addonPath(), addonModule.getType()).toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(id.addonPath(), addonModule.getType()).toFile().listFiles())) {
                if (file.isFile()) {
                    try {
                        addonModule.init(bedrockAddon, file, id);
                    } catch (IOException | SyntaxError | DeserializationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static <T> T register(Registry<T> list, String type, Identifier name, T idk) {
        Obsidian.BEDROCK_LOGGER.info("Registered {} {}.", type, name);
        if (list.get(name) != null) return list.get(name);
        else return Registry.register(list, name, idk);
    }

    public static void failedRegistering(String type, String name, Exception e) {
        failedRegistering(type, Identifier.tryParse(name), e);
    }

    public static void failedRegistering(String type, Identifier name, Exception e) {
        Obsidian.BEDROCK_LOGGER.error("Failed to register {} {}.", type, name);
        Obsidian.BEDROCK_LOGGER.error(e.getMessage(), e);
    }

}
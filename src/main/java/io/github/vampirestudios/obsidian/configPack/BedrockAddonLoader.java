package io.github.vampirestudios.obsidian.configPack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.bedrock.BedrockAddon;
import io.github.vampirestudios.obsidian.api.bedrock.IBedrockAddon;
import io.github.vampirestudios.obsidian.api.bedrock.ManifestFile;
import io.github.vampirestudios.obsidian.api.bedrock.block.BaseBlock;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModuleVersionIndependent;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelper;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import io.github.vampirestudios.obsidian.api.obsidian.enchantments.Enchantment;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.item.FoodItem;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import io.github.vampirestudios.obsidian.api.obsidian.potion.Potion;
import io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.SimpleStringDeserializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileNotFoundException;
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

public class BedrockAddonLoader {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Obsidian: Bedrock"));
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, (SimpleStringDeserializer<?>) Identifier::new)
            .setPrettyPrinting().create();
    public static final File BEDROCK_ADDON_DIRECTORY = new File(FabricLoader.getInstance().getGameDirectory(), "bedrock_addons");
    public static final List<IBedrockAddon> BEDROCK_ADDONS = new ArrayList<>();
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
        String name = f.getName().substring(0, i);
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

            for (IBedrockAddon pack : BEDROCK_ADDONS) {
                Obsidian.BEDROCK_LOGGER.info(String.format(" - %s", pack.getManifestFile().header.name));

                String modId = pack.getManifestFile().header.identifier.getNamespace();
                String path = BEDROCK_ADDON_DIRECTORY.getPath() + "/" + pack.getIdentifier().getPath();
                REGISTRY_HELPER = RegistryHelper.createRegistryHelper(modId);

                try {
//                    Obsidian.ADDON_MODULE_REGISTRY.forEach(addonModule -> loadAddonModule(new ModIdAndAddonPath(modId, path), addonModule));
//                    Obsidian.ADDON_MODULE_VERSION_INDEPENDENT_REGISTRY.forEach(addonModule -> loadAddonModule(new ModIdAndAddonPath(modId, path), addonModule));
//                    parseItemGroup(path);
//                    parseBlock(path);
//                    parseBasicItems(path);
//                    parseArmor(path);
//                    parseTools(path);
//                    parseWeapons(path);
//                    parseFood(path);
//                    parsePotions(path);
//                    parseCommands(path);
//                    parseEnchantments(path);
//                    parseStatusEffects(path);
//                    parseEntities(path);
//                    parseCurrencies(path);
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

    private static void loadAddonModule(ModIdAndAddonPath id, AddonModule addonModule) {
        if (Paths.get(id.getPath(), addonModule.getType()).toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(id.getPath(), addonModule.getType()).toFile().listFiles())) {
                if (file.isFile()) {
                    try {
                        addonModule.init(null, file, id);
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
                        addonModule.init(file, id, "bedrock");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
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
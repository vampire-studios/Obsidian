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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ObsidianAddonLoader {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Obsidian"));
    public static final File OBSIDIAN_ADDON_DIRECTORY = new File(FabricLoader.getInstance().getGameDirectory(), /*ModConfig.addon_folder.getValue()*/"obsidian_addons");
    public static final CopyOnWriteArrayList<IAddonPack> OBSIDIAN_ADDONS = new CopyOnWriteArrayList<>();
    public static final int PACK_VERSION = 2;
    public static RegistryHelper REGISTRY_HELPER;
    public static CopyOnWriteArrayList<io.github.vampirestudios.obsidian.api.obsidian.item.Item> ITEMS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<FoodItem> FOODS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<WeaponItem> WEAPONS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<RangedWeaponItem> RANGED_WEAPONS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem> TOOLS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Block> BLOCKS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Block> ORES = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Potion> POTIONS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Command.Node> COMMANDS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<StatusEffect> STATUS_EFFECTS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Enchantment> ENCHANTMENTS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<io.github.vampirestudios.obsidian.api.obsidian.ItemGroup> ITEM_GROUPS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Entity> ENTITIES = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem> ARMORS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<Elytra> ELYTRAS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<CauldronType> CAULDRON_TYPES = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<ShieldItem> SHIELDS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<VillagerProfession> VILLAGER_PROFESSIONS = new CopyOnWriteArrayList<>();
    public static CopyOnWriteArrayList<VillagerBiomeType> VILLAGER_BIOME_TYPES = new CopyOnWriteArrayList<>();

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

        for (IAddonPack pack : OBSIDIAN_ADDONS) {
            ObsidianAddon addon = (ObsidianAddon) pack;
            Obsidian.LOGGER.info(String.format(" - %s", pack.getConfigPackInfo().displayName));

            String modId = pack.getConfigPackInfo().namespace;
            String path = OBSIDIAN_ADDON_DIRECTORY.getPath() + "/" + pack.getConfigPackInfo().folderName + "/content/" + pack.getConfigPackInfo().namespace;
            REGISTRY_HELPER = RegistryHelper.createRegistryHelper(modId);

            try {
                Obsidian.ADDON_MODULE_REGISTRY.forEach(addonModule -> loadAddonModule(addon, new ModIdAndAddonPath(modId, path), addonModule));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private static void createObsidianAddonsFolder() {
        OBSIDIAN_ADDON_DIRECTORY.mkdirs();
    }

    public static BlockState getState(net.minecraft.block.Block block, Map<String, String> jsonProperties) {
        BlockState blockstate = block.getDefaultState();
        Collection<Property<?>> properties = blockstate.getProperties();
        for (Property property : properties) {
            String propertyName = property.getName();
            if (jsonProperties.containsKey(propertyName)) {
                String valueName = jsonProperties.get(propertyName);
                Optional valueOpt = property.parse(valueName);
                if (valueOpt.isPresent()) {
                    Comparable value = (Comparable) valueOpt.get();
                    blockstate = blockstate.with(property, value);
                } else {
                    System.err.printf("Property[%s=%s] doesn't exist for %s%n", propertyName, valueName, block);
                }
                jsonProperties.remove(propertyName);
            }
        }
        if (!jsonProperties.isEmpty()) {
            Joiner joiner = Joiner.on(", ");
            System.err.printf("The following properties do not exist in %s: %s%n", block, joiner.join(jsonProperties.keySet()));
        }
        return blockstate;
    }

    private static void loadAddonModule(ObsidianAddon addon, ModIdAndAddonPath id, AddonModule addonModule) {
        if (Paths.get(id.getPath(), addonModule.getType()).toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(id.getPath(), addonModule.getType()).toFile().listFiles())) {
                if (file.isFile()) {
                    try {
                        addonModule.init(addon, file, id);
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

    public static <T> void register(List<T> list, String type, String name, T idk) {
        list.add(idk);
        Obsidian.LOGGER.info("[Obsidian] Registered a {} {}.", type, name);
    }

    public static void failedRegistering(String type, String name, Exception e) {
        Obsidian.LOGGER.error("[Obsidian] Failed to register {} {}.", type, name);
        e.printStackTrace();
        Obsidian.LOGGER.error(e.getMessage());
    }

}

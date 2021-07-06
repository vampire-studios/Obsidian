package io.github.vampirestudios.obsidian.configPack;

import com.google.common.base.Joiner;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.*;
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
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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

import static io.github.vampirestudios.obsidian.Obsidian.id;

public class ObsidianAddonLoader {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "Obsidian"));
    public static final File OBSIDIAN_ADDON_DIRECTORY = new File(FabricLoader.getInstance().getGameDirectory(), /*ModConfig.addon_folder.getValue()*/"obsidian_addons");
    public static final CopyOnWriteArrayList<IAddonPack> OBSIDIAN_ADDONS = new CopyOnWriteArrayList<>();
    public static final int PACK_VERSION = 2;
    public static RegistryHelper REGISTRY_HELPER;
    public static Registry<Item> ITEMS = FabricRegistryBuilder.createSimple(Item.class, id("items")).buildAndRegister();
    public static Registry<FoodItem> FOODS = FabricRegistryBuilder.createSimple(FoodItem.class, id("foods")).buildAndRegister();
    public static Registry<WeaponItem> WEAPONS = FabricRegistryBuilder.createSimple(WeaponItem.class, id("weapons")).buildAndRegister();
    public static Registry<RangedWeaponItem> RANGED_WEAPONS = FabricRegistryBuilder.createSimple(RangedWeaponItem.class, id("ranged_weapons")).buildAndRegister();
    public static Registry<ToolItem> TOOLS = FabricRegistryBuilder.createSimple(ToolItem.class, id("tools")).buildAndRegister();
    public static Registry<Block> BLOCKS = FabricRegistryBuilder.createSimple(Block.class, id("blocks")).buildAndRegister();
    public static Registry<FuelSource> FUEL_SOURCES = FabricRegistryBuilder.createSimple(FuelSource.class, id("fuel_sources")).buildAndRegister();
    public static Registry<Block> ORES = FabricRegistryBuilder.createSimple(Block.class, id("ores")).buildAndRegister();
    public static Registry<Potion> POTIONS = FabricRegistryBuilder.createSimple(Potion.class, id("potions")).buildAndRegister();
    public static Registry<Command.CommandNode> COMMANDS = FabricRegistryBuilder.createSimple(Command.CommandNode.class, id("commands")).buildAndRegister();
    public static Registry<StatusEffect> STATUS_EFFECTS = FabricRegistryBuilder.createSimple(StatusEffect.class, id("status_effects")).buildAndRegister();
    public static Registry<Enchantment> ENCHANTMENTS = FabricRegistryBuilder.createSimple(Enchantment.class, id("enchantments")).buildAndRegister();
    public static Registry<ItemGroup> ITEM_GROUPS = FabricRegistryBuilder.createSimple(ItemGroup.class, id("item_groups")).buildAndRegister();
    public static Registry<Entity> ENTITIES = FabricRegistryBuilder.createSimple(Entity.class, id("entities")).buildAndRegister();
    public static Registry<EntityModel> ENTITY_MODELS = FabricRegistryBuilder.createSimple(EntityModel.class, id("entity_models")).buildAndRegister();
    public static Registry<ArmorItem> ARMORS = FabricRegistryBuilder.createSimple(ArmorItem.class, id("armors")).buildAndRegister();
    public static Registry<Elytra> ELYTRAS = FabricRegistryBuilder.createSimple(Elytra.class, id("elytras")).buildAndRegister();
    public static Registry<ZoomableItem> ZOOMABLE_ITEMS = FabricRegistryBuilder.createSimple(ZoomableItem.class, id("zoomable_items")).buildAndRegister();
    public static Registry<CauldronType> CAULDRON_TYPES = FabricRegistryBuilder.createSimple(CauldronType.class, id("cauldron_types")).buildAndRegister();
    public static Registry<ShieldItem> SHIELDS = FabricRegistryBuilder.createSimple(ShieldItem.class, id("shields")).buildAndRegister();
    public static Registry<VillagerProfession> VILLAGER_PROFESSIONS = FabricRegistryBuilder.createSimple(VillagerProfession.class, id("villager_professions")).buildAndRegister();
    public static Registry<VillagerBiomeType> VILLAGER_BIOME_TYPES = FabricRegistryBuilder.createSimple(VillagerBiomeType.class, id("villager_biome_types")).buildAndRegister();

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

    public static <T> void register(Registry<T> list, String type, Identifier name, T idk) {
        Registry.register(list, name, idk);
        Obsidian.LOGGER.info("[Obsidian] Registered a {} {}.", type, name);
    }

    public static void failedRegistering(String type, String name, Exception e) {
        Obsidian.LOGGER.error("[Obsidian] Failed to register {} {}.", type, name);
        e.printStackTrace();
        Obsidian.LOGGER.error(e.getMessage());
    }

}

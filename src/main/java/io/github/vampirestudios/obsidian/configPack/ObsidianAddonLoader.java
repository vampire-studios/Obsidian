package io.github.vampirestudios.obsidian.configPack;

import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.SyntaxError;
import com.google.common.base.Joiner;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelper;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.QuiltLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static io.github.vampirestudios.obsidian.Obsidian.id;

public class ObsidianAddonLoader {

    public static final File OBSIDIAN_ADDON_DIRECTORY = QuiltLoader.getGameDir().resolve(Obsidian.CONFIG.addonsFolder).toFile();
    public static final Registry<IAddonPack> OBSIDIAN_ADDONS = FabricRegistryBuilder.createSimple(IAddonPack.class, id("obsidian_addons")).buildAndRegister();
    public static final int SCHEMA_VERSION = 3;
    public static RegistryHelper REGISTRY_HELPER;

    public static void loadDefaultObsidianAddons() {
        if (!OBSIDIAN_ADDON_DIRECTORY.exists())
            createObsidianAddonsFolder();
    }

    public net.minecraft.block.Block register(Identifier name, net.minecraft.block.Block block, net.minecraft.item.ItemGroup tab) {
        return register(name, block, new net.minecraft.item.Item.Settings().group(tab));
    }

    public net.minecraft.block.Block register(Identifier name, net.minecraft.block.Block block, net.minecraft.item.Item.Settings properties) {
        return register(name, block, new BlockItem(block, properties));
    }

    public net.minecraft.block.Block register(Identifier name, net.minecraft.block.Block block, BlockItem item) {
        Registry.register(Registry.BLOCK, name, block);
        Registry.register(Registry.ITEM, name, item);
        return block;
    }

    public static void register(File file, String legacyFile, String newFile) {
        if (file.isDirectory()) {
            try {
                File legacyPackInfoFile = new File(file, legacyFile);
                File newPackInfoFile = new File(file, newFile);
                Utils.registerAddon(legacyPackInfoFile, newPackInfoFile);
            } catch (Exception e) {
                Obsidian.LOGGER.error("Failed to load obsidian addon!", e);
            }
        } else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".zip")) {
            try (ZipFile zipFile = new ZipFile(file)) {
                ZipEntry packInfoEntry = zipFile.getEntry(legacyFile);
                if (packInfoEntry != null) {
//                    Utils.registerAddon(new InputStreamReader(zipFile.getInputStream(packInfoEntry)));
                }
            } catch (Exception e) {
                Obsidian.LOGGER.error("Failed to load obsidian addon from zip!", e);
            }
        }
    }

    public static void loadObsidianAddons() {
        for (File file : Objects.requireNonNull(OBSIDIAN_ADDON_DIRECTORY.listFiles())) {
            // Load Packs
            register(file, "addon.info.pack", "addon.info.json5");
        }

        String moduleText;
        if (OBSIDIAN_ADDONS.getIds().size() > 1) {
            moduleText = "Loading {} obsidian addons:";
        } else {
            moduleText = "Loading {} obsidian addon:";
        }

        Obsidian.LOGGER.info(moduleText, OBSIDIAN_ADDONS.getIds().size());

        for (IAddonPack pack : OBSIDIAN_ADDONS) {
            String name;
            String folderName;
            String id;
            if (pack.getConfigPackInfo() instanceof LegacyObsidianAddonInfo legacyObsidianAddonInfo) {
                name = legacyObsidianAddonInfo.displayName;
                folderName = legacyObsidianAddonInfo.folderName;
                id = legacyObsidianAddonInfo.namespace;
            } else {
                ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) pack.getConfigPackInfo();
                name = addonInfo.addon.name;
                folderName = addonInfo.addon.folderName;
                id = addonInfo.addon.id;
            }

            Obsidian.LOGGER.info(" - {}", name);

            String path = OBSIDIAN_ADDON_DIRECTORY.getPath() + "/" + folderName + "/content/" + id;
            REGISTRY_HELPER = RegistryHelper.createRegistryHelper(id);

            try {
                Registries.ADDON_MODULE_REGISTRY.forEach(addonModule -> loadAddonModule(pack, new BasicAddonInfo(id, path), addonModule));
            } catch (Exception throwable) {
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
                    Obsidian.LOGGER.error("Property[{}={}] doesn't exist for {}", propertyName, valueName, block);
                }
                jsonProperties.remove(propertyName);
            }
        }
        if (!jsonProperties.isEmpty()) {
            Joiner joiner = Joiner.on(", ");
            Obsidian.LOGGER.error("The following properties do not exist in {}: {}", block, joiner.join(jsonProperties.keySet()));
        }
        return blockstate;
    }

    private static void loadAddonModule(IAddonPack addon, BasicAddonInfo id, AddonModule addonModule) {
        if (Paths.get(id.addonPath(), addonModule.getType()).toFile().exists()) {
            for (File file : Objects.requireNonNull(Paths.get(id.addonPath(), addonModule.getType()).toFile().listFiles())) {
                if (file.isFile()) {
                    try {
                        addonModule.init(addon, file, id);
                    } catch (SyntaxError | IOException | DeserializationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static <T> T register(Registry<T> list, String type, Identifier name, T idk) {
        Obsidian.LOGGER.info("Registered {} {}.", type, name);
        if (list.get(name) != null) return list.get(name);
        else return Registry.register(list, name, idk);
    }

    public static void failedRegistering(String type, String name, Exception e) {
        failedRegistering(type, Identifier.tryParse(name), e);
    }

    public static void failedRegistering(String type, Identifier name, Exception e) {
        Obsidian.LOGGER.error("Failed to register {} {}.", type, name);
        Obsidian.LOGGER.error(e.getMessage(), e);
    }

}

package io.github.vampirestudios.obsidian.utils;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonModMetadata;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.discovery.ModCandidate;
import net.fabricmc.loader.util.UrlUtil;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.Optional;

public class Utils {
    public Utils() {
    }

    public static void registerAddon(Reader reader, File file) {
        ObsidianAddonInfo obsidianAddonInfo = Obsidian.GSON.fromJson(reader, ObsidianAddonInfo.class);
        ObsidianAddon obsidianAddon = new ObsidianAddon(obsidianAddonInfo, file);
        if (!ObsidianAddonLoader.OBSIDIAN_ADDONS.contains(obsidianAddon) && obsidianAddon.getConfigPackInfo().addonVersion == ObsidianAddonLoader.PACK_VERSION) {
            ObsidianAddonLoader.OBSIDIAN_ADDONS.add(obsidianAddon);
            Obsidian.LOGGER.info(String.format("[Obsidian] Registering obsidian addon: %s", obsidianAddon.getConfigPackInfo().displayName));
        } else {
            Obsidian.LOGGER.info(String.format("[Obsidian] Found incompatible obsidian addon: %s with a version of %d", obsidianAddon.getConfigPackInfo().displayName, obsidianAddon.getConfigPackInfo().addonVersion));
        }
    }

    public static void registerAddon(Reader reader) {
        ObsidianAddonInfo obsidianAddonInfo = Obsidian.GSON.fromJson(reader, ObsidianAddonInfo.class);
        ObsidianAddon obsidianAddon = new ObsidianAddon(obsidianAddonInfo);
        if (!ObsidianAddonLoader.OBSIDIAN_ADDONS.contains(obsidianAddon) && obsidianAddon.getConfigPackInfo().addonVersion == ObsidianAddonLoader.PACK_VERSION) {
            ObsidianAddonLoader.OBSIDIAN_ADDONS.add(obsidianAddon);
            Obsidian.LOGGER.info(String.format("[Obsidian] Registering obsidian addon: %s", obsidianAddon.getConfigPackInfo().displayName));
        } else {
            Obsidian.LOGGER.info(String.format("[Obsidian] Found incompatible obsidian addon: %s with a version of %d", obsidianAddon.getConfigPackInfo().displayName, obsidianAddon.getConfigPackInfo().addonVersion));
        }
    }

    public static void registerMod(ObsidianAddon addon, File file, String namespace) {
        try {
            FabricLoader loader = FabricLoader.getInstance();
            Method addMod = net.fabricmc.loader.FabricLoader.class.getDeclaredMethod("addMod", ModCandidate.class);
            addMod.setAccessible(true);
            ModCandidate candidate = new ModCandidate(new ObsidianAddonModMetadata(addon), UrlUtil.asUrl(file), 0, false);
            addMod.invoke(loader, candidate);
            Optional<ModContainer> optional = loader.getModContainer(namespace);
            if (optional.isPresent()) {
                Method setupRootPath = net.fabricmc.loader.ModContainer.class.getDeclaredMethod("setupRootPath");
                setupRootPath.setAccessible(true);
                setupRootPath.invoke(optional.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Identifier appendToPath(Identifier identifier, String suffix) {
        return new Identifier(identifier.getNamespace(), identifier.getPath() + suffix);
    }

    public static Identifier prependToPath(Identifier identifier, String prefix) {
        return new Identifier(identifier.getNamespace(), prefix + identifier.getPath());
    }

    public static Identifier appendAndPrependToPath(Identifier identifier, String prefix, String suffix) {
        return new Identifier(identifier.getNamespace(), prefix + identifier.getPath() + suffix);
    }

}

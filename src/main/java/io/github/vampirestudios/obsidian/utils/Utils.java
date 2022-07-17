package io.github.vampirestudios.obsidian.utils;

import io.github.vampirestudios.obsidian.Const;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.Reader;
import java.lang.reflect.Array;

public class Utils {

    public static void registerAddon(Reader reader, File file) {
        ObsidianAddonInfo obsidianAddonInfo = Obsidian.GSON.fromJson(reader, ObsidianAddonInfo.class);
        ObsidianAddon obsidianAddon = new ObsidianAddon(obsidianAddonInfo, file);
        if (!ObsidianAddonLoader.OBSIDIAN_ADDONS.containsId(Const.id(obsidianAddon.getConfigPackInfo().namespace)) && obsidianAddon.getConfigPackInfo().addonVersion ==
                ObsidianAddonLoader.PACK_VERSION) {
            Obsidian.registerInRegistry(ObsidianAddonLoader.OBSIDIAN_ADDONS, obsidianAddon.getConfigPackInfo().namespace, obsidianAddon);
            Obsidian.LOGGER.info(String.format("[Obsidian] Registering obsidian addon: %s", obsidianAddon.getConfigPackInfo().displayName));
        } else if(obsidianAddon.getConfigPackInfo().addonVersion != ObsidianAddonLoader.PACK_VERSION) {
            Obsidian.LOGGER.info(String.format("[Obsidian] Found incompatible obsidian addon: %s with a version of %d", obsidianAddon.getConfigPackInfo().displayName, obsidianAddon.getConfigPackInfo().addonVersion));
        }
    }

    public static void registerAddon(Reader reader) {
        ObsidianAddonInfo obsidianAddonInfo = Obsidian.GSON.fromJson(reader, ObsidianAddonInfo.class);
        ObsidianAddon obsidianAddon = new ObsidianAddon(obsidianAddonInfo);
        if (!ObsidianAddonLoader.OBSIDIAN_ADDONS.containsId(Const.id(obsidianAddon.getConfigPackInfo().namespace)) &&
                obsidianAddon.getConfigPackInfo().addonVersion == ObsidianAddonLoader.PACK_VERSION) {
            Obsidian.registerInRegistry(ObsidianAddonLoader.OBSIDIAN_ADDONS, obsidianAddon.getConfigPackInfo().namespace, obsidianAddon);
            Obsidian.LOGGER.info(String.format("[Obsidian] Registering obsidian addon: %s", obsidianAddon.getConfigPackInfo().displayName));
        } else {
            Obsidian.LOGGER.info(String.format("[Obsidian] Found incompatible obsidian addon: %s with a version of %d", obsidianAddon.getConfigPackInfo().displayName, obsidianAddon.getConfigPackInfo().addonVersion));
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

    public static <T> T[] stripNulls(T[] arr) {
        int i = 0;
        for(T t : arr) {
            if(t != null) {
                i ++;
            }
        }
        T[] newArr = (T[]) Array.newInstance(arr.getClass().getComponentType(), i);
        i = 0;
        for(T t : arr) {
            if(t != null) {
                newArr[i] = t;
                i++;
            }
        }
        return newArr;
    }

}

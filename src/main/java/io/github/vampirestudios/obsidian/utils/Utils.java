package io.github.vampirestudios.obsidian.utils;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Const;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.configPack.*;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;

public class Utils {

    public static void registerAddon(File legacyInfoFile, File newInfoFile) throws SyntaxError, IOException {
        if (legacyInfoFile.exists()) {
            LegacyObsidianAddonInfo obsidianAddonInfo = Obsidian.GSON.fromJson(new FileReader(legacyInfoFile), LegacyObsidianAddonInfo.class);
            LegacyObsidianAddon obsidianAddon = new LegacyObsidianAddon(obsidianAddonInfo, legacyInfoFile);
            if (!ObsidianAddonLoader.OBSIDIAN_ADDONS.containsId(Const.id(obsidianAddon.getConfigPackInfo().namespace))
                    && obsidianAddon.getConfigPackInfo().addonVersion == ObsidianAddonLoader.SCHEMA_VERSION) {
                Obsidian.registerInRegistry(ObsidianAddonLoader.OBSIDIAN_ADDONS, obsidianAddon.getConfigPackInfo().namespace, obsidianAddon);
                Obsidian.LOGGER.info("Registering obsidian addon: {}", obsidianAddon.getConfigPackInfo().displayName);
            } else if(obsidianAddon.getConfigPackInfo().addonVersion != ObsidianAddonLoader.SCHEMA_VERSION) {
                Obsidian.LOGGER.info("Found incompatible obsidian addon: {} with a version of {}", obsidianAddon.getConfigPackInfo().displayName, obsidianAddon.getConfigPackInfo().version);
            } else {
                Obsidian.LOGGER.error("Found obsidian addon with invalid info file: {} with a version of {}", obsidianAddon.getConfigPackInfo().displayName, obsidianAddon.getConfigPackInfo().version);
            }
        }
        if (newInfoFile.exists()) {
            JsonObject jsonObject = Obsidian.JANKSON.load(newInfoFile);
            ObsidianAddonInfo obsidianAddonInfo = Obsidian.JANKSON.fromJson(jsonObject, ObsidianAddonInfo.class);
            ObsidianAddon obsidianAddon = new ObsidianAddon(obsidianAddonInfo, newInfoFile);
            if (obsidianAddon.getConfigPackInfo().addon != null && !ObsidianAddonLoader.OBSIDIAN_ADDONS.containsId(Const.id(obsidianAddon.getConfigPackInfo().addon.id))
                    && obsidianAddon.getConfigPackInfo().version == ObsidianAddonLoader.SCHEMA_VERSION) {
                Obsidian.registerInRegistry(ObsidianAddonLoader.OBSIDIAN_ADDONS, obsidianAddon.getConfigPackInfo().addon.id, obsidianAddon);
                Obsidian.LOGGER.info("Registering obsidian addon: {}", obsidianAddon.getConfigPackInfo().addon.name);
            } else if(obsidianAddon.getConfigPackInfo().version != ObsidianAddonLoader.SCHEMA_VERSION) {
                Obsidian.LOGGER.warn("Found incompatible obsidian addon: {} with a version of {}", obsidianAddon.getConfigPackInfo().addon.name, obsidianAddon.getConfigPackInfo().addon.version);
            } else {
                Obsidian.LOGGER.error("Found obsidian addon with invalid info file: {} with a version of {}", obsidianAddon.getConfigPackInfo().addon.name, obsidianAddon.getConfigPackInfo().addon.version);
            }
        }
    }

    /*public static void registerAddon(Reader reader) {
        ObsidianAddonInfo obsidianAddonInfo = Obsidian.GSON.fromJson(reader, ObsidianAddonInfo.class);
        ObsidianAddon obsidianAddon = new ObsidianAddon(obsidianAddonInfo);
        if (!ObsidianAddonLoader.OBSIDIAN_ADDONS.containsId(Const.id(obsidianAddon.getConfigPackInfo().addon.id)) &&
                obsidianAddon.getConfigPackInfo().legacyVersion == ObsidianAddonLoader.SCHEMA_VERSION) {
            Obsidian.registerInRegistry(ObsidianAddonLoader.OBSIDIAN_ADDONS, obsidianAddon.getConfigPackInfo().addon.id, obsidianAddon);
            Obsidian.LOGGER.info(String.format("Registering obsidian addon: {}", obsidianAddon.getConfigPackInfo().addon.name));
        } else {
            Obsidian.LOGGER.info(String.format("Found incompatible obsidian addon: {} with a version of %d", obsidianAddon.getConfigPackInfo().addon.name, obsidianAddon.getConfigPackInfo().legacyVersion));
        }
    }*/

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
        //noinspection unchecked
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

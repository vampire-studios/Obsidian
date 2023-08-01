package io.github.vampirestudios.obsidian.utils;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import io.github.cottonmc.jankson.JanksonFactory;
import io.github.vampirestudios.obsidian.*;
import io.github.vampirestudios.obsidian.configPack.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.fabricmc.loader.api.metadata.ModEnvironment;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.metadata.DependencyOverrides;
import net.fabricmc.loader.impl.metadata.ParseMetadataException;
import net.fabricmc.loader.impl.metadata.VersionOverrides;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Utils {

    public static void registerAddon(File legacyInfoFile, File newInfoFile, Optional<File> fabricModJson) throws SyntaxError, IOException {
        if (legacyInfoFile.exists()) {
            LegacyObsidianAddonInfo obsidianAddonInfo = Obsidian.GSON.fromJson(new FileReader(legacyInfoFile), LegacyObsidianAddonInfo.class);
            LegacyObsidianAddon obsidianAddon = new LegacyObsidianAddon(obsidianAddonInfo, legacyInfoFile);
            if (obsidianAddonInfo.addonVersion != ObsidianAddonLoader.SCHEMA_VERSION) {
                Obsidian.LOGGER.info("Found incompatible obsidian addon: {} with a version of {}", obsidianAddonInfo.displayName, obsidianAddonInfo.addonVersion);
                return;
            }
            if (!ObsidianAddonLoader.OBSIDIAN_ADDONS.containsKey(Const.id(obsidianAddonInfo.namespace))) {
                Obsidian.registerInRegistry(ObsidianAddonLoader.OBSIDIAN_ADDONS, obsidianAddonInfo.namespace, obsidianAddon);
                Obsidian.LOGGER.info("Registering obsidian addon: {}", obsidianAddonInfo.displayName);
            } else {
                Obsidian.LOGGER.error("Found obsidian addon with invalid info file: {} with a version of {}", obsidianAddonInfo.displayName, obsidianAddonInfo.addonVersion);
            }

            if (fabricModJson.isPresent()) {
                try {
                    FabricLoaderInterface.addMod(FabricLoaderImpl.INSTANCE, FabricLoaderInterface.createPlain(
                            Path.of("obsidian_addons/" + obsidianAddonInfo.folderName),
                            ModMetadataParser.parseMetadata(Files.newInputStream(fabricModJson.get().toPath()), "obsidian_addons/" + obsidianAddonInfo.folderName,
                                    Collections.emptyList(), new VersionOverrides(), new DependencyOverrides(FabricLoader.getInstance().getConfigDir()), false),
                            false,
                            Collections.emptyList()
                    ));
                } catch (ParseMetadataException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    FabricLoaderInterface.addMod(FabricLoaderImpl.INSTANCE, FabricLoaderInterface.createPlain(
                            Path.of("obsidian_addons/" + obsidianAddonInfo.folderName),
                            new V1ModMetadata(
                                    obsidianAddonInfo.namespace,
                                    Version.parse(obsidianAddonInfo.version),
                                    List.of(),
                                    ModEnvironment.UNIVERSAL,
                                    Map.of(),
                                    List.of(),
                                    List.of(),
                                    null,
                                    List.of(),
                                    false,
                                    obsidianAddonInfo.displayName,
                                    obsidianAddonInfo.description,
                                    obsidianAddonInfo.getAuthors(),
                                    List.of(),
                                    ContactInformation.EMPTY,
                                    List.of(),
                                    V1ModMetadata.NO_ICON,
                                    Map.of(),
                                    Map.of()
                            ), false,
                            List.of()
                    ));
                } catch (VersionParsingException ignored) {
                }
            }
        }
        if (newInfoFile.exists()) {
            JsonObject jsonObject = JanksonFactory.builder().build().load(newInfoFile);
            ObsidianAddonInfo obsidianAddonInfo = JanksonFactory.builder().build().fromJson(jsonObject, ObsidianAddonInfo.class);
            ObsidianAddon obsidianAddon = new ObsidianAddon(obsidianAddonInfo, newInfoFile);
            if (obsidianAddonInfo.version != ObsidianAddonLoader.SCHEMA_VERSION) {
                Obsidian.LOGGER.info("Found incompatible obsidian addon: {} with a version of {}", obsidianAddonInfo.addon.name, obsidianAddonInfo.version);
                return;
            }
            if (obsidianAddonInfo.addon != null && !ObsidianAddonLoader.OBSIDIAN_ADDONS.containsKey(Const.id(obsidianAddonInfo.addon.id))
                    && obsidianAddonInfo.version == ObsidianAddonLoader.SCHEMA_VERSION) {
                Obsidian.registerInRegistry(ObsidianAddonLoader.OBSIDIAN_ADDONS, obsidianAddonInfo.addon.id, obsidianAddon);
                Obsidian.LOGGER.info("Registering obsidian addon: {}", obsidianAddonInfo.addon.name);
            } else if (obsidianAddonInfo.version != ObsidianAddonLoader.SCHEMA_VERSION) {
                Obsidian.LOGGER.warn("Found incompatible obsidian addon: {} with a version of {}", obsidianAddonInfo.addon.name, obsidianAddonInfo.addon.version);
            } else {
                Obsidian.LOGGER.error("Found obsidian addon with invalid info file: {} with a version of {}", obsidianAddonInfo.addon.name, obsidianAddonInfo.addon.version);
            }

            if (fabricModJson.isPresent()) {
                try {
                    FabricLoaderInterface.addMod(FabricLoaderImpl.INSTANCE, FabricLoaderInterface.createPlain(
                            Path.of("obsidian_addons/" + obsidianAddonInfo.addon.folderName),
                            ModMetadataParser.parseMetadata(Files.newInputStream(fabricModJson.get().toPath()), "obsidian_addons/" + obsidianAddonInfo.addon.folderName,
                                    Collections.emptyList(), new VersionOverrides(), new DependencyOverrides(FabricLoader.getInstance().getConfigDir()), false),
                            false,
                            List.of()
                    ));
                } catch (ParseMetadataException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    FabricLoaderInterface.addMod(FabricLoaderImpl.INSTANCE, FabricLoaderInterface.createPlain(
                            Path.of("obsidian_addons/" + obsidianAddonInfo.addon.folderName),
                            new V1ModMetadata(
                                    obsidianAddonInfo.addon.id,
                                    Version.parse(obsidianAddonInfo.addon.version),
                                    List.of(),
                                    ModEnvironment.UNIVERSAL,
                                    Map.of(),
                                    List.of(),
                                    List.of(),
                                    null,
                                    List.of(),
                                    false,
                                    obsidianAddonInfo.addon.name,
                                    obsidianAddonInfo.addon.description,
                                    obsidianAddonInfo.addon.getAuthors(),
                                    List.of(),
                                    ContactInformation.EMPTY,
                                    List.of(),
                                    V1ModMetadata.NO_ICON,
                                    Map.of(),
                                    Map.of()
                            ), false,
                            List.of()
                    ));
                } catch (VersionParsingException ignored) {
                }
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

    public static ResourceLocation appendToPath(ResourceLocation identifier, String suffix) {
        return new ResourceLocation(identifier.getNamespace(), identifier.getPath() + suffix);
    }

    public static ResourceLocation prependToPath(ResourceLocation identifier, String prefix) {
        return new ResourceLocation(identifier.getNamespace(), prefix + identifier.getPath());
    }

    public static ResourceLocation appendAndPrependToPath(ResourceLocation identifier, String prefix, String suffix) {
        return new ResourceLocation(identifier.getNamespace(), prefix + identifier.getPath() + suffix);
    }

    public static <T> T[] stripNulls(T[] arr) {
        int i = 0;
        for (T t : arr) {
            if (t != null) {
                i++;
            }
        }
        //noinspection unchecked
        T[] newArr = (T[]) Array.newInstance(arr.getClass().getComponentType(), i);
        i = 0;
        for (T t : arr) {
            if (t != null) {
                newArr[i] = t;
                i++;
            }
        }
        return newArr;
    }

}

package io.github.vampirestudios.obsidian;

import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.discovery.ModCandidate;
import net.fabricmc.loader.impl.metadata.LoaderModMetadata;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class FabricLoaderInterface {
    // hippity-hoppitied from ModsMod
    private static final Method ADD_MOD_METHOD;
    private static final Method CREATE_PLAIN_METHOD;
    private static final Field MODS_FIELD;

    static {
        try {
            ADD_MOD_METHOD = FabricLoaderImpl.class.getDeclaredMethod("addMod", ModCandidate.class);
            ADD_MOD_METHOD.setAccessible(true);

            MODS_FIELD = FabricLoaderImpl.class.getDeclaredField("mods");
            MODS_FIELD.setAccessible(true);

            CREATE_PLAIN_METHOD = ModCandidate.class.getDeclaredMethod("createPlain", List.class, LoaderModMetadata.class, boolean.class, Collection.class);
            CREATE_PLAIN_METHOD.setAccessible(true);
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new IllegalStateException("failed to reflect addMod/createPlain/mods - fabric loader unsupported?", e);
        }
    }

    public static void addMod(FabricLoaderImpl fabricLoader, ModCandidate candidate) {
        try {
            ADD_MOD_METHOD.invoke(fabricLoader, candidate);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("Failed to inject mod", e);
        }
    }

    public static ModCandidate createPlain(Path path, LoaderModMetadata metadata, boolean requiresRemap, Collection<ModCandidate> nestedMods) {
        try {
            return (ModCandidate) CREATE_PLAIN_METHOD.invoke(null, List.of(path), metadata, requiresRemap, nestedMods);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create plain mod container", e);
        }
    }
}
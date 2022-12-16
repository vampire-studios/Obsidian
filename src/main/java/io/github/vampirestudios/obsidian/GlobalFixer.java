package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.api.RegistryEntryDeletedCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public final class GlobalFixer<T> {
    private final Registry<T> registry;

    private GlobalFixer(Registry<T> registry) {
        this.registry = registry;
    }

    public static void init() {
        for (Registry<?> registry : Registries.REGISTRIES) {
            new GlobalFixer<>(registry).register();
        }
    }

    private void register() {
        RegistryEntryAddedCallback.event(registry).register(this::onEntryAdded);
        RegistryEntryDeletedCallback.event(registry).register(this::onEntryDeleted);
    }

    private void onEntryDeleted(int rawId, RegistryEntry.Reference<?> entry) {
        ((DeletableObjectInternal) entry).obsidian$setDeleted(true);
        if (entry.comp_349() instanceof DeletableObjectInternal obj) obj.obsidian$setDeleted(true);
    }

    private void onEntryAdded(int rawId, Identifier id, Object obj) {
        ((DeletableObjectInternal) registry.getEntry(rawId).orElseThrow()).obsidian$setDeleted(false);
        if (obj instanceof DeletableObjectInternal doi) doi.obsidian$setDeleted(false);
    }
}
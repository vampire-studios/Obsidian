package io.github.vampirestudios.obsidian;

import io.github.vampirestudios.obsidian.api.RegistryEntryDeletedCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class GlobalFixer<T> {
    private final Registry<T> registry;

    private GlobalFixer(Registry<T> registry) {
        this.registry = registry;
    }

    public static void init() {
        for (Registry<?> registry : BuiltInRegistries.REGISTRY) {
            new GlobalFixer<>(registry).register();
        }
    }

    private void register() {
        RegistryEntryAddedCallback.event(registry).register(this::onEntryAdded);
        RegistryEntryDeletedCallback.event(registry).register(this::onEntryDeleted);
    }

    private void onEntryDeleted(int rawId, Holder.Reference<?> entry) {
        ((DeletableObjectInternal) entry).obsidian$setDeleted(true);
        if (entry.value() instanceof DeletableObjectInternal obj) obj.obsidian$setDeleted(true);
    }

    private void onEntryAdded(int rawId, ResourceLocation id, Object obj) {
        ((DeletableObjectInternal) registry.getHolder(rawId).orElseThrow()).obsidian$setDeleted(false);
        if (obj instanceof DeletableObjectInternal doi) doi.obsidian$setDeleted(false);
    }
}
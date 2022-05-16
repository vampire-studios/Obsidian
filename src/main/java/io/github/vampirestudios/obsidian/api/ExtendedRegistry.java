package io.github.vampirestudios.obsidian.api;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.util.registry.RegistryKey;

public interface ExtendedRegistry<T> {
    Event<RegistryEntryDeletedCallback<T>> obsidian$getEntryDeletedEvent();

    void obsidian$remove(RegistryKey<T> key);

    void obsidian$unfreeze();
}
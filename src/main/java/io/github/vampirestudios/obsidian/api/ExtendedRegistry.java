package io.github.vampirestudios.obsidian.api;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.resources.ResourceKey;

public interface ExtendedRegistry<T> {
    Event<RegistryEntryDeletedCallback<T>> obsidian$getEntryDeletedEvent();

    void obsidian$remove(ResourceKey<T> key);

    void obsidian$unfreeze();
}
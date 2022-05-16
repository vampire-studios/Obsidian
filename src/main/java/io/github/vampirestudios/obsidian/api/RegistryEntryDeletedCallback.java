package io.github.vampirestudios.obsidian.api;

import net.fabricmc.fabric.api.event.AutoInvokingEvent;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.util.Holder;
import net.minecraft.util.registry.Registry;

public interface RegistryEntryDeletedCallback<T> {
    void onEntryDeleted(int rawId, Holder.Reference<T> entry);

    @SuppressWarnings("unchecked")
    @AutoInvokingEvent
    static <T> Event<RegistryEntryDeletedCallback<T>> event(Registry<T> registry) {
        return ((ExtendedRegistry<T>) registry).obsidian$getEntryDeletedEvent();
    }
}
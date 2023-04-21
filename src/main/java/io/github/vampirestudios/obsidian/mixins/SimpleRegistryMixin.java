package io.github.vampirestudios.obsidian.mixins;

import com.mojang.serialization.Lifecycle;
import io.github.vampirestudios.obsidian.api.ExtendedRegistry;
import io.github.vampirestudios.obsidian.api.ExtendedRegistryEntryReference;
import io.github.vampirestudios.obsidian.api.RegistryEntryDeletedCallback;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.registry.RegistryEntryRemovedCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(MappedRegistry.class)
public abstract class SimpleRegistryMixin<T> implements ExtendedRegistry<T>, Registry<T> {
	@Shadow
	private boolean frozen;
	@Shadow @Nullable private Map<T, Holder.Reference<T>> intrusiveValueToEntry;

	@Shadow public abstract Optional<Holder.Reference<T>> getHolder(ResourceKey<T> key);

	@Shadow @Final private Object2IntMap<T> entryToRawId;
	@Shadow @Final private ObjectList<Holder.Reference<T>> rawIdToEntry;
	@Shadow @Final private Map<ResourceLocation, Holder.Reference<T>> idToEntry;
	@Shadow @Final private Map<ResourceKey<T>, Holder.Reference<T>> keyToEntry;
	@Shadow @Final private Map<T, Holder.Reference<T>> valueToEntry;
	@Shadow @Final private Map<T, Lifecycle> entryToLifecycle;
	@Shadow @Nullable private List<Holder.Reference<T>> cachedEntries;

	@Shadow public abstract Optional<Holder.Reference<T>> getHolder(int i);

	@SuppressWarnings("unchecked") private final Event<RegistryEntryDeletedCallback<T>> obsidian$entryDeletedEvent = EventFactory.createArrayBacked(RegistryEntryDeletedCallback.class, callbacks -> (rawId, entry) -> {
		for (var callback : callbacks) {
			callback.onEntryDeleted(rawId, entry);
		}

		if (entry.value() instanceof RegistryEntryDeletedCallback<?> callback)
			((RegistryEntryDeletedCallback<T>)callback).onEntryDeleted(rawId, entry);
	});

	@Override
	public Event<RegistryEntryDeletedCallback<T>> obsidian$getEntryDeletedEvent() {
		return obsidian$entryDeletedEvent;
	}

	@Override
	public void obsidian$remove(ResourceKey<T> key) {
		if (frozen) {
			throw new IllegalStateException("Registry is frozen (trying to remove key " + key + ")");
		}

		Holder.Reference<T> entry = getHolder(key).orElseThrow();
		int rawId = entryToRawId.getInt(entry.value());
		RegistryEntryRemovedCallback.event(this).invoker().onEntryRemoved(rawId, entry.key().location(), entry.value());
		obsidian$entryDeletedEvent.invoker().onEntryDeleted(rawId, entry);

		rawIdToEntry.set(rawId, null);
		entryToRawId.removeInt(entry);
		idToEntry.remove(key.location());
		keyToEntry.remove(key);
		valueToEntry.remove(entry.value());
		entryToLifecycle.remove(entry.value());
		cachedEntries = null;

		((ExtendedRegistryEntryReference) entry).obsidian$poison();
	}

	@Override
	public void obsidian$unfreeze() {
		frozen = false;
//		if (customHolderProvider != null)
		this.intrusiveValueToEntry = new IdentityHashMap<>();
	}

	@Inject(method = "freeze", at = @At("HEAD"), cancellable = true)
	private void disableThatThing(CallbackInfoReturnable<Registry<T>> cir) {
		cir.setReturnValue(this);
	}
}

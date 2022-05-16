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
import net.minecraft.util.Holder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
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
import java.util.function.Function;

@Mixin(SimpleRegistry.class)
public abstract class SimpleRegistryMixin<T> extends Registry<T> implements ExtendedRegistry<T> {
	@Shadow
	private boolean frozen;
	@Shadow @Final
	@Nullable
	private Function<T, Holder.Reference<T>> customHolderProvider;
	@Shadow @Nullable private Map<T, Holder.Reference<T>> intrusiveHolderCache;

	protected SimpleRegistryMixin(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle) {
		super(key, lifecycle);
	}

	@Shadow public abstract Optional<Holder<T>> getHolder(RegistryKey<T> key);

	@Shadow @Final private Object2IntMap<T> entryToRawId;
	@Shadow @Final private ObjectList<Holder.Reference<T>> rawIdToEntry;
	@Shadow @Final private Map<Identifier, Holder.Reference<T>> byId;
	@Shadow @Final private Map<RegistryKey<T>, Holder.Reference<T>> byKey;
	@Shadow @Final private Map<T, Holder.Reference<T>> byValue;
	@Shadow @Final private Map<T, Lifecycle> entryToLifecycle;
	@Shadow @Nullable private List<Holder.Reference<T>> holdersInOrder;

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
	public void obsidian$remove(RegistryKey<T> key) {
		if (frozen) {
			throw new IllegalStateException("Registry is frozen (trying to remove key " + key + ")");
		}

		Holder.Reference<T> entry = (Holder.Reference<T>) getHolder(key).orElseThrow();
		int rawId = entryToRawId.getInt(entry.value());
		RegistryEntryRemovedCallback.event(this).invoker().onEntryRemoved(rawId, entry.getRegistryKey().getValue(), entry.value());
		obsidian$entryDeletedEvent.invoker().onEntryDeleted(rawId, entry);

		rawIdToEntry.set(rawId, null);
		entryToRawId.removeInt(entry);
		byId.remove(key.getValue());
		byKey.remove(key);
		byValue.remove(entry.value());
		entryToLifecycle.remove(entry.value());
		holdersInOrder = null;

		((ExtendedRegistryEntryReference) entry).obsidian$poison();
	}

	@Override
	public void obsidian$unfreeze() {
		frozen = false;

		if (customHolderProvider != null)
			this.intrusiveHolderCache = new IdentityHashMap<>();
	}

	@Inject(method = "freeze", at = @At("HEAD"), cancellable = true)
	private void disableThatThing(CallbackInfoReturnable<Registry<T>> cir) {
		cir.setReturnValue((Registry<T>) (Object)this);
	}
}

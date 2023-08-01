package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {
    @Shadow
    private boolean frozen;
    @Shadow
    @Final
    private Map<T, Holder.Reference<T>> byValue;
    @Shadow
    @Final
    private Map<ResourceKey<T>, Holder.Reference<T>> byKey;

    @Shadow
    public abstract ResourceKey<? extends Registry<T>> key();

    @Shadow
    private @Nullable Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;

    /**
     * @author idk
     * @reason idk
     */
    @Overwrite
    public Registry<T> freeze() {
        if (this.frozen) {
            return (Registry<T>) this;
        } else {
            this.frozen = true;
            this.byValue.forEach((object, reference) -> reference.bindValue(object));
            List<ResourceLocation> list = this.byKey.entrySet().stream().filter((entry) -> !entry.getValue().isBound()).map((entry) -> entry.getKey().location())
                    .sorted().toList();
            if (!list.isEmpty()) {
                ResourceKey var10002 = this.key();
                throw new IllegalStateException("Unbound values in registry " + var10002 + ": " + list);
            } else {
                if (this.unregisteredIntrusiveHolders != null) {
                    if (!this.unregisteredIntrusiveHolders.isEmpty()) {
                        String blockNames = unregisteredIntrusiveHolders.values().stream()
                                .map((block) ->
                                        block.key().location().toString())
                                .collect(Collectors.joining());
                        System.out.printf(blockNames);
                        throw new IllegalStateException("Some intrusive holders were not registered: " + this.unregisteredIntrusiveHolders.values());
                    }

                    this.unregisteredIntrusiveHolders = null;
                }

                return (Registry<T>) this;
            }
        }
    }
}

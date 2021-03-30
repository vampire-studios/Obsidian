package io.github.vampirestudios.obsidian.mixins;

import com.google.common.collect.ImmutableMap;
import io.github.vampirestudios.obsidian.api.obsidian.CustomDynamicRegistry;
import io.github.vampirestudios.obsidian.api.obsidian.DynamicRegistryProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(DynamicRegistryManager.class)
public class DynamicRegistryManagerMixin {
    @Inject(method = "net/minecraft/util/registry/DynamicRegistryManager.method_30531()Lcom/google/common/collect/ImmutableMap;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/DynamicRegistryManager;register(Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/util/registry/RegistryKey;Lcom/mojang/serialization/Codec;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void registerCustomDynamicRegistries(CallbackInfoReturnable<ImmutableMap<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>>> ci, ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> builder) {
        List<DynamicRegistryProvider> providers = FabricLoader.getInstance().getEntrypoints("dynamic-registry-provider", DynamicRegistryProvider.class);
        for (DynamicRegistryProvider provider : providers) {
            provider.addDynamicRegistries((customDynamicRegistry) -> {
                addRegistry(customDynamicRegistry);
                builder.put(customDynamicRegistry.getRegistryRef(), getInfo(customDynamicRegistry));
            });
        }
    }

    @Unique
    private static <T> DynamicRegistryManager.Info<T> getInfo(CustomDynamicRegistry<T> customDynamicRegistry) {
        return new DynamicRegistryManager.Info<>(customDynamicRegistry.getRegistryRef(), customDynamicRegistry.getCodec(), null);
    }

    @Unique
    private static <T> void addRegistry(CustomDynamicRegistry<T> customDynamicRegistry) {
        BuiltinRegistries.addRegistry(customDynamicRegistry.getRegistryRef(), customDynamicRegistry.getRegistry(), customDynamicRegistry.getDefaultValueSupplier(), customDynamicRegistry.getLifecycle());
    }

}
package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {
    @Inject(method = "validateWrite()V", at = @At("HEAD"), cancellable = true)
    private void disableThatThing(CallbackInfo ci) {
        ci.cancel();
    }
    @Inject(method = "validateWrite(Lnet/minecraft/resources/ResourceKey;)V", at = @At("HEAD"), cancellable = true)
    private void disableThatThing1(CallbackInfo ci) {
        ci.cancel();
    }
}

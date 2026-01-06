package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.core.MappedRegistry.class)
public class MappedRegistryDebugMixin<T> {
    @Shadow private boolean frozen;
    @Shadow
    @Final
    private ResourceKey<? extends Registry<T>> key;

    @Inject(method = "prepareTagReload", at = @At("HEAD"))
    private void yourmod$debugPrepareTagReload(TagLoader.LoadResult<T> loadResult, CallbackInfoReturnable<Registry.PendingTags<T>> cir) {
        if (!this.frozen) {
            System.err.println(STR."[TAG-DEBUG] Registry NOT frozen during tag reload: \{this.key}");
        }
    }
}

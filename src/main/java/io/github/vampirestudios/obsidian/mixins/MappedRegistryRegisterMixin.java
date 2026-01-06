/*
package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryRegisterMixin {
    */
/** Redirect the internal "check not frozen" guard to always pass. *//*

    @Redirect(
        method = "register(Lnet/minecraft/resources/ResourceKey;Ljava/lang/Object;Lnet/minecraft/resources/Identifier;)Ljava/lang/Object;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/core/MappedRegistry;checkRegistryFrozen(Ljava/lang/Object;)V")
    )
    private void yourmod$skipFrozenCheck(Object self, Object value) {
        // no-op
    }
}
*/

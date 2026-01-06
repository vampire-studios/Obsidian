package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.minecraft.ScopeComponent;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin {

    // For 1.21.x, Yarn exposes getFovMultiplier; signature differs across point releases.
    // If your mappings show getFovMultiplier() with params, adjust the method descriptor accordingly.
    @Inject(method = "getFieldOfViewModifier", at = @At("RETURN"), cancellable = true)
    private void yourmod$fovMultiplier(CallbackInfoReturnable<Float> cir) {
        AbstractClientPlayer self = (AbstractClientPlayer)(Object)this;
        if (!self.isUsingItem()) return;

        ItemStack active = self.getActiveItem();
        ScopeComponent sc = active.get(OItemComponents.SCOPE);
        if (sc == null) return;

        cir.setReturnValue(cir.getReturnValue() * sc.zoomMultiplier());
    }
}

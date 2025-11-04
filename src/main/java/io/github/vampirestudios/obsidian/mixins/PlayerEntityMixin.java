package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.events.PlayerTickCallback;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void hookPlayerTick(CallbackInfo ci) {
        // Fire off our filthy tick callback BEFORE vanilla does its shit.
        PlayerTickCallback.EVENT.invoker().onPlayerTick((Player)(Object)this);
    }
}

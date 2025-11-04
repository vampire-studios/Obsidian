package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.events.PlayerPickupItemCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(
        method = "playerTouch",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hookOnPickup(Player player, CallbackInfo ci) {
        InteractionResult result = PlayerPickupItemCallback.EVENT
            .invoker()
            .interact(player, (ItemEntity)(Object)this);
        if (result == InteractionResult.FAIL) {
            ci.cancel(); // abort pickup
        }
    }
}
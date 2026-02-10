package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerInventoryMixin {

    @Inject(method = "initMenu(Lnet/minecraft/world/inventory/AbstractContainerMenu;)V", at = @At("TAIL"))
    private void crucible$invOpen(AbstractContainerMenu menu, CallbackInfo ci) {
        ServerPlayer sp = (ServerPlayer) (Object) this;

        CrucibleEvents.fire(
                SkillTrigger.INVENTORY_OPEN,
                SkillContext.builder(sp).level(sp.level()).build()
        );
    }

    @Inject(method = "doCloseContainer()V", at = @At("HEAD"))
    private void crucible$invClose(CallbackInfo ci) {
        ServerPlayer sp = (ServerPlayer) (Object) this;

        CrucibleEvents.fire(
                SkillTrigger.INVENTORY_CLOSE,
                SkillContext.builder(sp).level(sp.level()).build()
        );
    }
}

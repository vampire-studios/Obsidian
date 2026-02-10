package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerDropMixin {

    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("RETURN")
    )
    private void crucible$itemDrop(ItemStack stack, boolean dropAround, boolean includeThrowerName, CallbackInfoReturnable<ItemEntity> cir) {
        ServerPlayer sp = (ServerPlayer) (Object) this;
        ItemEntity itemEntity = cir.getReturnValue();
        if (itemEntity == null) return;

        CrucibleEvents.fire(
                SkillTrigger.ITEM_DROP,
                SkillContext.builder(sp)
                        .level(sp.level())
                        .stack(stack)
                        .build()
        );
    }
}

package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class PlayerPickupMixin {
    @Inject(method = "onItemPickup", at = @At("TAIL"))
    private void crucible$afterTake(ItemEntity itemEntity, CallbackInfo ci) {
        LivingEntity p = (LivingEntity)(Object)this;
        if (!(p instanceof Player sp)) return;
        if (!(sp.level() instanceof ServerLevel level)) return;

        ItemStack stack = itemEntity.getItem();
        CrucibleEvents.fire(SkillTrigger.ITEM_PICKUP,
                SkillContext.builder(sp).level(level).stack(stack).build()
        );
    }
}

package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantResultSlot.class)
public abstract class MerchantTradeMixin {
    @Inject(method = "onTake", at = @At("TAIL"))
    private void crucible$afterTrade(Player player, ItemStack stack, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer sp)) return;
        if (!(sp.level() instanceof ServerLevel level)) return;

        CrucibleEvents.fire(SkillTrigger.TRADE,
                SkillContext.builder(sp).level(level).stack(stack).build()
        );
    }
}

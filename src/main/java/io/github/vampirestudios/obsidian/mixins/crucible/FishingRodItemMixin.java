package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingRodItem.class)
public class FishingRodItemMixin {

    @Inject(
            method = "use",
            at = @At("TAIL")
    )
    private void crucible$fishCast(Level level, Player player, InteractionHand hand,
                                   CallbackInfoReturnable<InteractionResult> cir) {
        if (level.isClientSide()) return;
        if (!(player instanceof ServerPlayer sp)) return;

        CrucibleEvents.fire(
                SkillTrigger.FISH,
                SkillContext.builder(sp)
                        .level(sp.level())
                        .hand(hand)
                        .stack(sp.getItemInHand(hand))
                        .build()
        );
    }
}

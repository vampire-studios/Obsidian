package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketUseMixin {
    @Inject(method = "use", at = @At("RETURN"))
    private void crucible$afterBucketUse(Level level, Player player, InteractionHand hand,
                                         CallbackInfoReturnable<InteractionResult> cir) {
        if (level.isClientSide()) return;
        if (!(level instanceof ServerLevel sl)) return;

        // Only if it did something meaningful
        if (!cir.getReturnValue().consumesAction()) return;

        CrucibleEvents.fire(SkillTrigger.BUCKET,
                SkillContext.builder(player)
                        .level(sl)
                        .hand(hand)
                        .stack(player.getItemInHand(hand))
                        .build()
        );
    }
}

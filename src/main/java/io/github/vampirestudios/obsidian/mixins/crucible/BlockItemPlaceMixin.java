package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemPlaceMixin {
    @Inject(
            method = "place",
            at = @At("RETURN")
    )
    private void crucible$afterPlace(BlockPlaceContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
        // Only after a successful place
        if (!cir.getReturnValue().consumesAction()) return;
        if (!(ctx.getPlayer() instanceof ServerPlayer sp)) return;
        if (!(ctx.getLevel() instanceof ServerLevel sl)) return;

        BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
        CrucibleEvents.fire(SkillTrigger.BLOCK_PLACE,
                SkillContext.builder(sp)
                        .level(sl)
                        .hand(ctx.getHand())
                        .stack(ctx.getItemInHand())
                        .position(pos)
                        .build()
        );
    }
}

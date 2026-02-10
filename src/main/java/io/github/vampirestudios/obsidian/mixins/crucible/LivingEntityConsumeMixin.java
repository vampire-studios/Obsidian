package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityConsumeMixin {

    @Inject(method = "completeUsingItem", at = @At("TAIL"))
    private void crucible$afterCompleteUsingItem(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof ServerPlayer sp)) return;
        if (!(sp.level() instanceof ServerLevel level)) return;

        ItemStack used = sp.getActiveItem();
        CrucibleEvents.fire(SkillTrigger.CONSUME,
                SkillContext.builder(sp)
                        .level(level)
                        .stack(used)
                        .build()
        );
    }
}

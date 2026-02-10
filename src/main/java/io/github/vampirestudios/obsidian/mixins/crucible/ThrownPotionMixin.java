package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownSplashPotion.class)
public class ThrownPotionMixin {

    @Inject(method = "onHitAsPotion", at = @At("TAIL"))
    private void crucible$splashPotion(CallbackInfo ci) {
        ThrownSplashPotion potion = (ThrownSplashPotion) (Object) this;
        if (!(potion.level() instanceof ServerLevel sl)) return;

        Entity owner = potion.getOwner();
        if (!(owner instanceof LivingEntity caster)) return;

        CrucibleEvents.fire(
                SkillTrigger.SPLASH_POTION,
                SkillContext.builder(caster)
                        .level(sl)
                        .projectile(potion)
                        .build()
        );
    }
}

package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class ProjectileThrowMixin {
    @Inject(method = "shootFromRotation", at = @At("TAIL"))
    private void crucible$onShootFromRotation(Entity owner, float xRot, float yRot, float yOffset, float velocity, float inaccuracy, CallbackInfo ci) {
        Projectile proj = (Projectile) (Object) this;
        if (!(proj.level() instanceof ServerLevel level)) return;

        if (owner instanceof ServerPlayer sp) {
            CrucibleEvents.fire(SkillTrigger.PROJECTILE_THROW,
                    SkillContext.builder(sp)
                            .level(level)
                            .projectile(proj)
                            .build()
            );

            // Optional: SHOOT trigger as well (if you differentiate bow vs throw later, you can refine)
            CrucibleEvents.fire(SkillTrigger.SHOOT,
                    SkillContext.builder(sp)
                            .level(level)
                            .projectile(proj)
                            .build()
            );
        }
    }
}

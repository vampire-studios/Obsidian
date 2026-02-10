package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDieMixin {
    @Inject(method = "die", at = @At("HEAD"))
    private void crucible$onDie(DamageSource source, CallbackInfo ci) {
        LivingEntity victim = (LivingEntity) (Object) this;
        if (!(victim.level() instanceof ServerLevel level)) return;

        // Victim DEATH


        if (victim instanceof ServerPlayer) {
            CrucibleEvents.fire(SkillTrigger.PLAYERDEATH,
                    SkillContext.builder(victim).level(level).build()
            );
        }

        // Attacker KILL / KILLPLAYER
        var attacker = source.getEntity();
        if (attacker instanceof ServerPlayer killer) {
            CrucibleEvents.fire(SkillTrigger.KILL,
                    SkillContext.builder(killer).level(level).target(victim).build()
            );
        }
    }
}

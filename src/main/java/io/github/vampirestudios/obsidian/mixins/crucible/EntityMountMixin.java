package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMountMixin {

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z", at = @At("RETURN"))
    private void crucible$mount(Entity entity, boolean force, boolean sendEventAndTriggers, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

        Entity self = (Entity) (Object) this;
        if (!(self instanceof LivingEntity rider)) return;
        if (!(rider.level() instanceof ServerLevel sl)) return;

        LivingEntity mountAsTarget = (entity instanceof LivingEntity le) ? le : null;

        CrucibleEvents.fire(
                SkillTrigger.MOUNT,
                SkillContext.builder(rider)
                        .level(sl)
                        .target(mountAsTarget)
                        .build()
        );
    }

    @Inject(method = "stopRiding()V", at = @At("HEAD"))
    private void crucible$unmount(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!(self instanceof LivingEntity rider)) return;
        if (!(rider.level() instanceof ServerLevel sl)) return;

        Entity vehicle = rider.getVehicle();
        LivingEntity mountAsTarget = (vehicle instanceof LivingEntity le) ? le : null;

        CrucibleEvents.fire(
                SkillTrigger.DISMOUNT,
                SkillContext.builder(rider)
                        .level(sl)
                        .target(mountAsTarget)
                        .build()
        );
    }

    @Inject(method = "stopRiding()V", at = @At("RETURN"))
    private void crucible$unmounted(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!(self instanceof LivingEntity rider)) return;
        if (!(rider.level() instanceof ServerLevel sl)) return;

        CrucibleEvents.fire(
                SkillTrigger.DISMOUNTED,
                SkillContext.builder(rider)
                        .level(sl)
                        .build()
        );
    }
}

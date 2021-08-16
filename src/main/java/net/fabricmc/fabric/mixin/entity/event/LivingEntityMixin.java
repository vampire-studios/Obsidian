package net.fabricmc.fabric.mixin.entity.event;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract Optional<BlockPos> getSleepingPosition();

    @Inject(method = "sleep", at = @At("RETURN"))
    private void onSleep(BlockPos pos, CallbackInfo info) {
        EntitySleepEvents.START_SLEEPING.invoker().onStartSleeping((LivingEntity) (Object) this, pos);
    }

    @Inject(method = "wakeUp", at = @At("HEAD"))
    private void onWakeUp(CallbackInfo info) {
        // If actually asleep - this method is often called with data loading, syncing etc. "just to be sure"
        if (getSleepingPosition().isPresent()) {
            EntitySleepEvents.STOP_SLEEPING.invoker().onStopSleeping((LivingEntity) (Object) this, getSleepingPosition().get());
        }
    }

    @Inject(method = "isSleepingInBed", at = @At("RETURN"), cancellable = true)
    private void onIsSleepingInBed(CallbackInfoReturnable<Boolean> info) {
        if (getSleepingPosition().isPresent()) {
            BlockPos sleepingPos = getSleepingPosition().get();
            BlockState bedState = ((LivingEntity) (Object) this).world.getBlockState(sleepingPos);
            ActionResult result = EntitySleepEvents.ALLOW_BED.invoker().allowBed((LivingEntity) (Object) this, sleepingPos, bedState, info.getReturnValueZ());

            if (result != ActionResult.PASS) {
                info.setReturnValue(result.isAccepted());
            }
        }
    }

    @Inject(method = "getSleepingDirection", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void onGetSleepingDirection(CallbackInfoReturnable<Direction> info, @Nullable BlockPos sleepingPos) {
        if (sleepingPos != null) {
            info.setReturnValue(EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.invoker().modifySleepDirection((LivingEntity) (Object) this, sleepingPos, info.getReturnValue()));
        }
    }
}

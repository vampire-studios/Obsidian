package org.quiltmc.qsl.item.extension.mixin.trident;

import net.minecraft.world.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TridentItem.class)
public class TridentItemMixin {

    /** Replaces the hardcoded 2.5f throw speed with ProjectileModifyingTridentItem#getThrowPower. */
    /*@ModifyArg(
            method = "releaseUsing",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownTrident;shootFromRotation(Lnet/minecraft/world/entity/Entity;FFFFF)V"),
            index = 4
    )
    private float obsidian$modifyThrowPower(float originalPower, ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if ((Object) this instanceof ProjectileModifyingTridentItem modifying) {
            return modifying.getThrowPower(stack);
        }
        return originalPower;
    }

    *//** Calls modifyThrownTrident just before the entity is added to the world. *//*
    @Inject(
            method = "releaseUsing",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void obsidian$callModifyHook(ItemStack itemStack, Level level, LivingEntity livingEntity, int i,
                                         CallbackInfoReturnable<Boolean> cir, net.minecraft.world.entity.player.Player player,
                                         int charge, int loyaltyLevel, ThrownTrident trident) {
        if ((Object) this instanceof ProjectileModifyingTridentItem modifying) {
            modifying.modifyThrownTrident(itemStack, trident);
        }
    }*/
}

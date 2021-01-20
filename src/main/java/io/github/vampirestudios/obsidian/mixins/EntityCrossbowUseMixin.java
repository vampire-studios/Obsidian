package io.github.vampirestudios.obsidian.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.RangedWeaponItem;

@Mixin({ PiglinEntity.class, PillagerEntity.class })
public class EntityCrossbowUseMixin {
	@Inject(method = "canUseRangedWeapon", at = @At("HEAD"), cancellable = true)
	public void ob_canUseRangedWeapon(RangedWeaponItem weapon, CallbackInfoReturnable<Boolean> cir) {
		if (weapon instanceof CrossbowItem) {
			cir.setReturnValue(true);
		}
	}
}
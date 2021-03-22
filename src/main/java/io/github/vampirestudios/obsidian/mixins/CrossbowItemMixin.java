package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
	@Inject(method = "createArrow", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private static void ob_createArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack projectileStack, CallbackInfoReturnable<PersistentProjectileEntity> cir, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
		if ((crossbow.getItem() instanceof CrossbowInterface)) {
			((CrossbowInterface) crossbow.getItem()).modifyShotProjectile(crossbow, entity, projectileStack, persistentProjectileEntity);
		}
	}

	//Redirecting this method in order to get the item stack and shooting entity
	@Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;shootAll(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;FF)V"))
	private void ob_shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence) {
		float _speed = stack.getItem() instanceof CrossbowInterface ? ((CrossbowInterface) stack.getItem()).getProjectileSpeed(stack, entity) : speed;
		CrossbowItem.shootAll(world, entity, hand, stack, _speed, 1.0F);
	}
}
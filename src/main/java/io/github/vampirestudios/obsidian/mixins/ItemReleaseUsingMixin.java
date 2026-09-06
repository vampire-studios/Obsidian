package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.obsidian.item.ObsidianItemHolder;
import io.github.vampirestudios.obsidian.minecraft.AmmoUtil;
import io.github.vampirestudios.obsidian.minecraft.obsidian.RangedWeaponEvents;
import io.github.vampirestudios.obsidian.minecraft.ChargeComponent;
import io.github.vampirestudios.obsidian.minecraft.ChargeMath;
import io.github.vampirestudios.obsidian.minecraft.ShooterComponent;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemReleaseUsingMixin {

	@Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
	private void yourmod$releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeLeft, CallbackInfoReturnable<Boolean> ci) {
		ShooterComponent shooter = stack.get(OItemComponents.SHOOTER);
		if (shooter == null) return;

		// We fully handle it
		ci.cancel();

		if (level.isClientSide()) return;
		if (!(user instanceof Player player)) return;

		// Compute charge ticks from vanilla "use duration - time left"
		int useDuration = ((Item) (Object) this).getUseDuration(stack, user);
		int chargeTicks = Math.max(0, useDuration - timeLeft);

		ChargeComponent charge = stack.get(OItemComponents.CHARGE);

		// Enforce min release time if a charge component exists
		if (charge != null && chargeTicks < Math.max(0, charge.minReleaseTicks())) {
			return;
		}

		// Ammo check
		AmmoUtil.Found ammoFound = null;
		if (shooter.requiresAmmo() && !player.getAbilities().instabuild) {
			ammoFound = AmmoUtil.findAmmo(player, shooter.ammo());
			if (ammoFound == null) return;
		}

		// Cooldown
		player.getCooldowns().addCooldown(stack, Math.max(0, shooter.cooldownTicks()));

		// Power multiplier from charge (or 1.0)
		float chargeMult = ChargeMath.powerMultiplier(charge, chargeTicks);
		float finalPower = shooter.power() * chargeMult;

		// Spawn projectile
		EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(shooter.projectileEntity());
		Entity created = type.create(level, EntitySpawnReason.SPAWN_ITEM_USE);
		if (!(created instanceof Projectile proj)) {
			return; // misconfigured projectile entity id
		}

		proj.setOwner(player);
		proj.setPos(
				player.getX(),
				player.getEyeY() - 0.1,
				player.getZ()
		);

		// If it is a thrown-item projectile, set its item for rendering
		if (proj instanceof ThrowableItemProjectile tip) {
			tip.setItem(stack.copyWithCount(1));
		}

		// Shoot using player rotation
		proj.shootFromRotation(
				player,
				player.getXRot(),
				player.getYRot(),
				0.0F,
				finalPower,
				shooter.inaccuracy()
		);

		level.addFreshEntity(proj);

		RangedWeaponEvents.shot(player, proj, ObsidianItemHolder.of(stack.getItem()));

		// Sound
		SoundEvent se = BuiltInRegistries.SOUND_EVENT.getValue(shooter.shootSound());
		level.playSound(null, player.blockPosition(), se, SoundSource.PLAYERS, 1.0f, 0.9f + level.getRandom().nextFloat() * 0.2f);

		// Consume ammo
		if (shooter.requiresAmmo() && shooter.consumeAmmo() && !player.getAbilities().instabuild) {
			AmmoUtil.consume(player, ammoFound, 1);
		}
	}
}

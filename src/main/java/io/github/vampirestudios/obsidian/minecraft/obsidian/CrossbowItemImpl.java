package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;

/**
 * A crossbow whose wind-up and shot speed are configurable, which is all a heavy crossbow is: slower
 * to charge, harder-hitting when it fires.
 *
 * <p>The charge time is applied through {@code CrossbowItemChargeMixin}, because vanilla reads it
 * from a static method that the charging sounds, the model's pull property and the shot itself all
 * consult — overriding one caller would desynchronise the rest.</p>
 */
public class CrossbowItemImpl extends CrossbowItem {

	public RangedWeaponItem rangedWeaponItem;

	public CrossbowItemImpl(RangedWeaponItem rangedWeaponItem, Properties settings) {
		super(settings);
		this.rangedWeaponItem = rangedWeaponItem;
	}

	/** Ticks to wind a bolt, before Quick Charge. */
	public int obsidian$chargeTicks() {
		return this.rangedWeaponItem.chargeTicks();
	}

	@Override
	protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity,
	                               float inaccuracy, float angle, LivingEntity target) {
		super.shootProjectile(shooter, projectile, index,
				velocity * this.rangedWeaponItem.velocityMultiplier(), inaccuracy, angle, target);
		RangedWeaponEvents.shot(shooter, projectile, this.rangedWeaponItem);
	}

	/**
	 * A crossbow naming {@code ammo} fires only what it names — which is how a pack gives it bolts
	 * rather than arrows. One that names none takes arrows and fireworks, as vanilla does.
	 */
	@Override
	public java.util.function.Predicate<net.minecraft.world.item.ItemStack> getAllSupportedProjectiles() {
		return AmmoPredicate.of(this.rangedWeaponItem, AmmoPredicate.arrowsOrFireworks());
	}

	@Override
	public int getDefaultProjectileRange() {
		Integer range = this.rangedWeaponItem.projectileRange;
		return range != null ? range : super.getDefaultProjectileRange();
	}

//	@Override
//	public boolean canBeDepleted() {
//		return rangedWeaponItem.damageable;
//	}
}

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A bow whose draw time is configurable, which is all a shortbow is: the same shot curve, reached
 * sooner. {@code draw_time} of {@value RangedWeaponItem#DEFAULT_SHORTBOW_DRAW_TIME} ticks is a
 * shortbow, {@value RangedWeaponItem#DEFAULT_DRAW_TIME} is a vanilla bow.
 */
public class BowItemImpl extends BowItem {

	public RangedWeaponItem rangedWeaponItem;

	private final int drawTicks;

	public BowItemImpl(RangedWeaponItem rangedWeaponItem, Properties settings) {
		super(settings);
		this.rangedWeaponItem = rangedWeaponItem;
		this.drawTicks = rangedWeaponItem.drawTicks();
	}

	/**
	 * Vanilla works out the shot's power from how many ticks the bow was held, against its own
	 * 20-tick draw. Stretching that number before handing it over keeps every other part of the
	 * shot — ammo use, sounds, stats, the full-power crit — exactly as vanilla does it.
	 */
	@Override
	public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
		if (this.drawTicks == MAX_DRAW_DURATION) return super.releaseUsing(stack, level, entity, timeLeft);

		int maxDuration = this.getUseDuration(stack, entity);
		int heldTicks = maxDuration - timeLeft;
		int scaledTicks = Math.round(heldTicks * (float) MAX_DRAW_DURATION / this.drawTicks);

		return super.releaseUsing(stack, level, entity, maxDuration - scaledTicks);
	}

	@Override
	protected void shootProjectile(LivingEntity shooter, net.minecraft.world.entity.projectile.Projectile projectile,
	                               int index, float velocity, float inaccuracy, float angle, LivingEntity target) {
		super.shootProjectile(shooter, projectile, index,
				velocity * this.rangedWeaponItem.velocityMultiplier(), inaccuracy, angle, target);
		RangedWeaponEvents.shot(shooter, projectile, this.rangedWeaponItem);
	}

	/**
	 * A bow naming {@code ammo} fires only what it names; one that names none takes ordinary arrows.
	 */
	@Override
	public java.util.function.Predicate<ItemStack> getAllSupportedProjectiles() {
		return AmmoPredicate.of(this.rangedWeaponItem, AmmoPredicate.arrows());
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

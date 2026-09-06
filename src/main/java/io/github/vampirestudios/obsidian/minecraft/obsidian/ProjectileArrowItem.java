package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.projectile.Projectile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * The item half of an arrow-shaped {@code item/projectile}: ammunition.
 *
 * <p>An {@link ArrowItem} rather than a plain item, so the whole vanilla ranged-weapon pipeline —
 * finding ammo in the inventory, consuming it, Infinity, the crossbow's loaded-projectile component —
 * works without Obsidian reimplementing any of it. Which weapons will actually fire it is decided by
 * the weapon, through {@code ammo}.
 */
public class ProjectileArrowItem extends ArrowItem {

	public final Projectile projectile;

	public ProjectileArrowItem(Projectile projectile, Properties settings) {
		super(settings);
		this.projectile = projectile;
	}

	@Override
	public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, ItemStack weapon) {
		ArrowEntityImpl arrow = new ArrowEntityImpl(Obsidian.PROJECTILE_ARROW, shooter, level,
				ammo.copyWithCount(1), weapon);

		if (projectile.damage > 0.0F) arrow.setBaseDamage(projectile.damage);
		if (!projectile.recoverable) arrow.pickup = AbstractArrow.Pickup.DISALLOWED;

		return arrow;
	}
}

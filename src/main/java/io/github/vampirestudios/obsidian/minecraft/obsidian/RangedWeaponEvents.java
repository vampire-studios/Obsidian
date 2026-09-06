package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.api.obsidian.item.ObsidianProjectileSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import org.jspecify.annotations.Nullable;

/**
 * The one thing every Obsidian ranged weapon does when it fires, wherever it fires from: tag the
 * projectile with the item that launched it, then run that item's {@code on_shoot}.
 *
 * <p>Tagging is unconditional, but {@code on_shoot} only runs for a player — a skeleton firing an Obsidian
 * bow still produces a projectile whose landing fires hit events, since those do not need one.
 */
public final class RangedWeaponEvents {

	private RangedWeaponEvents() {
	}

	public static void shot(@Nullable LivingEntity shooter, Projectile projectile, @Nullable Item item) {
		if (item == null) return;

		ObsidianProjectileSource.tag(projectile, item);

		if (projectile.level().isClientSide()) return;
		if (shooter instanceof Player player) EventActionHandler.handleOnShoot(player, item);
	}
}

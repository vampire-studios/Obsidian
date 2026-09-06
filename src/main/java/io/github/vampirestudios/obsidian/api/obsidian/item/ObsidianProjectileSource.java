package io.github.vampirestudios.obsidian.api.obsidian.item;

import net.minecraft.world.entity.projectile.Projectile;
import org.jspecify.annotations.Nullable;

/**
 * A projectile's memory of the Obsidian item that launched it, so its hit events can be run against that
 * item's definition. Implemented on every {@link Projectile} by {@code ProjectileEventsMixin}.
 *
 * <p>Obsidian shoots from two places — the vanilla-derived bow and crossbow implementations, and the
 * {@code SHOOTER} component's own release handling — so the tag is set explicitly at both rather than read
 * back off the projectile, whose weapon-item bookkeeping only the vanilla path fills in.
 *
 * <p>The tag is not saved with the world. A projectile still in flight when the level unloads comes back
 * untagged and fires no hit events, which is a shot lost rather than anything broken.
 */
public interface ObsidianProjectileSource {

	void obsidian$setSourceItem(@Nullable Item item);

	@Nullable Item obsidian$sourceItem();

	/** Tags a projectile with the definition of the item that launched it. */
	static void tag(Projectile projectile, @Nullable Item weapon) {
		if (projectile instanceof ObsidianProjectileSource source) source.obsidian$setSourceItem(weapon);
	}

	/** The definition behind whatever launched this projectile, or null when nothing tagged it. */
	static @Nullable Item of(Projectile projectile) {
		return projectile instanceof ObsidianProjectileSource source ? source.obsidian$sourceItem() : null;
	}
}

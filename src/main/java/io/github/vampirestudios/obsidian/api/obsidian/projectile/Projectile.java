package io.github.vampirestudios.obsidian.api.obsidian.projectile;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import net.minecraft.resources.Identifier;

/**
 * A throwable item: what it does in flight, and what it does when it lands.
 *
 * <p>A projectile is an {@linkplain Item item} first, so its name, settings, model, tooltip and creative
 * tab all work the way they do anywhere else. What it adds is a flight path and three events — the same
 * {@code projectile_hit_entity} and {@code projectile_hit_block} a ranged weapon already fires, plus
 * {@code projectile_expired} for one that ran out of time.
 */
public class Projectile extends Item {

	/**
	 * Which kind of projectile to build. A {@code thrown} one is used from the hand like a snowball; an
	 * {@code arrow} is ammunition, fired only by a ranged weapon that names it, and sticks where it lands.
	 */
	public Shape shape = Shape.THROWN;

	public enum Shape {
		@SerializedName("thrown") THROWN,
		@SerializedName(value = "arrow", alternate = {"bolt"}) ARROW
	}

	public boolean isArrow() {
		return shape == Shape.ARROW;
	}

	public Flight flight = new Flight();
	public Trail trail;

	/** Damage dealt to the entity struck. Zero throws a harmless projectile, which is a normal thing to want. */
	public float damage = 0.0F;

	/**
	 * How many entities the projectile passes through before stopping. {@code 0} stops at the first, which
	 * is what a thrown item normally does.
	 */
	public int pierce = 0;

	/** Whether the projectile survives hitting a block, bouncing off it instead of stopping. */
	public boolean bounce = false;

	/** How much speed is kept through a bounce. Ignored unless {@link #bounce} is set. */
	@SerializedName("bounce_damping")
	public float bounceDamping = 0.5F;

	/** Whether the item can be picked back up where the projectile came to rest. */
	public boolean recoverable = false;

	/** How many the throw takes from the stack. {@code 0} throws without consuming, for a reusable wand. */
	@SerializedName("consume_count")
	public int consumeCount = 1;

	/** Ticks before the thrower can throw again. */
	@SerializedName("cooldown")
	public int cooldown = 0;

	public static class Flight {
		/** Launch speed in blocks per tick. Vanilla's snowball is {@code 1.5}. */
		public float speed = 1.5F;

		/** Blocks per tick lost to gravity each tick. A snowball uses {@code 0.03}; {@code 0} flies flat. */
		public float gravity = 0.03F;

		/** Fraction of speed kept each tick. {@code 1.0} never slows down. */
		public float drag = 0.99F;

		/** How far the throw scatters from where the player aimed. {@code 0} is perfectly accurate. */
		public float inaccuracy = 1.0F;

		/** Ticks before an unspent projectile gives up and fires {@code projectile_expired}. */
		public int lifetime = 200;

		/** Whether the projectile is stopped by water rather than passing through it. */
		@SerializedName("stops_in_water")
		public boolean stopsInWater = false;
	}

	public static class Trail {
		/** The particle left behind in flight. */
		public Identifier particle;

		/** How many particles per emission. */
		public int count = 1;

		/** Ticks between emissions. {@code 1} emits every tick. */
		public int interval = 2;

		/** Random spread applied to each particle, in blocks. */
		public float spread = 0.0F;
	}

}

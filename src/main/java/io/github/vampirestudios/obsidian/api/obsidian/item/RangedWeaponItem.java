package io.github.vampirestudios.obsidian.api.obsidian.item;

public class RangedWeaponItem extends Item {

	/** Vanilla's full bow draw, in ticks. */
	public static final int DEFAULT_DRAW_TIME = 20;

	/** A shortbow trades power for speed, so it draws in half the time by default. */
	public static final int DEFAULT_SHORTBOW_DRAW_TIME = 10;

	/** Vanilla's crossbow charge, in ticks — 1.25 seconds. */
	public static final int DEFAULT_CHARGE_TIME = 25;

	/** A heavy crossbow is slower to wind and hits harder for it. */
	public static final int DEFAULT_HEAVY_CHARGE_TIME = 40;

	/** How much harder: the default projectile speed multiplier for a heavy crossbow. */
	public static final float DEFAULT_HEAVY_VELOCITY = 1.25F;

	public enum Type {
		@com.google.gson.annotations.SerializedName("bow") BOW,
		@com.google.gson.annotations.SerializedName("shortbow") SHORTBOW,
		@com.google.gson.annotations.SerializedName("crossbow") CROSSBOW,
		@com.google.gson.annotations.SerializedName("heavy_crossbow") HEAVY_CROSSBOW,
		@com.google.gson.annotations.SerializedName("trident") TRIDENT
	}

	public Type weapon_type;

	/**
	 * Bows and shortbows: ticks to reach a full-power shot. Lower draws faster; the shot's power
	 * still scales with how long it was actually held, so a half-drawn shortbow is as weak as a
	 * half-drawn bow.
	 *
	 * <p>Unset means {@value #DEFAULT_DRAW_TIME} for a bow and {@value #DEFAULT_SHORTBOW_DRAW_TIME}
	 * for a shortbow.</p>
	 */
	@com.google.gson.annotations.SerializedName("draw_time")
	public Integer drawTime;

	/**
	 * Crossbows: ticks to wind a bolt. Unset means {@value #DEFAULT_CHARGE_TIME} for a crossbow and
	 * {@value #DEFAULT_HEAVY_CHARGE_TIME} for a heavy crossbow. Quick Charge still applies on top.
	 */
	@com.google.gson.annotations.SerializedName("charge_time")
	public Integer chargeTime;

	/**
	 * Multiplies the speed the projectile leaves at, and with it the damage on impact. Unset means
	 * {@value #DEFAULT_HEAVY_VELOCITY} for a heavy crossbow and {@code 1.0} for everything else.
	 */
	@com.google.gson.annotations.SerializedName("projectile_velocity")
	public Float projectileVelocity;

	/** How far the weapon can be aimed at a target, in blocks. Unset uses vanilla's range. */
	@com.google.gson.annotations.SerializedName("projectile_range")
	public Integer projectileRange;

	/**
	 * Ammunition this weapon fires, as ids of arrow-shaped {@code item/projectile} entries.
	 *
	 * <p>Naming any is exclusive: the weapon then fires **only** what it names, and vanilla arrows no
	 * longer load into it. A weapon that names none behaves like vanilla and takes ordinary arrows.
	 *
	 * <p>The gate is one-way. A custom arrow is not accepted by vanilla bows either, so ammunition and
	 * weapon have to be designed as a pair — which is what makes bolts distinct from arrows.
	 */
	@com.google.gson.annotations.SerializedName("ammo")
	public java.util.List<net.minecraft.resources.Identifier> ammo;

	/** Whether this weapon restricts what it fires. */
	public boolean hasCustomAmmo() {
		return ammo != null && !ammo.isEmpty();
	}

	/** The resolved draw time in ticks, never below 1. */
	public int drawTicks() {
		if (this.drawTime != null) return Math.max(1, this.drawTime);
		return this.weapon_type == Type.SHORTBOW ? DEFAULT_SHORTBOW_DRAW_TIME : DEFAULT_DRAW_TIME;
	}

	/** The resolved charge time in ticks, never below 1. */
	public int chargeTicks() {
		if (this.chargeTime != null) return Math.max(1, this.chargeTime);
		return this.weapon_type == Type.HEAVY_CROSSBOW ? DEFAULT_HEAVY_CHARGE_TIME : DEFAULT_CHARGE_TIME;
	}

	/** The resolved projectile speed multiplier. */
	public float velocityMultiplier() {
		if (this.projectileVelocity != null) return Math.max(0.0F, this.projectileVelocity);
		return this.weapon_type == Type.HEAVY_CROSSBOW ? DEFAULT_HEAVY_VELOCITY : 1.0F;
	}

	public boolean isBowLike() {
		return this.weapon_type == Type.BOW || this.weapon_type == Type.SHORTBOW;
	}

	public boolean isCrossbowLike() {
		return this.weapon_type == Type.CROSSBOW || this.weapon_type == Type.HEAVY_CROSSBOW;
	}

}

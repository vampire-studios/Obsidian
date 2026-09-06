package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.InteractionContext;
import io.github.vampirestudios.obsidian.api.obsidian.projectile.Projectile;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * The entity behind an {@code item/projectile}.
 *
 * <p>One entity type serves every projectile a pack declares. The definition is not stored on the entity
 * — it is looked up from the item the projectile carries, which is already synced and saved for the
 * renderer's sake. That keeps packs from needing an entity type each, and means a projectile in flight
 * picks up a reloaded definition rather than a stale copy.
 */
public class ProjectileEntityImpl extends ThrowableItemProjectile {

	/** Entities already struck, so a piercing projectile does not hit the same one every tick. */
	private int piercedSoFar = 0;

	public ProjectileEntityImpl(EntityType<? extends ProjectileEntityImpl> type, Level level) {
		super(type, level);
	}

	public ProjectileEntityImpl(EntityType<? extends ProjectileEntityImpl> type, LivingEntity thrower,
	                            Level level, ItemStack stack) {
		super(type, thrower, level, stack);
	}

	/** The definition this projectile was thrown from, or null when its item is no longer registered. */
	public @Nullable Projectile definition() {
		Identifier id = BuiltInRegistries.ITEM.getKey(getItem().getItem());
		return id == null ? null : ContentRegistries.PROJECTILES.getValue(id);
	}

	@Override
	protected Item getDefaultItem() {
		return Items.SNOWBALL;
	}

	@Override
	protected double getDefaultGravity() {
		Projectile definition = definition();
		return definition == null ? 0.03D : definition.flight.gravity;
	}

	@Override
	public void tick() {
		super.tick();

		Projectile definition = definition();
		if (definition == null) return;

		applyDrag(definition);
		emitTrail(definition);

		if (definition.flight.stopsInWater && this.isInWater()) {
			expire(definition);
			return;
		}

		if (this.tickCount >= Math.max(1, definition.flight.lifetime)) {
			expire(definition);
		}
	}

	private void applyDrag(Projectile definition) {
		float drag = definition.flight.drag;
		if (drag == 1.0F || drag <= 0.0F) return;
		this.setDeltaMovement(this.getDeltaMovement().scale(drag));
	}

	private void emitTrail(Projectile definition) {
		Projectile.Trail trail = definition.trail;
		if (trail == null || trail.particle == null) return;
		if (!(this.level() instanceof ServerLevel serverLevel)) return;

		int interval = Math.max(1, trail.interval);
		if (this.tickCount % interval != 0) return;

		ParticleOptions particle = BuiltInRegistries.PARTICLE_TYPE.getValue(trail.particle)
				instanceof SimpleParticleType simple ? simple : null;
		if (particle == null) return;

		serverLevel.sendParticles(particle, this.getX(), this.getY(), this.getZ(),
				Math.max(1, trail.count), trail.spread, trail.spread, trail.spread, 0.0D);
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		Projectile definition = definition();
		if (definition == null || !(this.level() instanceof ServerLevel serverLevel)) return;

		Entity struck = result.getEntity();
		if (definition.damage > 0.0F) {
			struck.hurtServer(serverLevel, this.damageSources().thrown(this, this.getOwner()), definition.damage);
		}

		runEvents(definition, "projectile_hit_entity",
				InteractionContext.ofProjectile(this.level(), shooter(),
						struck instanceof LivingEntity living ? living : null, null));

		// A piercing projectile keeps going; anything else has done its job.
		if (this.piercedSoFar++ >= definition.pierce) {
			finish(definition, null);
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		Projectile definition = definition();
		if (definition == null) return;

		runEvents(definition, "projectile_hit_block",
				InteractionContext.ofProjectile(this.level(), shooter(), null, result));

		if (definition.bounce) {
			bounceOff(result, definition);
			return;
		}
		finish(definition, result);
	}

	/** Reflects the projectile off the face it struck, keeping {@code bounce_damping} of its speed. */
	private void bounceOff(BlockHitResult result, Projectile definition) {
		Vec3 movement = this.getDeltaMovement();
		Vec3 reflected = switch (result.getDirection().getAxis()) {
			case X -> new Vec3(-movement.x, movement.y, movement.z);
			case Y -> new Vec3(movement.x, -movement.y, movement.z);
			case Z -> new Vec3(movement.x, movement.y, -movement.z);
		};
		this.setDeltaMovement(reflected.scale(Math.clamp(definition.bounceDamping, 0.0F, 1.0F)));
	}

	private void expire(Projectile definition) {
		runEvents(definition, "projectile_expired",
				InteractionContext.ofProjectile(this.level(), shooter(), null, null));
		finish(definition, null);
	}

	/** Drops the item if the projectile is recoverable, then removes it. */
	private void finish(Projectile definition, @Nullable BlockHitResult at) {
		if (this.level().isClientSide()) return;

		if (definition.recoverable && !this.getItem().isEmpty()) {
			Vec3 where = at != null ? at.getLocation() : this.position();
			this.level().addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(
					this.level(), where.x, where.y, where.z, this.getItem().copyWithCount(1)));
		}
		this.discard();
	}

	/**
	 * Dispatches directly rather than through {@code ProjectileEventsMixin}, which fires the same two
	 * events for a ranged weapon's arrows. That mixin carries the launching item in a field, which is
	 * lost when the world reloads; a thrown projectile resolves its definition from the item stack it
	 * carries, which is saved. Since this entity never sets the mixin's source item, the two do not
	 * both fire — do not "unify" them by setting it.
	 */
	private void runEvents(Projectile definition, String event, InteractionContext ctx) {
		if (this.level().isClientSide()) return;
		for (Map<String, Object> actionConfig : definition.getEventActions(event)) {
			EventActionHandler.dispatch(ctx, (String) actionConfig.get("action"), actionConfig, event);
		}
	}

	/** The thrower, when it was a player — a dispenser or a mob leaves the events without one. */
	private @Nullable Player shooter() {
		return this.getOwner() instanceof Player player ? player : null;
	}
}

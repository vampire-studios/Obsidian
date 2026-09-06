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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * The arrow-shaped half of {@code item/projectile}: ammunition fired from a bow or crossbow.
 *
 * <p>Everything an arrow is expected to do — sticking into blocks, being picked back up, crit damage on
 * a full draw, Power and Punch — is {@link AbstractArrow}'s. What this adds is the definition's flight
 * tuning, its trail, and the same impact events the thrown shape fires.
 *
 * <p>Like {@link ProjectileEntityImpl}, one entity type serves every arrow a pack declares; the
 * definition is resolved from the pickup stack, which is saved and synced already.
 */
public class ArrowEntityImpl extends AbstractArrow {

	public ArrowEntityImpl(EntityType<? extends ArrowEntityImpl> type, Level level) {
		super(type, level);
	}

	public ArrowEntityImpl(EntityType<? extends ArrowEntityImpl> type, LivingEntity shooter, Level level,
	                       ItemStack ammo, @Nullable ItemStack weapon) {
		super(type, shooter, level, ammo, weapon);
	}

	/** The definition this arrow was fired from, or null when its item is no longer registered. */
	public @Nullable Projectile definition() {
		Identifier id = BuiltInRegistries.ITEM.getKey(getPickupItemStackOrigin().getItem());
		return id == null ? null : ContentRegistries.PROJECTILES.getValue(id);
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return new ItemStack(Items.ARROW);
	}

	@Override
	protected double getDefaultGravity() {
		Projectile definition = definition();
		return definition == null ? 0.05D : definition.flight.gravity;
	}

	@Override
	public void tick() {
		super.tick();

		Projectile definition = definition();
		if (definition == null) return;

		emitTrail(definition);

		if (definition.flight.lifetime > 0 && this.tickCount >= definition.flight.lifetime) {
			runEvents(definition, "projectile_expired",
					InteractionContext.ofProjectile(this.level(), shooter(), null, null));
			this.discard();
		}
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
		if (definition == null) return;

		runEvents(definition, "projectile_hit_entity",
				InteractionContext.ofProjectile(this.level(), shooter(),
						result.getEntity() instanceof LivingEntity living ? living : null, null));
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);

		Projectile definition = definition();
		if (definition == null) return;

		runEvents(definition, "projectile_hit_block",
				InteractionContext.ofProjectile(this.level(), shooter(), null, result));
	}

	/** See the note on {@link ProjectileEntityImpl#runEvents} — this deliberately does not use the mixin. */
	private void runEvents(Projectile definition, String event, InteractionContext ctx) {
		if (this.level().isClientSide()) return;
		for (Map<String, Object> actionConfig : definition.getEventActions(event)) {
			EventActionHandler.dispatch(ctx, (String) actionConfig.get("action"), actionConfig, event);
		}
	}

	private @Nullable Player shooter() {
		return this.getOwner() instanceof Player player ? player : null;
	}
}

package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.api.obsidian.item.ObsidianProjectileSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Carries the launching item on the projectile and turns its landing into {@code projectile_hit_entity}
 * and {@code projectile_hit_block} events.
 *
 * <p>{@code onHit} is the one place both kinds of landing pass through, so a single injection covers
 * arrows, bolts, tridents and anything the {@code SHOOTER} component spawns.
 */
@Mixin(Projectile.class)
public abstract class ProjectileEventsMixin implements ObsidianProjectileSource {

	@Unique
	private @Nullable Item obsidian$sourceItem;

	@Override
	public void obsidian$setSourceItem(@Nullable Item item) {
		this.obsidian$sourceItem = item;
	}

	@Override
	public @Nullable Item obsidian$sourceItem() {
		return this.obsidian$sourceItem;
	}

	@Inject(method = "onHit", at = @At("HEAD"))
	private void obsidian$onHit(HitResult hit, CallbackInfo ci) {
		Item item = this.obsidian$sourceItem;
		if (item == null) return;

		Projectile projectile = (Projectile) (Object) this;
		if (projectile.level().isClientSide()) return;

		Player shooter = projectile.getOwner() instanceof Player player ? player : null;

		if (hit instanceof EntityHitResult entityHit) {
			Entity struck = entityHit.getEntity();
			EventActionHandler.handleProjectileHitEntity(projectile, shooter,
					struck instanceof LivingEntity living ? living : null, item);
		} else if (hit instanceof BlockHitResult blockHit) {
			EventActionHandler.handleProjectileHitBlock(projectile, shooter, blockHit, item);
		}
	}
}

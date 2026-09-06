package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class ProjectileHitMixin {
	@Inject(method = "onHit", at = @At("HEAD"))
	private void crucible$onHit(HitResult hit, CallbackInfo ci) {
		Projectile proj = (Projectile) (Object) this;
		if (!(proj.level() instanceof ServerLevel level)) return;

		Entity owner = proj.getOwner();
		if (!(owner instanceof ServerPlayer sp)) return;

		if (hit instanceof EntityHitResult ehr) {
			Entity e = ehr.getEntity();
			LivingEntity target = (e instanceof LivingEntity le) ? le : null;

			CrucibleEvents.fire(SkillTrigger.PROJECTILE_HIT,
					SkillContext.builder(sp)
							.level(level)
							.projectile(proj)
							.target(target)
							.build()
			);
		} else if (hit instanceof BlockHitResult bhr) {
			// "land" = hit a block / ground
			CrucibleEvents.fire(SkillTrigger.PROJECTILE_LAND,
					SkillContext.builder(sp)
							.level(level)
							.projectile(proj)
							.position(bhr.getBlockPos())
							.build()
			);
		}
	}
}

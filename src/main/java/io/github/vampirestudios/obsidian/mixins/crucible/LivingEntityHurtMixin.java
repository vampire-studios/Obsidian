package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityHurtMixin {
	@Inject(method = "actuallyHurt", at = @At("HEAD"))
	private void crucible$onHurt(ServerLevel serverLevel, DamageSource source, float f, CallbackInfo ci) {
		LivingEntity victim = (LivingEntity) (Object) this;
		if (!(victim.level() instanceof ServerLevel level)) return;

		var attacker = source.getEntity();
		if (attacker instanceof ServerPlayer sp) {
			// attacker got DAMAGED? No. Victim got damaged. Use ctx.caster = victim (for victim skills)
			CrucibleEvents.fire(SkillTrigger.DAMAGED,
					SkillContext.builder(victim).level(level).target(sp).build()
			);

			CrucibleEvents.fire(SkillTrigger.DAMAGE_DEALT, SkillContext.builder(sp).level(level).target(victim).build());
		}
	}
}

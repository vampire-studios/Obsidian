package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public class CreeperTriggersMixin {

	// PRIME: creeper starts igniting/swelling
	@Inject(method = "ignite()V", at = @At("TAIL"))
	private void crucible$prime(CallbackInfo ci) {
		Creeper creeper = (Creeper) (Object) this;
		if (!(creeper.level() instanceof ServerLevel sl)) return;

		CrucibleEvents.fire(
				SkillTrigger.CREEPER_PRIME,
				SkillContext.builder(creeper).level(sl).build()
		);
	}

	// EXPLODE: creeper actually explodes
	@Inject(method = "explodeCreeper()V", at = @At("HEAD"))
	private void crucible$explode(CallbackInfo ci) {
		Creeper creeper = (Creeper) (Object) this;
		if (!(creeper.level() instanceof ServerLevel sl)) return;

		CrucibleEvents.fire(
				SkillTrigger.CREEPER_EXPLODE,
				SkillContext.builder(creeper).level(sl).build()
		);
	}

	// CREEPER_CHARGE: lightning charges creeper
	@Inject(method = "thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V", at = @At("TAIL"))
	private void crucible$creeperCharge(ServerLevel level, LightningBolt bolt, CallbackInfo ci) {
		Creeper creeper = (Creeper) (Object) this;

		CrucibleEvents.fire(
				SkillTrigger.CREEPER_CHARGE,
				SkillContext.builder(creeper).level(level).build()
		);
	}
}

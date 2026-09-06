package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityJumpMixin {
	@Inject(method = "jumpFromGround", at = @At("TAIL"))
	private void crucible$afterJump(CallbackInfo ci) {
		LivingEntity le = (LivingEntity) (Object) this;
		if (!(le instanceof ServerPlayer sp)) return;
		if (!(sp.level() instanceof ServerLevel level)) return;

		CrucibleEvents.fire(SkillTrigger.JUMP,
				SkillContext.builder(sp).level(level).build()
		);
	}
}

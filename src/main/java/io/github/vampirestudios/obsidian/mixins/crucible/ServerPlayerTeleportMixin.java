package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerTeleportMixin {
	@Inject(method = "teleportTo(DDD)V", at = @At("TAIL"))
	private void crucible$afterTeleport(double x, double y, double z, CallbackInfo ci) {
		ServerPlayer sp = (ServerPlayer) (Object) this;
		if (!(sp.level() instanceof ServerLevel level)) return;

		CrucibleEvents.fire(SkillTrigger.TELEPORT,
				SkillContext.builder(sp).level(level).build()
		);
	}
}

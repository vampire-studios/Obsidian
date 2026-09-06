package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntitySneakMixin {
	@Inject(method = "setShiftKeyDown", at = @At("TAIL"))
	private void crucible$onShiftChanged(boolean sneaking, CallbackInfo ci) {
		Entity e = (Entity) (Object) this;
		if (!(e instanceof ServerPlayer sp)) return;
		if (!(sp.level() instanceof ServerLevel level)) return;

		CrucibleEvents.fire(sneaking ? SkillTrigger.CROUCH : SkillTrigger.UNCROUCH,
				SkillContext.builder(sp).level(level).build()
		);
	}
}

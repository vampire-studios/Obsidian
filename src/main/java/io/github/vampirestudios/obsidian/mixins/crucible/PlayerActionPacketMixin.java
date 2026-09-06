package io.github.vampirestudios.obsidian.mixins.crucible;

import io.github.vampirestudios.obsidian.api.crucible.CrucibleEvents;
import io.github.vampirestudios.obsidian.api.crucible.SkillContext;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class PlayerActionPacketMixin {
	@Shadow
	public ServerPlayer player;

	@Inject(method = "handlePlayerAction", at = @At("HEAD"))
	private void crucible$handlePlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
		if (player == null) return;
		if (!(player.level() instanceof ServerLevel level)) return;

		var action = packet.getAction();

		switch (action) {
			case DROP_ITEM -> CrucibleEvents.fire(SkillTrigger.PRESS_Q,
					SkillContext.builder(player).level(level).build()
			);
			case DROP_ALL_ITEMS -> CrucibleEvents.fire(SkillTrigger.PRESS_CTRLQ,
					SkillContext.builder(player).level(level).build()
			);
			case SWAP_ITEM_WITH_OFFHAND -> CrucibleEvents.fire(SkillTrigger.PRESS_F,
					SkillContext.builder(player).level(level).build()
			);
			default -> {
			}
		}
	}
}

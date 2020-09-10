package io.github.vampirestudios.obsidian;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * This event is called when a player joins the server.
 *
 * <p>It hooks in at the end of {@link net.minecraft.server.PlayerManager#onPlayerConnect(ClientConnection, ServerPlayerEntity)} through {@link io.github.vampirestudios.obsidian.mixins.MixinPlayerManager}.
 */
public interface PlayerJoinCallback {
	Event<PlayerJoinCallback> EVENT = EventFactory.createArrayBacked(PlayerJoinCallback.class,
			(listeners) -> (playerEntity) -> {
				for (PlayerJoinCallback event : listeners) {
					event.onPlayerJoin(playerEntity);
				}
			}
	);

	void onPlayerJoin(ServerPlayerEntity player);
}
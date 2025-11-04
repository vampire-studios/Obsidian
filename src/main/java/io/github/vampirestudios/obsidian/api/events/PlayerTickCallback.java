package io.github.vampirestudios.obsidian.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

/**
 * A feral callback that fires every damn tick for every player.
 */
public interface PlayerTickCallback {
    Event<PlayerTickCallback> EVENT = EventFactory.createArrayBacked(
        PlayerTickCallback.class,
        listeners -> player -> {
            // Loop through all listeners; no bail-out—everyone gets a taste.
            for (PlayerTickCallback listener : listeners) {
                listener.onPlayerTick(player);
            }
        }
    );

    /**
     * Called at the VERY START of PlayerEntity.tick().
     * @param player the primal player entity about to get ticked
     */
    void onPlayerTick(Player player);
}

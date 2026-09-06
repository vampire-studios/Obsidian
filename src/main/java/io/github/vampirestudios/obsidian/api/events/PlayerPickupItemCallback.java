package io.github.vampirestudios.obsidian.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Callback for when a player tries to pick up an item.
 * Called before the item is removed from the world.
 *
 * Upon return:
 *  - SUCCESS: cancels further processing (you can still manually insert stack if you want)
 *  - PASS:    lets the next listener run, defaults to SUCCESS if nobody cares
 *  - FAIL:    cancels pickup entirely
 */
public interface PlayerPickupItemCallback {
	Event<PlayerPickupItemCallback> EVENT = EventFactory.createArrayBacked(
			PlayerPickupItemCallback.class,
			listeners -> (player, item) -> {
				for (PlayerPickupItemCallback listener : listeners) {
					InteractionResult res = listener.interact(player, item);
					if (res != InteractionResult.PASS) {
						return res;
					}
				}
				return InteractionResult.PASS;
			}
	);

	InteractionResult interact(Player player, ItemEntity itemEntity);
}
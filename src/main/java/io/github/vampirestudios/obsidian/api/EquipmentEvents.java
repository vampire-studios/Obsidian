package io.github.vampirestudios.obsidian.api;

import io.github.vampirestudios.obsidian.api.events.PlayerTickCallback;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.api.obsidian.item.ObsidianItemHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Turns what a player is wearing into {@code on_equip}, {@code on_unequip} and {@code equipment_tick}
 * events.
 *
 * <p>There is no single vanilla hook for "this item was equipped" — armor arrives through the inventory
 * screen, a right-click, a dispenser, {@code /item} or a death drop being picked back up. So rather than
 * chase every route, this watches the slots themselves: once a tick it compares what is worn against what
 * was worn last tick and fires on the difference. Gear a player logs in already wearing counts as a change
 * from nothing, so {@code on_equip} is a reliable place to set up state that {@code on_unequip} tears down.
 *
 * <p>Held items are deliberately not watched. Scrolling the hotbar is not equipping, and items in hand
 * already have {@code inventory_tick} and the use events.
 */
public final class EquipmentEvents {

	private static final EquipmentSlot[] WATCHED = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND
	};

	/**
	 * What each player wore last tick, indexed to match {@link #WATCHED}. Weakly keyed so a player who logs
	 * out — or dies, since a respawn is a new entity — drops out on its own; nothing here needs to survive
	 * that, because the next first tick treats everything worn as freshly equipped.
	 *
	 * <p>Only ever touched from the server thread, which {@link #tick} guarantees by ignoring client ticks.
	 */
	private static final Map<Player, Item[]> WORN = new WeakHashMap<>();

	private EquipmentEvents() {
	}

	public static void register() {
		PlayerTickCallback.EVENT.register(EquipmentEvents::tick);
	}

	private static void tick(Player player) {
		if (player.level().isClientSide()) return;

		Item[] previous = WORN.computeIfAbsent(player, p -> new Item[WATCHED.length]);

		for (int i = 0; i < WATCHED.length; i++) {
			EquipmentSlot slot = WATCHED[i];
			Item current = ObsidianItemHolder.of(player.getItemBySlot(slot).getItem());
			Item before = previous[i];

			if (current != before) {
				// Unequip first: an item that swaps state on should get to swap it back off before whatever
				// replaced it in the same slot sets its own.
				if (before != null) EventActionHandler.handleOnUnequip(player, slot, before);
				if (current != null) EventActionHandler.handleOnEquip(player, slot, current);
				previous[i] = current;
			}

			if (current != null) EventActionHandler.handleEquipmentTick(player, slot, current);
		}
	}
}

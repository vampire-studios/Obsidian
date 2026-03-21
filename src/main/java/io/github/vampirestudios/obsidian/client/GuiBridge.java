package io.github.vampirestudios.obsidian.client;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.vampirestudios.obsidian.scripting.std.ObsPackRuntime;
import io.github.vampirestudios.obsidian.scripting.std.PlayerCommandHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GuiBridge {
	private static final Map<String, SimpleGui> OPEN = new HashMap<>();

	// open
	public static String create(ServerPlayer p, int rows, Component title) {
		if (rows < 1 || rows > 6) throw new IllegalArgumentException("rows must be 1..6");
		MenuType<ChestMenu> menu = switch (rows) {
			case 1 -> MenuType.GENERIC_9x1;
			case 2 -> MenuType.GENERIC_9x2;
			case 3 -> MenuType.GENERIC_9x3;
			case 4 -> MenuType.GENERIC_9x4;
			case 5 -> MenuType.GENERIC_9x5;
			case 6 -> MenuType.GENERIC_9x6;
			default -> throw new IllegalStateException("Unexpected value: " + rows);
		};
		String id = UUID.randomUUID().toString();

		SimpleGui gui = new SimpleGui(menu, p, false) {
			@Override
			public void onManualClose() {
				ObsPackRuntime.fireGuiClose(getPlayer(), id);
				super.onManualClose();
			}

			@Override
			public void onOpen() {
				ObsPackRuntime.fireGuiOpen(p, id, rows);
				super.onOpen();
			}
		};
		gui.setTitle(title);
		OPEN.put(id, gui);
		return id;
	}

	/** Set a slot with a basic item. */
	public static void set(String id, int slot, String itemId, int count, Map<String, Object> comps, ServerPlayer player) {
		SimpleGui gui = OPEN.get(id);
		if (gui == null || gui.getPlayer() != player) return;
		if (slot < 0 || slot >= gui.getSize()) throw new IllegalArgumentException("slot OOB: " + slot);
		if (count < 1 || count > 64) count = Math.max(1, Math.min(64, count));

		var key = Identifier.parse(itemId);
		if (!BuiltInRegistries.ITEM.containsKey(key))
			throw new IllegalArgumentException("Unknown item: " + itemId);
		Item item = BuiltInRegistries.ITEM.getValue(key);

		ItemStack stack = new ItemStack(item, count);
		if (comps != null) {
			try { PlayerCommandHandler.applyComponents(stack, comps); }
			catch (Exception e) { throw new IllegalArgumentException("components: " + e.getMessage(), e); }
		}

		GuiElementBuilder el = new GuiElementBuilder(stack)
				.setCallback((i, clickType, containerInput, slotBasedGui) -> ObsPackRuntime.fireGuiClick(player, id, i, clickType.name().toLowerCase()));

		gui.setSlot(slot, el.build());
	}

	public static void open(String id, ServerPlayer player) {
		SimpleGui gui = OPEN.get(id);
		if (gui != null && gui.getPlayer() == player) gui.open();
	}

	/** Close a GUI for a player. */
	public static void close(String id, ServerPlayer player) {
		SimpleGui gui = OPEN.get(id);
		if (gui != null && gui.getPlayer() == player) {
			OPEN.remove(id);
			gui.close();
		}
	}

	/** Cleanup all GUIs for a player (call on disconnect). */
	public static void closeAllFor(ServerPlayer player) {
		var ids = new java.util.ArrayList<String>();
		OPEN.forEach((k, g) -> {
			if (g.getPlayer() == player) ids.add(k);
		});
		for (var k : ids) close(k, player);
	}

	/** Utility: get GUI by id. */
	public static SimpleGui get(String id) {
		return OPEN.get(id);
	}
}

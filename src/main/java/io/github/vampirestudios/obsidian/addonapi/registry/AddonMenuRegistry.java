package io.github.vampirestudios.obsidian.addonapi.registry;

import io.github.vampirestudios.obsidian.addonapi.menu.MenuDefinition;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AddonMenuRegistry {

	private static final Map<Identifier, MenuDefinition> MENUS = new HashMap<>();

	public static void put(Identifier id, MenuDefinition def) {
		MENUS.put(id, def);
	}

	public static MenuDefinition get(Identifier id) {
		return MENUS.get(id);
	}

	public static Collection<MenuDefinition> all() {
		return MENUS.values();
	}

	public static void clear() {
		MENUS.clear();
	}
}

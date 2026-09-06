package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem;

/**
 * Armor is an ordinary Obsidian item whose properties happen to carry an armor material — the slot,
 * defense and equip sound all live in the settings {@link io.github.vampirestudios.obsidian.addon_modules.Armor}
 * builds, so nothing here needs overriding. Extending {@link ItemImpl} is what gives armor the item events.
 */
public class ArmorItemImpl extends ItemImpl {

	public ArmorItemImpl(ArmorItem item, Properties settings) {
		super(item, settings);
	}

}

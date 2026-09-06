package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;

/** Armor that starts out dyed the colour the pack asked for. Otherwise an ordinary {@link ItemImpl}. */
public class DyeableArmorItemImpl extends ItemImpl {

	public DyeableArmorItemImpl(ArmorItem item, Properties settings) {
		super(item, settings
				.component(DataComponents.DYED_COLOR, new DyedItemColor(item.information.getItemSettings().defaultColor)));
	}

}

package io.github.vampirestudios.obsidian.minecraft.oraxen;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;

public class DyeableArmorItemImpl extends CustomArmorItem {

	public DyeableArmorItemImpl(NexoItem nexoItem, Properties settings) {
		super(nexoItem, settings.component(DataComponents.DYED_COLOR, new DyedItemColor(nexoItem.mechanics.dyeable.default_color)));
	}

}

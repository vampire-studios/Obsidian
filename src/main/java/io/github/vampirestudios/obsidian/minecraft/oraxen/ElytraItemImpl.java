package io.github.vampirestudios.obsidian.minecraft.oraxen;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;

public class ElytraItemImpl extends ItemImpl {

	public ElytraItemImpl(NexoItem item, Properties settings) {
		super(item, settings.component(DataComponents.GLIDER, Unit.INSTANCE));
	}
}

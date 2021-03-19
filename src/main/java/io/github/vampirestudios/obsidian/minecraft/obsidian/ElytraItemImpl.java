package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.fabricmc.fabric.api.item.v1.elytra.FabricElytraExtensions;
import net.minecraft.item.ElytraItem;

public class ElytraItemImpl extends ElytraItem implements FabricElytraExtensions {

	public ElytraItemImpl(Settings settings) {
		super(settings);
	}

}

package io.github.vampirestudios.obsidian.api.nexo;

import net.minecraft.world.item.Item;

public interface ItemMechanic {
	boolean applies(NexoItem item);

	Item wrap(Item base, NexoItem item, Item.Properties props);
}
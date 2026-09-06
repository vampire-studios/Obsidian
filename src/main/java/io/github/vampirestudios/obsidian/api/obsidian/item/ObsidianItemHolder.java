package io.github.vampirestudios.obsidian.api.obsidian.item;

import org.jspecify.annotations.Nullable;

/**
 * Implemented by the vanilla {@link net.minecraft.world.item.Item} subclasses Obsidian registers, so code
 * holding nothing but a stack can get back to the pack definition behind it.
 *
 * <p>Each implementation keeps its own field of its own type — armor items hold an {@link ArmorItem},
 * shields a {@link ShieldItem} — and they all widen to {@link Item}, which is what carries the event map.
 */
public interface ObsidianItemHolder {

	/** The pack definition this item was built from. */
	Item obsidianItem();

	/** The definition behind a vanilla item, or null when it is not one of ours. */
	static @Nullable Item of(net.minecraft.world.item.Item item) {
		return item instanceof ObsidianItemHolder holder ? holder.obsidianItem() : null;
	}
}

package io.github.vampirestudios.obsidian.registry;

import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class OBlockTags {
	public static final TagKey<Block> ABOVE_BYPASSES_SEAT_CHECK = blockTag("above_bypasses_seat_check");

	private static TagKey<Block> blockTag(String name) {
		return TagKey.create(Registries.BLOCK, Obsidian.id(name));
	}

	public static void init() {
	}
}

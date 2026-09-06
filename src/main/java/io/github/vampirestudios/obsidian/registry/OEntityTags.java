package io.github.vampirestudios.obsidian.registry;

import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class OEntityTags {

	public static final TagKey<EntityType<?>> CAN_SIT_IN_SEATS = entityTypeTag("can_sit_in_seats");

	private static TagKey<EntityType<?>> entityTypeTag(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, Obsidian.id(name));
	}

	public static void init() {
	}
}

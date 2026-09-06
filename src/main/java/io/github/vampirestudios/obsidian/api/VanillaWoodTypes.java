package io.github.vampirestudios.obsidian.api;

import com.google.common.collect.ImmutableMap;
import io.github.vampirestudios.obsidian.addon_modules.ContentUtils;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Map;

public class VanillaWoodTypes {
	public static final Map<Identifier, WoodType> SOUND_TYPES = ImmutableMap.<Identifier, WoodType>builder()
			.put(Identifier.withDefaultNamespace("oak"), WoodType.OAK)
			.put(Identifier.withDefaultNamespace("spruce"), WoodType.SPRUCE)
			.put(Identifier.withDefaultNamespace("birch"), WoodType.BIRCH)
			.put(Identifier.withDefaultNamespace("acacia"), WoodType.ACACIA)
			.put(Identifier.withDefaultNamespace("cherry"), WoodType.CHERRY)
			.put(Identifier.withDefaultNamespace("jungle"), WoodType.JUNGLE)
			.put(Identifier.withDefaultNamespace("dark_oak"), WoodType.DARK_OAK)
			.put(Identifier.withDefaultNamespace("crimson"), WoodType.CRIMSON)
			.put(Identifier.withDefaultNamespace("warped"), WoodType.WARPED)
			.put(Identifier.withDefaultNamespace("mangrove"), WoodType.MANGROVE)
			.put(Identifier.withDefaultNamespace("bamboo"), WoodType.BAMBOO)
			.build();

	public static void init() {
		/* nothing to do */
	}

	public static WoodType get(Identifier woodType) {
		WoodType woodType1 = SOUND_TYPES.get(woodType);
		if (WoodType.values().toList().contains(woodType1)) {
			return woodType1;
		} else if (ContentRegistries.BLOCK_SET_TYPES.containsKey(woodType)) {
			return ContentUtils.getWoodType(woodType);
		} else {
			throw new IllegalStateException("No wood type known with name " + woodType);
		}
	}
}
package io.github.vampirestudios.obsidian.villager;

import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * The biome to villager type bindings declared by addons.
 * <p>
 * Vanilla keeps its own biome map private inside {@link VillagerType} and only consults it from
 * {@code VillagerType.byBiome}, so an addon type would never be handed to a villager no matter how it
 * was registered. {@code VillagerTypeMixin} asks this class first, which also lets an addon take over a
 * biome vanilla already claims.
 */
public final class AddonVillagerTypes {

	private static final Map<ResourceKey<Biome>, ResourceKey<VillagerType>> BY_BIOME = new HashMap<>();

	private AddonVillagerTypes() {
	}

	public static void bind(ResourceKey<Biome> biome, ResourceKey<VillagerType> type) {
		ResourceKey<VillagerType> previous = BY_BIOME.put(biome, type);
		if (previous != null && !previous.equals(type)) {
			Obsidian.LOGGER.warn("Biome {} was bound to villager type {}, it is now {}",
					biome.identifier(), previous.identifier(), type.identifier());
		}
	}

	/**
	 * @return the addon villager type for this biome, or null when no addon claims it and vanilla's own
	 * mapping should decide.
	 */
	public static @Nullable ResourceKey<VillagerType> byBiome(Holder<Biome> biome) {
		if (BY_BIOME.isEmpty()) return null;
		return biome.unwrapKey().map(BY_BIOME::get).orElse(null);
	}

	public static void clear() {
		BY_BIOME.clear();
	}
}

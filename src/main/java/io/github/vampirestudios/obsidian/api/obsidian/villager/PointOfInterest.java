package io.github.vampirestudios.obsidian.api.obsidian.villager;

import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PointOfInterest {

	public Identifier id;
	public int ticket_count = 1;
	public int search_distance = 1;
	public List<Identifier> blocks;

	/**
	 * Every block state of every listed block, the way vanilla registers its own job sites. Matching
	 * only the default state would leave a lectern claimable from one facing and dead from the other
	 * three.
	 * <p>
	 * Blocks that are not registered are skipped with a warning rather than silently becoming air,
	 * which the defaulted block registry would otherwise hand back.
	 */
	public Set<BlockState> getBlocks() {
		Set<BlockState> states = new LinkedHashSet<>();
		if (blocks == null) return states;
		for (Identifier identifier : blocks) {
			if (identifier == null) continue;
			Block block = BuiltInRegistries.BLOCK.get(identifier).map(Holder::value).orElse(null);
			if (block == null) {
				Obsidian.LOGGER.warn("Point of interest {} lists unknown block {}", id, identifier);
				continue;
			}
			states.addAll(block.getStateDefinition().getPossibleStates());
		}
		return states;
	}

	public int getTicketCount() {
		return ticket_count > 0 ? ticket_count : 1;
	}

	public int getSearchDistance() {
		return search_distance > 0 ? search_distance : 1;
	}

}

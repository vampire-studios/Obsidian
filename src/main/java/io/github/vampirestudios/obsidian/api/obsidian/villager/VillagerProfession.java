package io.github.vampirestudios.obsidian.api.obsidian.villager;

import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;

public class VillagerProfession {

	public NameInformation name;

	/**
	 * Items the villager picks up and hands to other villagers, the way a farmer shares wheat.
	 */
	@SerializedName("harvestable_items")
	public List<Identifier> harvestableItems;

	/**
	 * Blocks the villager works with besides its job site, the way a farmer works farmland.
	 */
	@SerializedName("secondary_poi")
	public List<Identifier> secondaryPoi;

	public PointOfInterest poi;
	@SerializedName("work_sound")
	public Identifier workSound;

	/**
	 * Trade sets by villager level, e.g. {@code { "1": "examplepack:tinker_level_1" }}. The sets
	 * themselves are datapack entries the addon ships under {@code data/<namespace>/trade_set/}.
	 */
	public Map<String, Identifier> trades;

	public ImmutableSet<Item> getRequestedItems() {
		ImmutableSet.Builder<Item> items = ImmutableSet.builder();
		if (harvestableItems == null) return items.build();
		for (Identifier identifier : harvestableItems) {
			if (identifier == null) continue;
			Item item = BuiltInRegistries.ITEM.get(identifier).map(Holder::value).orElse(null);
			if (item == null) {
				Obsidian.LOGGER.warn("Villager profession lists unknown requested item {}", identifier);
				continue;
			}
			items.add(item);
		}
		return items.build();
	}

	public ImmutableSet<Block> getSecondaryPoi() {
		ImmutableSet.Builder<Block> blocks = ImmutableSet.builder();
		if (secondaryPoi == null) return blocks.build();
		for (Identifier identifier : secondaryPoi) {
			if (identifier == null) continue;
			Block block = BuiltInRegistries.BLOCK.get(identifier).map(Holder::value).orElse(null);
			if (block == null) {
				Obsidian.LOGGER.warn("Villager profession lists unknown secondary point of interest {}", identifier);
				continue;
			}
			blocks.add(block);
		}
		return blocks.build();
	}

	public SoundEvent getWorkSound() {
		if (workSound == null) return null;
		SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(workSound).map(Holder::value).orElse(null);
		if (sound == null) {
			Obsidian.LOGGER.warn("Villager profession lists unknown work sound {}", workSound);
		}
		return sound;
	}

	/**
	 * The trade sets are only looked up when a villager levels up, so the keys are built without
	 * checking that the datapack entry exists yet.
	 */
	public Int2ObjectMap<ResourceKey<TradeSet>> getTradeSets() {
		Int2ObjectMap<ResourceKey<TradeSet>> tradeSets = new Int2ObjectOpenHashMap<>();
		if (trades == null) return tradeSets;
		trades.forEach((level, tradeSet) -> {
			if (tradeSet == null) return;
			try {
				tradeSets.put(Integer.parseInt(level.trim()), ResourceKey.create(Registries.TRADE_SET, tradeSet));
			} catch (NumberFormatException e) {
				Obsidian.LOGGER.warn("Villager profession has trade set {} under a non-numeric level '{}'", tradeSet, level);
			}
		});
		return tradeSets;
	}

}

package com.shnupbups.oxidizelib;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import static net.minecraft.block.Oxidizable.OxidizationLevel;

import net.minecraft.block.Block;

public class OxidizableFamily {
	private final ImmutableMap<OxidizationLevel, Block> unwaxed;
	private final ImmutableMap<OxidizationLevel, Block> waxed;

	private OxidizableFamily(Map<OxidizationLevel, Block> unwaxed, Map<OxidizationLevel, Block> waxed) {
		this.unwaxed = ImmutableMap.<OxidizationLevel, Block>builder().putAll(unwaxed).build();
		this.waxed = ImmutableMap.<OxidizationLevel, Block>builder().putAll(waxed).build();
	}

	public Block getUnwaxedForLevel(OxidizationLevel level) {
		return unwaxed.get(level);
	}

	public Block getWaxedForLevel(OxidizationLevel level) {
		return waxed.get(level);
	}

	public ImmutableBiMap<Block, Block> getOxidizationLevelIncreasesMap() {
		return ImmutableBiMap.<Block, Block>builder()
				.put(getUnwaxedForLevel(OxidizationLevel.UNAFFECTED), getUnwaxedForLevel(OxidizationLevel.EXPOSED))
				.put(getUnwaxedForLevel(OxidizationLevel.EXPOSED), getUnwaxedForLevel(OxidizationLevel.WEATHERED))
				.put(getUnwaxedForLevel(OxidizationLevel.WEATHERED), getUnwaxedForLevel(OxidizationLevel.OXIDIZED))
				.build();
	}

	public ImmutableBiMap<Block, Block> getUnwaxedToWaxedMap() {
		return ImmutableBiMap.<Block, Block>builder()
				.put(getUnwaxedForLevel(OxidizationLevel.UNAFFECTED), getWaxedForLevel(OxidizationLevel.UNAFFECTED))
				.put(getUnwaxedForLevel(OxidizationLevel.EXPOSED), getWaxedForLevel(OxidizationLevel.EXPOSED))
				.put(getUnwaxedForLevel(OxidizationLevel.WEATHERED), getWaxedForLevel(OxidizationLevel.WEATHERED))
				.put(getUnwaxedForLevel(OxidizationLevel.OXIDIZED), getWaxedForLevel(OxidizationLevel.OXIDIZED))
				.build();
	}

	public static class Builder {
		private final HashMap<OxidizationLevel, Block> unwaxed = new HashMap<>();
		private final HashMap<OxidizationLevel, Block> waxed = new HashMap<>();

		private Builder add(OxidizationLevel level, Block unwaxed, Block waxed) {
			this.unwaxed.put(level, unwaxed);
			this.waxed.put(level, waxed);
			return this;
		}

		public Builder unaffected(Block unwaxed, Block waxed) {
			return add(OxidizationLevel.UNAFFECTED, unwaxed, waxed);
		}

		public Builder weathered(Block unwaxed, Block waxed) {
			return add(OxidizationLevel.WEATHERED, unwaxed, waxed);
		}

		public Builder exposed(Block unwaxed, Block waxed) {
			return add(OxidizationLevel.EXPOSED, unwaxed, waxed);
		}

		public Builder oxidized(Block unwaxed, Block waxed) {
			return add(OxidizationLevel.OXIDIZED, unwaxed, waxed);
		}

		public OxidizableFamily build() {
			for(OxidizationLevel level:OxidizationLevel.values()) {
				if(!unwaxed.containsKey(level)) {
					throw new IllegalStateException("OxidizableFamily is missing unwaxed variant for "+level);
				} else if(!waxed.containsKey(level)) {
					throw new IllegalStateException("OxidizableFamily is missing waxed variant for "+level);
				}
			}
			return new OxidizableFamily(unwaxed, waxed);
		}
	}
}
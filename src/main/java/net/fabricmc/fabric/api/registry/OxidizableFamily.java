/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

import static net.minecraft.block.Oxidizable.OxidizationLevel;

/**
 * Represents a 'family' of blocks that can be affected by Oxidization.
 *
 * <p>Like vanilla's Copper Blocks, these come in four levels of Oxidization;
 * Unaffected, Exposed, Weathered, and Oxidized.
 *
 * <p>Each block also has a 'Waxed' form.
 */
public record OxidizableFamily(
		ImmutableMap<OxidizationLevel, Block> unwaxed,
		ImmutableMap<OxidizationLevel, Block> waxed) {
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Get the unwaxed variant in this family for the given {@link OxidizationLevel}.
	 * @param level the {@link OxidizationLevel}
	 * @return the unwaxed variant
	 */
	public Block unwaxed(OxidizationLevel level) {
		return unwaxed().get(level);
	}

	/**
	 * Get the waxed variant in this family for the given {@link OxidizationLevel}.
	 * @param level the {@link OxidizationLevel}
	 * @return the waxed variant
	 */
	public Block waxed(OxidizationLevel level) {
		return waxed().get(level);
	}

	/**
	 * Builds a map of {@link OxidizationLevel} increases.
	 * @return the map
	 */
	public BiMap<Block, Block> oxidizationLevelIncreasesMap() {
		return ImmutableBiMap.<Block, Block>builder()
				.put(unwaxed(OxidizationLevel.UNAFFECTED), unwaxed(OxidizationLevel.EXPOSED))
				.put(unwaxed(OxidizationLevel.EXPOSED), unwaxed(OxidizationLevel.WEATHERED))
				.put(unwaxed(OxidizationLevel.WEATHERED), unwaxed(OxidizationLevel.OXIDIZED))
				.build();
	}

	/**
	 * Builds a map of {@link OxidizationLevel} decreases.
	 * @return the map
	 */
	public BiMap<Block, Block> oxidizationLevelDecreasesMap() {
		return oxidizationLevelIncreasesMap().inverse();
	}

	/**
	 * Builds a map of unwaxed forms to waxed counterparts.
	 * @return the map
	 */
	public BiMap<Block, Block> unwaxedToWaxedMap() {
		return ImmutableBiMap.<Block, Block>builder()
				.put(unwaxed(OxidizationLevel.UNAFFECTED), waxed(OxidizationLevel.UNAFFECTED))
				.put(unwaxed(OxidizationLevel.EXPOSED), waxed(OxidizationLevel.EXPOSED))
				.put(unwaxed(OxidizationLevel.WEATHERED), waxed(OxidizationLevel.WEATHERED))
				.put(unwaxed(OxidizationLevel.OXIDIZED), waxed(OxidizationLevel.OXIDIZED))
				.build();
	}

	/**
	 * Builds a map of waxed forms to unwaxed counterparts.
	 * @return the map
	 */
	public BiMap<Block, Block> waxedToUnwaxedMap() {
		return unwaxedToWaxedMap().inverse();
	}

	/**
	 * Allows for the creation of {@link OxidizableFamily}s.
	 */
	public static class Builder {
		private final HashMap<OxidizationLevel, Block> unwaxed = new HashMap<>();
		private final HashMap<OxidizationLevel, Block> waxed = new HashMap<>();

		/**
		 * Adds blocks to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 * @param level the {@link OxidizationLevel} of the blocks to add
		 * @param unwaxed the unwaxed variant
		 * @param waxed the waxed variant
		 * @return this builder
		 */
		public Builder add(OxidizationLevel level, Block unwaxed, Block waxed) {
			if (!(unwaxed instanceof Oxidizable)) {
				LOGGER.warn("Block " + unwaxed + " is not oxidizable, but added to OxidizableFamily as unwaxed block. This is likely an error!");
			}

			this.unwaxed.put(level, unwaxed);
			this.waxed.put(level, waxed);
			return this;
		}

		/**
		 * Adds blocks of the 'unaffected' {@link OxidizationLevel} to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 * @param unwaxed the unwaxed variant
		 * @param waxed the waxed variant
		 * @return this builder
		 */
		public Builder unaffected(Block unwaxed, Block waxed) {
			return add(OxidizationLevel.UNAFFECTED, unwaxed, waxed);
		}

		/**
		 * Adds blocks of the 'weathered' {@link OxidizationLevel} to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 * @param unwaxed the unwaxed variant
		 * @param waxed the waxed variant
		 * @return this builder
		 */
		public Builder weathered(Block unwaxed, Block waxed) {
			return add(OxidizationLevel.WEATHERED, unwaxed, waxed);
		}

		/**
		 * Adds blocks of the 'exposed' {@link OxidizationLevel} to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 * @param unwaxed the unwaxed variant
		 * @param waxed the waxed variant
		 * @return this builder
		 */
		public Builder exposed(Block unwaxed, Block waxed) {
			return add(OxidizationLevel.EXPOSED, unwaxed, waxed);
		}

		/**
		 * Adds blocks of the 'oxidized' {@link OxidizationLevel} to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 * @param unwaxed the unwaxed variant
		 * @param waxed the waxed variant
		 * @return this builder
		 */
		public Builder oxidized(Block unwaxed, Block waxed) {
			return add(OxidizationLevel.OXIDIZED, unwaxed, waxed);
		}

		/**
		 * Builds this {@link OxidizableFamily}.
		 * @return the {@link OxidizableFamily}
		 * @throws IllegalStateException if any variants are missing or null
		 */
		public OxidizableFamily build() {
			for (OxidizationLevel level : OxidizationLevel.values()) {
				if (!unwaxed.containsKey(level) || unwaxed.get(level) == null) {
					throw new IllegalStateException("OxidizableFamily is missing unwaxed variant for " + level + "!");
				}

				if (!waxed.containsKey(level) || waxed.get(level) == null) {
					throw new IllegalStateException("OxidizableFamily is missing waxed variant for " + level + "!");
				}
			}

			return new OxidizableFamily(ImmutableMap.copyOf(unwaxed), ImmutableMap.copyOf(waxed));
		}
	}
}
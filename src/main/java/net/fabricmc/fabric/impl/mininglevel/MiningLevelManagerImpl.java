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

package net.fabricmc.fabric.impl.mininglevel;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MiningLevelManagerImpl {
	private static final Logger LOGGER = LogManager.getLogger("fabric-mining-level-api-v1/MiningLevelManagerImpl");
	private static final String TOOL_TAG_NAMESPACE = "fabric";
	private static final Pattern TOOL_TAG_PATTERN = Pattern.compile("^needs_tool_level_([0-9]+)$");

	// A cache of block state mining levels. Cleared by
	// - MiningLevelCacheInvalidator when tags are reloaded
	// - ClientPlayNetworkHandlerMixin when tags are synced
	private static final ThreadLocal<Reference2IntMap<BlockState>> CACHE = ThreadLocal.withInitial(Reference2IntOpenHashMap::new);

	public static int getRequiredMiningLevel(BlockState state) {
		return CACHE.get().computeIntIfAbsent(state, s -> {
			TagGroup<Block> blockTags = BlockTags.getTagGroup();
			int miningLevel = MiningLevels.HAND;

			// Handle #fabric:needs_tool_level_N
			for (Identifier tagId : blockTags.getTagsFor(state.getBlock())) {
				if (!tagId.getNamespace().equals(TOOL_TAG_NAMESPACE)) {
					continue;
				}

				Matcher matcher = TOOL_TAG_PATTERN.matcher(tagId.getPath());

				if (matcher.matches()) {
					try {
						int tagMiningLevel = Integer.parseInt(matcher.group(1));

						if (tagMiningLevel < 0) {
							LOGGER.warn("#{} has a negative mining level which has no effects!", tagId);
						}

						miningLevel = Math.max(miningLevel, tagMiningLevel);
					} catch (NumberFormatException e) {
						LOGGER.error("Could not read mining level from tag #{}", tagId, e);
					}
				}
			}

			// Handle vanilla tags
			if (state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
				miningLevel = Math.max(miningLevel, MiningLevels.DIAMOND);
			} else if (state.isIn(BlockTags.NEEDS_IRON_TOOL)) {
				miningLevel = Math.max(miningLevel, MiningLevels.IRON);
			} else if (state.isIn(BlockTags.NEEDS_STONE_TOOL)) {
				miningLevel = Math.max(miningLevel, MiningLevels.STONE);
			}

			return miningLevel;
		});
	}

	public static void clearCache() {
		CACHE.get().clear();
	}
}
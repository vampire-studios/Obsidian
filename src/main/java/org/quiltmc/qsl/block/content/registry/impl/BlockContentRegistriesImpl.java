/*
 * Copyright 2022 The Quilt Project
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

package org.quiltmc.qsl.block.content.registry.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import io.github.vampirestudios.obsidian.ResourceLoaderEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.qsl.block.content.registry.api.BlockContentRegistries;
import org.quiltmc.qsl.block.content.registry.api.FlammableBlockEntry;
import org.quiltmc.qsl.block.content.registry.api.ReversibleBlockEntry;
import org.quiltmc.qsl.registry.attachment.api.RegistryEntryAttachment;

import java.util.Map;
import java.util.function.BiFunction;

@ApiStatus.Internal
public class BlockContentRegistriesImpl implements ModInitializer {
	private static final Map<Block, BlockState> INITIAL_PATH_STATES = ImmutableMap.copyOf(ShovelItem.FLATTENABLES);
	private static final Map<Block, Block> INITIAL_STRIPPED_BLOCKS = ImmutableMap.copyOf(AxeItem.STRIPPABLES);

	@Override
	public void onInitialize() {
		// Fill the initial flammable blocks map
		var builder = ImmutableMap.<Block, FlammableBlockEntry>builder();
		FireBlock fireBlock = ((FireBlock) Blocks.FIRE);
		fireBlock.burnOdds.keySet().forEach(block ->
				builder.put(block, new FlammableBlockEntry(fireBlock.igniteOdds.getInt(block), fireBlock.burnOdds.getInt(block)))
		);
		var initialFlammableBlocks = builder.build();

		addMapToAttachment(INITIAL_PATH_STATES, BlockContentRegistries.FLATTENABLE);
		addMapToAttachment(INITIAL_STRIPPED_BLOCKS, BlockContentRegistries.STRIPPABLE);
		addMapToAttachment(initialFlammableBlocks, BlockContentRegistries.FLAMMABLE);

		resetMaps();
		ResourceLoaderEvents.END_DATA_PACK_RELOAD.register(context -> {
			if (context.error().isPresent()) return;
			resetMaps();
		});
	}

	private static void resetMaps() {
		ShovelItem.FLATTENABLES.clear();
		setMapFromAttachment(ShovelItem.FLATTENABLES::put, BlockContentRegistries.FLATTENABLE);

		AxeItem.STRIPPABLES.clear();
		setMapFromAttachment(AxeItem.STRIPPABLES::put, BlockContentRegistries.STRIPPABLE);

//		resetSimpleReversibleMap(OXIDATION_INCREASE_BLOCKS, OXIDATION_DECREASE_BLOCKS, BlockContentRegistries.OXIDIZABLE);
//
//		resetSimpleReversibleMap(UNWAXED_WAXED_BLOCKS, WAXED_UNWAXED_BLOCKS, BlockContentRegistries.WAXABLE);

		FireBlock fireBlock = ((FireBlock) Blocks.FIRE);
		fireBlock.igniteOdds.clear();
		fireBlock.burnOdds.clear();
		BlockContentRegistries.FLAMMABLE.registry().stream().forEach(entry -> BlockContentRegistries.FLAMMABLE.get(entry).ifPresent(v -> {
			fireBlock.igniteOdds.put(entry, v.burn());
			fireBlock.burnOdds.put(entry, v.spread());
		}));
	}

	private static <T, V> void setMapFromAttachment(BiFunction<T, V, ?> map, RegistryEntryAttachment<T, V> attachment) {
		attachment.forEach(entry -> map.apply(entry.entry(), entry.value()));
	}

	private static <T, V> void addMapToAttachment(Map<T, V> map, RegistryEntryAttachment<T, V> attachment) {
		map.forEach(attachment::put);
	}

	private static void resetSimpleReversibleMap(BiMap<Block, Block> baseWay, BiMap<Block, Block> reversed,
			RegistryEntryAttachment<Block, ReversibleBlockEntry> rea) {
		baseWay.clear();
		reversed.clear();
		setMapFromAttachment((entry, value) -> baseWay.put(entry, value.block()), rea);
		setMapFromAttachment((entry, value) -> value.reversible() ? reversed.put(value.block(), entry) : null, rea);
	}

	/**
	 * Calculates the bookshelf count around a given position.
	 *
	 * @param world the world
	 * @param pos   the position to count around of
	 * @return the bookshelf count around
	 */
	public static float calculateBookshelfCount(Level world, BlockPos pos) {
		float count = 0;

		for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
			if (world.isEmptyBlock(pos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2))) {
				var blockPos = pos.offset(offset);
				var state = world.getBlockState(blockPos);
				var block = state.getBlock();
				count += BlockContentRegistries.ENCHANTING_BOOSTERS.get(block)
						.map(booster -> booster.getEnchantingBoost(world, state, blockPos))
						.orElse(0.0F);
			}
		}

		return count;
	}
}
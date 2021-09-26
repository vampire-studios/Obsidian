package com.shnupbups.oxidizelib;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.item.HoneycombItem;

public class OxidizeLib {
	public static final ArrayList<OxidizableFamily> OXIDIZABLE_FAMILIES = new ArrayList<>();

	private static Supplier<BiMap<Block, Block>> OXIDIZATION_LEVEL_INCREASES;
	private static Supplier<BiMap<Block, Block>> OXIDIZATION_LEVEL_DECREASES;
	private static Supplier<BiMap<Block, Block>> UNWAXED_TO_WAXED;
	private static Supplier<BiMap<Block, Block>> WAXED_TO_UNWAXED;

	public static void registerOxidizableFamily(OxidizableFamily family) {
		OXIDIZABLE_FAMILIES.add(family);
	}

	public static Supplier<BiMap<Block, Block>> getOxidizationLevelIncreases() {
		if(OXIDIZATION_LEVEL_INCREASES != null) return OXIDIZATION_LEVEL_INCREASES;

		ImmutableBiMap.Builder<Block, Block> builder = ImmutableBiMap.builder();

		builder.putAll(Oxidizable.OXIDATION_LEVEL_INCREASES.get());

		for(OxidizableFamily family:OXIDIZABLE_FAMILIES) {
			builder.putAll(family.getOxidizationLevelIncreasesMap());
		}

		OXIDIZATION_LEVEL_INCREASES = Suppliers.memoize(builder::build);

		return OXIDIZATION_LEVEL_INCREASES;
	}

	public static Supplier<BiMap<Block, Block>> getOxidizationLevelDecreases() {
		if(OXIDIZATION_LEVEL_DECREASES != null) return OXIDIZATION_LEVEL_DECREASES;

		OXIDIZATION_LEVEL_DECREASES = Suppliers.memoize(() -> getOxidizationLevelIncreases().get().inverse());

		return OXIDIZATION_LEVEL_DECREASES;
	}

	public static Supplier<BiMap<Block, Block>> getUnwaxedToWaxed() {
		if(UNWAXED_TO_WAXED != null) return UNWAXED_TO_WAXED;

		ImmutableBiMap.Builder<Block, Block> builder = ImmutableBiMap.builder();

		builder.putAll(HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get());

		for(OxidizableFamily family:OXIDIZABLE_FAMILIES) {
			builder.putAll(family.getUnwaxedToWaxedMap());
		}

		UNWAXED_TO_WAXED = Suppliers.memoize(builder::build);

		return UNWAXED_TO_WAXED;
	}

	public static Supplier<BiMap<Block, Block>> getWaxedToUnwaxed() {
		if(WAXED_TO_UNWAXED != null) return WAXED_TO_UNWAXED;

		WAXED_TO_UNWAXED = Suppliers.memoize(() -> getUnwaxedToWaxed().get().inverse());

		return WAXED_TO_UNWAXED;
	}

	public static Optional<Block> getDecreasedOxidizationBlock(Block block) {
		return Optional.ofNullable(getOxidizationLevelDecreases().get().get(block));
	}

	public static Optional<BlockState> getDecreasedOxidizationState(BlockState state) {
		return getDecreasedOxidizationBlock(state.getBlock()).map((block) -> block.getStateWithProperties(state));
	}

	public static Optional<Block> getIncreasedOxidizationBlock(Block block) {
		return Optional.ofNullable(getOxidizationLevelIncreases().get().get(block));
	}

	public static Optional<BlockState> getIncreasedOxidizationState(BlockState state) {
		return getIncreasedOxidizationBlock(state.getBlock()).map((block) -> block.getStateWithProperties(state));
	}

	public static Optional<Block> getWaxedBlock(Block block) {
		return Optional.ofNullable(getUnwaxedToWaxed().get().get(block));
	}

	public static Optional<BlockState> getWaxedState(BlockState state) {
		return getWaxedBlock(state.getBlock()).map((block) -> block.getStateWithProperties(state));
	}

	public static Optional<Block> getUnwaxedBlock(Block block) {
		return Optional.ofNullable(getWaxedToUnwaxed().get().get(block));
	}

	public static Optional<BlockState> getUnwaxedState(BlockState state) {
		return getUnwaxedBlock(state.getBlock()).map((block) -> block.getStateWithProperties(state));
	}

	public static Block getUnaffectedOxidizationBlock(Block block) {
		Block block2 = block;

		for(Block block3 = getOxidizationLevelDecreases().get().get(block); block3 != null; block3 = getOxidizationLevelDecreases().get().get(block3)) {
			block2 = block3;
		}

		return block2;
	}

	public static BlockState getUnaffectedOxidizationState(BlockState state) {
		return getUnaffectedOxidizationBlock(state.getBlock()).getStateWithProperties(state);
	}
}
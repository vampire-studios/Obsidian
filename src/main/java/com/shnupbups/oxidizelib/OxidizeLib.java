package com.shnupbups.oxidizelib;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.item.HoneycombItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class OxidizeLib {
	protected static final List<OxidizableFamily> OXIDIZABLE_FAMILIES = new ArrayList<>();

	private static Supplier<BiMap<Block, Block>> oxidizationLevelIncreases;
	private static Supplier<BiMap<Block, Block>> oxidizationLevelDecreases;
	private static Supplier<BiMap<Block, Block>> unwaxedToWaxed;
	private static Supplier<BiMap<Block, Block>> waxedToUnwaxed;

	public static void registerOxidizableFamily(OxidizableFamily family) {
		OXIDIZABLE_FAMILIES.add(family);
	}

	public static Supplier<BiMap<Block, Block>> getOxidizationLevelIncreases() {
		if(oxidizationLevelIncreases != null) return oxidizationLevelIncreases;

		ImmutableBiMap.Builder<Block, Block> builder = ImmutableBiMap.builder();

		builder.putAll(Oxidizable.OXIDATION_LEVEL_INCREASES.get());

		for(OxidizableFamily family:OXIDIZABLE_FAMILIES) {
			builder.putAll(family.getOxidizationLevelIncreasesMap());
		}

		oxidizationLevelIncreases = Suppliers.memoize(builder::build);

		return oxidizationLevelIncreases;
	}

	public static Supplier<BiMap<Block, Block>> getOxidizationLevelDecreases() {
		if(oxidizationLevelDecreases != null) return oxidizationLevelDecreases;

		oxidizationLevelDecreases = Suppliers.memoize(() -> getOxidizationLevelIncreases().get().inverse());

		return oxidizationLevelDecreases;
	}

	public static Supplier<BiMap<Block, Block>> getUnwaxedToWaxed() {
		if(unwaxedToWaxed != null) return unwaxedToWaxed;

		ImmutableBiMap.Builder<Block, Block> builder = ImmutableBiMap.builder();

		builder.putAll(HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get());

		for(OxidizableFamily family:OXIDIZABLE_FAMILIES) {
			builder.putAll(family.getUnwaxedToWaxedMap());
		}

		unwaxedToWaxed = Suppliers.memoize(builder::build);

		return unwaxedToWaxed;
	}

	public static Supplier<BiMap<Block, Block>> getWaxedToUnwaxed() {
		if(waxedToUnwaxed != null) return waxedToUnwaxed;

		waxedToUnwaxed = Suppliers.memoize(() -> getUnwaxedToWaxed().get().inverse());

		return waxedToUnwaxed;
	}

	public static Optional<Block> getDecreasedOxidizationBlock(Block block) {
		return Optional.ofNullable(getOxidizationLevelDecreases().get().get(block));
	}

	public static Optional<Block> getIncreasedOxidizationBlock(Block block) {
		return Optional.ofNullable(getOxidizationLevelIncreases().get().get(block));
	}

	public static Optional<Block> getWaxedBlock(Block block) {
		return Optional.ofNullable(getUnwaxedToWaxed().get().get(block));
	}

	public static Optional<BlockState> getWaxedState(BlockState state) {
		return getWaxedBlock(state.getBlock()).map(block -> block.getStateWithProperties(state));
	}

	public static Optional<Block> getUnwaxedBlock(Block block) {
		return Optional.ofNullable(getWaxedToUnwaxed().get().get(block));
	}

	public static Optional<BlockState> getUnwaxedState(BlockState state) {
		return getUnwaxedBlock(state.getBlock()).map(block -> block.getStateWithProperties(state));
	}

	public static Block getUnaffectedOxidizationBlock(Block block) {
		Block block2 = block;

		for(Block block3 = getOxidizationLevelDecreases().get().get(block); block3 != null; block3 = getOxidizationLevelDecreases().get().get(block3)) {
			block2 = block3;
		}

		return block2;
	}

}
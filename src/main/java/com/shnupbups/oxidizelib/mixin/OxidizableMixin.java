package com.shnupbups.oxidizelib.mixin;

import java.util.Optional;

import com.shnupbups.oxidizelib.OxidizeLib;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;

@Mixin(Oxidizable.class)
public interface OxidizableMixin {
	/**
	 * @author Shnupbups
	 * @reason thonkjang
	 */
	@Overwrite
	static Optional<Block> getDecreasedOxidationBlock(Block block) {
		return OxidizeLib.getDecreasedOxidizationBlock(block);
	}

	/**
	 * @author Shnupbups
	 * @reason thonkjang
	 */
	@Overwrite
	static Optional<Block> getIncreasedOxidationBlock(Block block) {
		return OxidizeLib.getIncreasedOxidizationBlock(block);
	}

	/**
	 * @author Shnupbups
	 * @reason thonkjang
	 */
	@Overwrite
	static Block getUnaffectedOxidationBlock(Block block) {
		return OxidizeLib.getUnaffectedOxidizationBlock(block);
	}
}
package io.github.vampirestudios.obsidian;

import net.minecraft.block.Block;

public class ConvertableOxidizableBlock {
	public Block left;
	public Block right;

	public ConvertableOxidizableBlock(Block left, Block right) {
		this.left = left;
		this.right = right;
	}

	public Block getLeft() {
		return left;
	}

	public Block getRight() {
		return right;
	}
}

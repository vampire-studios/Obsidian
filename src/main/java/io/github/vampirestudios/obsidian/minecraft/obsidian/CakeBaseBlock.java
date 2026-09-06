package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CakeBaseBlock extends CakeBlock {
	private final int slices;

	public CakeBaseBlock(BlockBehaviour.Properties properties) {
		this(7, properties);
	}

	public CakeBaseBlock(int slices, BlockBehaviour.Properties properties) {
		super(properties.strength(0.5F).sound(SoundType.WOOL));
		this.slices = slices;
	}

	public int getSlices() {
		return this.slices;
	}
}

package io.github.vampirestudios.obsidian.minecraft.bedrock;

import io.github.vampirestudios.obsidian.api.bedrock.block.BaseBlock;
import net.minecraft.block.Block;

public class BlockImpl extends Block {

	public BaseBlock block;

	public BlockImpl(BaseBlock block, Settings settings) {
		super(settings);
		this.block = block;
	}

}
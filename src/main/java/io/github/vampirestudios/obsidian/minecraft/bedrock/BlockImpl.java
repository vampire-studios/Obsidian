package io.github.vampirestudios.obsidian.minecraft.bedrock;

import io.github.vampirestudios.obsidian.api.bedrock.block.BaseBlock;
import net.minecraft.world.level.block.Block;

public class BlockImpl extends Block {

    public BaseBlock block;

    public BlockImpl(BaseBlock block, Properties settings) {
        super(settings);
        this.block = block;
    }

}
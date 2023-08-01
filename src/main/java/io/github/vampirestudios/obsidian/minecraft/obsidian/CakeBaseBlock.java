package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.SoundType;

public class CakeBaseBlock extends CakeBlock {
    private final int slices;

    public CakeBaseBlock() {
        this(7);
    }

    public CakeBaseBlock(int slices) {
        super(Properties.of().strength(0.5F).sound(SoundType.WOOL));
        this.slices = slices;
    }

    public int getSlices() {
        return this.slices;
    }
}

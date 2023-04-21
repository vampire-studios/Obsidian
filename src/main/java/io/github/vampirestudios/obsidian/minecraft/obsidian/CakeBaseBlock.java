package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class CakeBaseBlock extends CakeBlock {
    private final int slices;

    public CakeBaseBlock() {
        this(7);
    }

    public CakeBaseBlock(int slices) {
        super(Properties.of(Material.DEPRECATED, MaterialColor.NONE).strength(0.5F).sound(SoundType.WOOL));
        this.slices = slices;
    }

    public int getSlices() {
        return this.slices;
    }
}

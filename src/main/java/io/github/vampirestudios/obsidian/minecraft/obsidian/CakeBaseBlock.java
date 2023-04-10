package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.block.CakeBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class CakeBaseBlock extends CakeBlock {
    private final int slices;

    public CakeBaseBlock() {
        this(7);
    }

    public CakeBaseBlock(int slices) {
        super(Settings.of(Material.GENERIC, MapColor.CLEAR).strength(0.5F).sounds(BlockSoundGroup.WOOL));
        this.slices = slices;
    }

    public int getSlices() {
        return this.slices;
    }
}

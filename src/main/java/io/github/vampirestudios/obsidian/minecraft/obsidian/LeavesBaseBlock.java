package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;

public class LeavesBaseBlock extends LeavesBlock {
    public LeavesBaseBlock() {
        super(Settings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS));
    }
}

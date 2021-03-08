package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.block.LadderBlock;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.Direction;

public class CustomLadderBlock extends LadderBlock {
    public CustomLadderBlock() {
        super(Settings.of(Material.AGGREGATE).strength(0.4F).sounds(BlockSoundGroup.LADDER).nonOpaque());
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }
}

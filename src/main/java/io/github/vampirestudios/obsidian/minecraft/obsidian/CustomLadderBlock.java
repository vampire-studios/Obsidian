package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;

public class CustomLadderBlock extends LadderBlock {
    public CustomLadderBlock() {
        super(Properties.of(Material.AGGREGATE).strength(0.4F).sounds(SoundType.LADDER).nonOpaque());
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }
}

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class CustomLadderBlock extends LadderBlock {
    public CustomLadderBlock() {
        super(Properties.of(Material.DEPRECATED).strength(0.4F).sound(SoundType.LADDER).noCollission());
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }
}

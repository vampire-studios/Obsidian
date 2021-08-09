package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.vampirelib.api.Climbable;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class TrapdoorBlockImpl extends TrapdoorBlock implements Climbable {
    public TrapdoorBlockImpl(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    public ClimbBehavior getClimbBehavior(Entity entity, BlockState state, BlockPos pos) {
        return ClimbBehavior.Ladder;
    }
}

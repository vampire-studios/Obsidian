package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CustomLadderBlock extends LadderBlock {
	public CustomLadderBlock(BlockBehaviour.Properties properties) {
		super(properties.strength(0.4F).sound(SoundType.LADDER).noCollision());
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}
}

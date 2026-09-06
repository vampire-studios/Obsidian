package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * A horizontal block that carries the {@code occupied} state a chair needs. The sitting itself is
 * {@link SeatLogic}, reached through {@link HorizontalFacingBlockImpl}'s interaction handling, so
 * this type and a plain block declaring {@code behaviour.seat} behave identically.
 */
public class HorizontalFacingSittableBlock extends HorizontalFacingBlockImpl {
	public static final BooleanProperty OCCUPIED = SeatLogic.OCCUPIED;

	public HorizontalFacingSittableBlock(Block block, Properties settings) {
		super(block, settings);
		this.registerDefaultState(this.defaultBlockState().setValue(OCCUPIED, false).setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(OCCUPIED));
	}

}

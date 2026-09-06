package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * The dyeable counterpart of {@link HorizontalFacingSittableBlock}; the sitting is
 * {@link SeatLogic} either way.
 */
public class HorizontalFacingSittableAndDyableBlock extends HorizontalFacingDyableBlockImpl {
	public static final BooleanProperty OCCUPIED = SeatLogic.OCCUPIED;

	public HorizontalFacingSittableAndDyableBlock(Identifier id, Block block, Properties settings) {
		super(id, block, settings);
		this.registerDefaultState(this.defaultBlockState().setValue(OCCUPIED, false).setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(OCCUPIED));
	}
}

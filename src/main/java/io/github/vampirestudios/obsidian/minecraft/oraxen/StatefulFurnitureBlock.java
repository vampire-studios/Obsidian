package io.github.vampirestudios.obsidian.minecraft.oraxen;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Furniture carrying its current Nexo state directly in the block state. */
public class StatefulFurnitureBlock extends FurnitureBlock {
	public StatefulFurnitureBlock(NexoItem.Mechanics.Furniture mech, Properties settings) {
		super(mech, settings);
		this.registerDefaultState(this.defaultBlockState().setValue(FURNITURE_STATE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FURNITURE_STATE);
	}
}

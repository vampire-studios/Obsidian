package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.BlockSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

public class PowerableBlockImpl extends BlockImpl {

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public PowerableBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
		super(block, settings);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		// A pack may also have listed "powered" in vanilla_properties; adding it twice fails the build.
		if (!declaresProperty(POWERED)) builder.add(POWERED);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, Orientation orientation, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, sourceBlock, orientation, movedByPiston);
		if (block.information.powerable) {
			boolean powered = level.hasNeighborSignal(pos);
			if (powered != state.getValue(POWERED)) {
				level.setBlock(pos, state.setValue(POWERED, powered), 2);
			}
		}
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (!oldState.is(this) && block.information.powerable) {
			boolean powered = level.hasNeighborSignal(pos);
			if (powered != state.getValue(POWERED)) {
				level.setBlock(pos, state.setValue(POWERED, powered), 2);
			}
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		// Always go through super so the on_interact event fires for toggleable blocks too.
		InteractionResult result = super.useWithoutItem(state, level, pos, player, hit);
		if (block.information.toggleable) {
			if (!level.isClientSide()) {
				level.setBlock(pos, state.cycle(POWERED), 3);
			}
			return InteractionResult.SUCCESS;
		}
		return result;
	}

}

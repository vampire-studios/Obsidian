package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * The {@code powered} state, for any kind of block.
 *
 * <p>A block declaring {@code information.powerable} follows the redstone signal around it;
 * {@code toggleable} flips on being used. Both put the same {@code powered} state on the block, which is
 * what a pack's powered model, powered luminance and {@code on_powered} handling all read.
 *
 * <p>Blocks come from a different vanilla class per shape — a stairs is a {@code StairBlock}, a fence a
 * {@code FenceBlock} — so there is no one place to put this. An implementation says it is powerable by
 * implementing this and calling the three methods from the hooks it already has.
 */
public interface PoweredBlock {

	BooleanProperty POWERED = BlockStateProperties.POWERED;

	/** The declaration this block was built from. */
	Block declaration();

	/** Whether a declaration asked for the state at all. */
	static boolean declaresPowered(Block block) {
		return block != null && block.information != null
				&& (block.information.powerable || block.information.toggleable);
	}

	default boolean isPowerable() {
		return declaration() != null && declaration().information != null && declaration().information.powerable;
	}

	default boolean isToggleable() {
		return declaration() != null && declaration().information != null && declaration().information.toggleable;
	}

	/** Adds the state, from {@code createBlockStateDefinition}. */
	default void definePowered(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		if (declaresPowered(declaration())) builder.add(POWERED);
	}

	/**
	 * Brings the state back in line with the redstone around the block, from {@code neighborChanged} and
	 * {@code onPlace}. Does nothing for a block that only toggles by hand.
	 */
	default void refreshPower(BlockState state, Level level, BlockPos pos) {
		if (!isPowerable() || !state.hasProperty(POWERED)) return;

		boolean powered = level.hasNeighborSignal(pos);
		if (powered != state.getValue(POWERED)) {
			level.setBlock(pos, state.setValue(POWERED, powered), 2);
		}
	}

	/**
	 * Flips the state, from {@code useWithoutItem}.
	 *
	 * @return whether the block was toggled, so the caller knows to report the interaction as handled
	 */
	default boolean togglePower(BlockState state, Level level, BlockPos pos) {
		if (!isToggleable() || !state.hasProperty(POWERED)) return false;

		if (!level.isClientSide()) level.setBlock(pos, state.cycle(POWERED), 3);
		return true;
	}

	/** {@link #togglePower} as the result a {@code useWithoutItem} override returns. */
	default InteractionResult useToToggle(BlockState state, Level level, BlockPos pos, InteractionResult otherwise) {
		return togglePower(state, level, pos) ? InteractionResult.SUCCESS : otherwise;
	}
}

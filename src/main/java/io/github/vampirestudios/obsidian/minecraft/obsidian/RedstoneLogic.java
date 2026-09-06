package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

/**
 * The two redstone outputs a block definition can declare: {@code behaviour.power_source}, a block that
 * emits a constant signal, and {@code behaviour.repeater}, a block that passes a signal on after a delay.
 *
 * <p>These are outputs. {@code information.powerable} and {@code toggleable} are the inputs — a block that
 * watches for a signal — and the two sides are independent; a block may do both.
 *
 * <p>Repeaters follow vanilla's diode convention exactly: input comes from behind, output goes out the
 * front, and {@code facing} is set on placement so that the front is the way the placing player was
 * looking. A block with no facing at all has no front, so it cannot be a repeater.
 */
public final class RedstoneLogic {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final int MAX_SIGNAL = 15;

	private RedstoneLogic() {
	}

	private static Block.Behaviour.@Nullable PowerSource powerSource(Block block) {
		return block == null || block.behaviour == null ? null : block.behaviour.power_source;
	}

	private static Block.Behaviour.@Nullable Repeater repeater(Block block) {
		return block == null || block.behaviour == null ? null : block.behaviour.repeater;
	}

	public static boolean isRepeater(Block block) {
		return repeater(block) != null;
	}

	/** Whether the block emits anything at all, which is what vanilla asks before it bothers reading it. */
	public static boolean isSignalSource(Block block) {
		return powerSource(block) != null || isRepeater(block);
	}

	/**
	 * The signal this block gives the neighbour asking. {@code towards} is vanilla's argument: the direction
	 * from the asking block back to this one, which a repeater compares against its own facing the way
	 * {@code DiodeBlock} does.
	 */
	public static int signal(Block block, BlockState state, Direction towards,
	                         @Nullable IntegerProperty power, @Nullable Direction facing) {
		if (power != null && state.hasProperty(power)) {
			return facing == towards ? state.getValue(power) : 0;
		}

		Block.Behaviour.PowerSource source = powerSource(block);
		return source == null ? 0 : clamp(source.value);
	}

	/**
	 * Recomputes a repeater's output, scheduling the change {@code delay} ticks out rather than applying it
	 * now — which is the delay, and is also what keeps a chain of them from resolving in a single tick.
	 */
	public static void onNeighbourChanged(Block block, Level level, BlockPos pos, BlockState state,
	                                      @Nullable IntegerProperty power, @Nullable Direction facing) {
		Block.Behaviour.Repeater declared = repeater(block);
		if (declared == null || power == null || !state.hasProperty(power)) return;
		if (!(level instanceof ServerLevel serverLevel)) return;

		if (facing == null) {
			LOGGER.warn("Block {} declares behaviour.repeater but has no facing to output along; "
							+ "give it a horizontal_directional or directional block type.",
					block.information != null ? block.information.id : "?");
			return;
		}

		if (target(serverLevel, pos, facing, declared) == state.getValue(power)) return;
		if (serverLevel.getBlockTicks().hasScheduledTick(pos, state.getBlock())) return;

		serverLevel.scheduleTick(pos, state.getBlock(), Math.max(1, declared.delay));
	}

	/** Applies the output the scheduled tick was waiting to apply. */
	public static void onScheduledTick(Block block, ServerLevel level, BlockPos pos, BlockState state,
	                                   @Nullable IntegerProperty power, @Nullable Direction facing) {
		Block.Behaviour.Repeater declared = repeater(block);
		if (declared == null || power == null || facing == null || !state.hasProperty(power)) return;

		int output = target(level, pos, facing, declared);
		if (output == state.getValue(power)) return;

		level.setBlock(pos, state.setValue(power, output), 2);
		level.updateNeighborsAt(pos, state.getBlock());
	}

	/**
	 * What the repeater should be putting out: whatever arrives at its back, less {@code loss}. A loss of
	 * zero passes the signal on at full strength, which is the thing vanilla redstone cannot do.
	 */
	private static int target(Level level, BlockPos pos, Direction facing, Block.Behaviour.Repeater declared) {
		BlockPos behind = pos.relative(facing);
		int input = level.getSignal(behind, facing);
		return clamp(input - Math.max(0, declared.loss));
	}

	private static int clamp(int signal) {
		return Math.max(0, Math.min(MAX_SIGNAL, signal));
	}
}

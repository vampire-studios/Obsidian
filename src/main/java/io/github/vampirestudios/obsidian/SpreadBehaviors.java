package io.github.vampirestudios.obsidian;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpreadBehaviors {
	private static final Table<Block, SpreaderType, ISpreadingBehavior> SPREADERS = HashBasedTable.create();

	static {
		setupVanillaBehavior();
	}

	/**
	 * This method allows you to add spreading behavior, which is used by {@link IForgeSpreadingBlock}
	 */
	private static void addSpreaderBehavior(Block block, SpreaderType type, ISpreadingBehavior behavior) {
		if (SPREADERS.contains(block, type))
			Obsidian.LOGGER.info("Replacing spreading behavior for block '{}' and spreader type '{}'", block, type.getName());
		SPREADERS.put(block, type, behavior);
	}

	/**
	 * This method allows you to add a simple spreading behavior
	 */
	public static void addSimpleSpreaderBehavior(Block block, SpreaderType type) {
		addSpreaderBehavior(block, type, new SimpleSpreaderBehavior(block.getDefaultState()));
	}

	/**
	 * This method allows you to add a complex spreading behavior
	 */
	public static void addComplexSpreaderBehavior(Block block, SpreaderType type, ISpreadingBehavior behavior) {
		addSpreaderBehavior(block, type, behavior);
	}

	public static boolean canSpread(BlockState state, SpreaderType type) {
		return getSpreadingBehavior(state.getBlock(), type) != null;
	}

	public static BlockState getSpreadState(BlockState state, World level, BlockPos pos, SpreaderType type) {
		ISpreadingBehavior behavior = getSpreadingBehavior(state.getBlock(), type);
		if (behavior == null) return state;
		return behavior.getSpreadingState(state, level, pos);
	}

	private static ISpreadingBehavior getSpreadingBehavior(Block block, SpreaderType type) {
		return SPREADERS.get(block, type);
	}

	private static void setupVanillaBehavior() {
		addSpreaderBehavior(
				Blocks.DIRT,
				SpreaderType.GRASS,
				(state, level, pos) -> Blocks.GRASS_BLOCK.getDefaultState().with(Properties.SNOWY, level.getBlockState(pos.up()).isOf(Blocks.SNOW))
		);
		addSpreaderBehavior(
				Blocks.GRASS_BLOCK,
				SpreaderType.REVERT,
				(state, level, pos) -> Blocks.DIRT.getDefaultState()
		);
		addSpreaderBehavior(
				Blocks.DIRT,
				SpreaderType.MYCELIUM,
				(state, level, pos) -> Blocks.MYCELIUM.getDefaultState()
						.with( Properties.SNOWY, level.getBlockState( pos.up() ).isOf(Blocks.SNOW) )
		);
		addSpreaderBehavior(
				Blocks.MYCELIUM,
				SpreaderType.REVERT,
				(state, level, pos) -> Blocks.DIRT.getDefaultState()
		);
		addSpreaderBehavior(
				Blocks.NETHERRACK,
				SpreaderType.CRIMSON,
				(state, level, pos) -> Blocks.CRIMSON_NYLIUM.getDefaultState()
		);
		addSpreaderBehavior(
				Blocks.CRIMSON_NYLIUM,
				SpreaderType.REVERT,
				(state, level, pos) -> Blocks.NETHERRACK.getDefaultState()
		);
		addSpreaderBehavior(
				Blocks.NETHERRACK,
				SpreaderType.WARPED,
				(state, level, pos) -> Blocks.WARPED_NYLIUM.getDefaultState()
		);
		addSpreaderBehavior(
				Blocks.WARPED_NYLIUM,
				SpreaderType.REVERT,
				(state, level, pos) -> Blocks.NETHERRACK.getDefaultState()
		);
	}

	public static class SimpleSpreaderBehavior implements ISpreadingBehavior {

		private final BlockState state;

		public SimpleSpreaderBehavior(BlockState state) {
			this.state = state;
		}

		@Override
		public BlockState getSpreadingState(BlockState state, World level, BlockPos pos) {
			return this.state;
		}
	}

}
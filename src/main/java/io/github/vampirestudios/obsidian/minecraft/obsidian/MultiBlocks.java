package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.block.MultiBlockVariants;
import io.github.vampirestudios.obsidian.registry.properties.ListProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/**
 * Building and taking down the cells of a multi-block. The block implementations own the state property and
 * the facing; everything that touches the world lives here so they can share it.
 */
public final class MultiBlocks {

	/**
	 * Removing one cell removes the rest, and each of those removals runs this again. The first one through
	 * takes the whole structure down, so the rest have nothing left to do.
	 */
	private static final ThreadLocal<Boolean> DISMANTLING = ThreadLocal.withInitial(() -> false);

	private MultiBlocks() {
	}

	/**
	 * Where a cell sits relative to the origin. Cells are authored for the block facing north and rotate the
	 * same way {@link BlockShapeUtils#orientToFacing} rotates shapes, so a cell and the geometry inside it
	 * always agree.
	 */
	public static BlockPos offset(BlockPos origin, int[] cell, @Nullable Direction facing) {
		int x = cell[0], y = cell[1], z = cell[2];
		return switch (facing == null ? Direction.NORTH : facing) {
			case SOUTH -> origin.offset(-x, y, -z);
			case WEST -> origin.offset(z, y, -x);
			case EAST -> origin.offset(-z, y, x);
			default -> origin.offset(x, y, z);
		};
	}

	/** The position of the origin cell, given any cell of the structure and where it sits. */
	public static BlockPos originOf(BlockPos pos, int[] cell, @Nullable Direction facing) {
		return pos.subtract(offset(BlockPos.ZERO, cell, facing));
	}

	/** Whether every cell other than the one being placed is free, so the structure can be built whole. */
	public static boolean hasRoom(BlockPlaceContext ctx, Block.MultiBlockInformation information, @Nullable Direction facing) {
		Level level = ctx.getLevel();
		BlockPos origin = ctx.getClickedPos();

		for (String cell : MultiBlockVariants.cells(information)) {
			int[] parsed = MultiBlockVariants.parse(cell);
			if (parsed == null || isOrigin(parsed)) continue;

			BlockPos pos = offset(origin, parsed, facing);
			if (pos.getY() < level.getMinY() || pos.getY() > level.getMaxY()) return false;
			if (!level.getBlockState(pos).canBeReplaced(ctx)) return false;
		}
		return true;
	}

	/** Builds the rest of the structure around a freshly placed origin cell. */
	public static void placeParts(Level level, BlockPos origin, BlockState originState, ListProperty part,
	                              Block.MultiBlockInformation information, @Nullable Direction facing) {
		for (String cell : MultiBlockVariants.cells(information)) {
			int[] parsed = MultiBlockVariants.parse(cell);
			if (parsed == null || isOrigin(parsed) || !part.getPossibleValues().contains(cell)) continue;

			level.setBlock(offset(origin, parsed, facing), originState.setValue(part, cell), net.minecraft.world.level.block.Block.UPDATE_ALL);
		}
	}

	/**
	 * Takes down the cells around one that was just removed. The removed cell drops for itself, so the rest
	 * go quietly and the structure yields exactly one item however it was broken.
	 */
	public static void dismantle(Level level, BlockPos pos, BlockState state, ListProperty part,
	                             Block.MultiBlockInformation information, @Nullable Direction facing) {
		if (DISMANTLING.get()) return;
		if (!state.hasProperty(part)) return;

		int[] broken = MultiBlockVariants.parse(state.getValue(part));
		if (broken == null) return;

		DISMANTLING.set(true);
		try {
			BlockPos origin = originOf(pos, broken, facing);

			for (String cell : MultiBlockVariants.cells(information)) {
				int[] parsed = MultiBlockVariants.parse(cell);
				if (parsed == null) continue;

				BlockPos cellPos = offset(origin, parsed, facing);
				if (cellPos.equals(pos)) continue;

				BlockState cellState = level.getBlockState(cellPos);
				if (!cellState.is(state.getBlock()) || !cellState.hasProperty(part)) continue;
				if (!cellState.getValue(part).equals(cell)) continue;

				level.levelEvent(null, 2001, cellPos, net.minecraft.world.level.block.Block.getId(cellState));
				level.removeBlock(cellPos, false);
			}
		} finally {
			DISMANTLING.set(false);
		}
	}

	private static boolean isOrigin(int[] cell) {
		return cell[0] == 0 && cell[1] == 0 && cell[2] == 0;
	}
}

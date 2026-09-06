package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.world.Portal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds the empty rectangle inside a portal frame, the way vanilla does for obsidian.
 *
 * <p>Vanilla's {@code PortalShape} cannot be reused: its frame test is a hardcoded predicate for
 * obsidian, and the entry points that take a predicate take one over the shape rather than over the
 * frame blocks. The algorithm is the same one — walk out along the axis to the frame, then up — just
 * against the block a definition names.
 */
public final class PortalFrame {

	/** The interior of a found frame, and which way it faces. */
	public record Found(List<BlockPos> interior, Direction.Axis axis) {
	}

	private PortalFrame() {
	}

	/**
	 * Looks for a frame around {@code inside}, trying both horizontal axes.
	 *
	 * @param inside a position within the empty area, typically the block above the one that was lit
	 * @return the interior positions to fill, or null when no valid frame surrounds the position
	 */
	public static @Nullable Found find(LevelAccessor level, BlockPos inside, Portal portal) {
		Block frame = BuiltInRegistries.BLOCK.getValue(portal.frame);
		if (frame == null) return null;

		Found onX = findOnAxis(level, inside, portal, frame, Direction.Axis.X);
		return onX != null ? onX : findOnAxis(level, inside, portal, frame, Direction.Axis.Z);
	}

	private static @Nullable Found findOnAxis(LevelAccessor level, BlockPos inside, Portal portal,
	                                          Block frame, Direction.Axis axis) {
		if (!isEmpty(level, inside)) return null;

		Direction negative = axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH;
		Direction positive = negative.getOpposite();

		// Walk out to the frame on both sides, then down to the floor of the opening.
		int left = distanceToFrame(level, inside, negative, frame, portal.maxWidth());
		int right = distanceToFrame(level, inside, positive, frame, portal.maxWidth());
		if (left < 0 || right < 0) return null;

		int width = left + right + 1;
		if (width < portal.minWidth() || width > portal.maxWidth()) return null;

		BlockPos origin = inside.relative(negative, left);
		int down = distanceToFrame(level, origin, Direction.DOWN, frame, portal.maxHeight());
		if (down < 0) return null;
		BlockPos bottomLeft = origin.below(down);

		int height = openingHeight(level, bottomLeft, positive, width, frame, portal);
		if (height < portal.minHeight() || height > portal.maxHeight()) return null;

		List<BlockPos> interior = new ArrayList<>(width * height);
		for (int dy = 0; dy < height; dy++) {
			for (int dx = 0; dx < width; dx++) {
				BlockPos at = bottomLeft.relative(positive, dx).above(dy);
				if (!isEmpty(level, at)) return null;
				interior.add(at);
			}
		}

		// The bottom sits on frame already; the sides and top have to close it in.
		if (!isFramed(level, bottomLeft, positive, width, height, frame)) return null;

		return new Found(interior, axis);
	}

	/**
	 * How many empty blocks lie between {@code from} and the frame in {@code direction}, or -1 when the
	 * frame is not reached within {@code limit}.
	 */
	private static int distanceToFrame(LevelAccessor level, BlockPos from, Direction direction, Block frame, int limit) {
		for (int distance = 0; distance <= limit; distance++) {
			BlockPos at = from.relative(direction, distance + 1);
			if (is(level, at, frame)) return distance;
			if (!isEmpty(level, at)) return -1;
		}
		return -1;
	}

	/** How tall the opening is above {@code bottomLeft}, stopping at the first row that is not empty. */
	private static int openingHeight(LevelAccessor level, BlockPos bottomLeft, Direction along, int width,
	                                 Block frame, Portal portal) {
		for (int height = 0; height < portal.maxHeight(); height++) {
			for (int dx = 0; dx < width; dx++) {
				BlockPos at = bottomLeft.relative(along, dx).above(height);
				if (!isEmpty(level, at)) return height;
			}
		}
		return portal.maxHeight();
	}

	/** Whether the sides, top and bottom of the opening are all the frame block. */
	private static boolean isFramed(LevelAccessor level, BlockPos bottomLeft, Direction along, int width,
	                                int height, Block frame) {
		Direction back = along.getOpposite();

		for (int dy = 0; dy < height; dy++) {
			if (!is(level, bottomLeft.above(dy).relative(back), frame)) return false;
			if (!is(level, bottomLeft.above(dy).relative(along, width), frame)) return false;
		}
		for (int dx = 0; dx < width; dx++) {
			if (!is(level, bottomLeft.relative(along, dx).below(), frame)) return false;
			if (!is(level, bottomLeft.relative(along, dx).above(height), frame)) return false;
		}
		return true;
	}

	private static boolean is(LevelAccessor level, BlockPos pos, Block block) {
		return level.getBlockState(pos).is(block);
	}

	/** Air and fire both count: fire is what a flint and steel leaves behind on the way in. */
	private static boolean isEmpty(LevelAccessor level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.isAir() || state.is(Blocks.FIRE);
	}
}

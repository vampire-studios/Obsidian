package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.world.Pattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Matches a {@link Pattern}'s shape against the world.
 *
 * <p>The shape is authored once, facing north, and tried at each of the four horizontal rotations. It is
 * anchored by the block the player clicked: every cell the click could correspond to is tried, so a
 * player can activate an altar by clicking any part of it rather than one magic block — unless the
 * definition names an {@code anchor}, in which case only that cell is tried.
 */
public final class PatternMatcher {

	/** A cell that matches anything at all. */
	private static final char WILDCARD = ' ';

	/** A cell that must be empty. */
	private static final char EMPTY = '_';

	/** A matched shape: every position it occupies, and which way round it was found. */
	public record Match(List<BlockPos> positions, Direction facing) {
	}

	private PatternMatcher() {
	}

	/**
	 * Looks for the shape around a clicked position.
	 *
	 * @return the match, or null when the shape is not built there
	 */
	public static @Nullable Match find(LevelAccessor level, BlockPos clicked, Pattern pattern) {
		if (pattern.isEmpty()) return null;

		List<Direction> facings = pattern.rotate
				? List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
				: List.of(Direction.NORTH);

		for (Direction facing : facings) {
			for (Cell candidate : anchorCells(pattern)) {
				// The clicked block is this cell, so the shape's origin is that far back from it.
				BlockPos origin = clicked.subtract(rotate(candidate.x(), candidate.y(), candidate.z(), facing));

				Match match = matchAt(level, origin, pattern, facing);
				if (match != null) return match;
			}
		}
		return null;
	}

	/** Which cells the clicked block is allowed to be: the named anchor, or anything not a wildcard. */
	private static List<Cell> anchorCells(Pattern pattern) {
		List<Cell> cells = new ArrayList<>();
		Character anchor = pattern.anchor == null || pattern.anchor.isEmpty() ? null : pattern.anchor.charAt(0);

		forEachCell(pattern, (x, y, z, key) -> {
			if (key == WILDCARD) return;
			if (anchor != null && key != anchor) return;
			cells.add(new Cell(x, y, z));
		});
		return cells;
	}

	/** Tests the whole shape with its origin at {@code origin}. */
	private static @Nullable Match matchAt(LevelAccessor level, BlockPos origin, Pattern pattern, Direction facing) {
		List<BlockPos> positions = new ArrayList<>();
		boolean[] failed = {false};

		forEachCell(pattern, (x, y, z, key) -> {
			if (failed[0] || key == WILDCARD) return;

			BlockPos at = origin.offset(rotate(x, y, z, facing));
			if (!matches(level, at, key, pattern)) {
				failed[0] = true;
				return;
			}
			// Cells that must be empty are part of the shape but not part of what it is made of.
			if (key != EMPTY) positions.add(at);
		});

		return failed[0] ? null : new Match(positions, facing);
	}

	private static boolean matches(LevelAccessor level, BlockPos pos, char key, Pattern pattern) {
		BlockState state = level.getBlockState(pos);
		if (key == EMPTY) return state.isAir();

		String declared = pattern.keys == null ? null : pattern.keys.get(String.valueOf(key));
		if (declared == null) {
			// A key nothing declares can never match; warn once per lookup rather than fail the pack.
			Obsidian.LOGGER.warn("Pattern {} uses key '{}' with no entry in keys.", pattern.id, key);
			return false;
		}

		if (declared.startsWith("#")) {
			Identifier tagId = Identifier.tryParse(declared.substring(1));
			if (tagId == null) return false;
			return state.is(TagKey.create(Registries.BLOCK, tagId));
		}

		Identifier blockId = Identifier.tryParse(declared);
		if (blockId == null) return false;

		Block block = BuiltInRegistries.BLOCK.getValue(blockId);
		return block != null && state.is(block);
	}

	/**
	 * Turns a cell offset to face {@code facing}. The shape is authored facing north, so north is the
	 * identity and the rest turn clockwise around the origin.
	 */
	private static net.minecraft.core.Vec3i rotate(int x, int y, int z, Direction facing) {
		return switch (facing) {
			case EAST -> new net.minecraft.core.Vec3i(-z, y, x);
			case SOUTH -> new net.minecraft.core.Vec3i(-x, y, -z);
			case WEST -> new net.minecraft.core.Vec3i(z, y, -x);
			default -> new net.minecraft.core.Vec3i(x, y, z);
		};
	}

	/** Walks the shape, handing each cell its offset and its key. */
	private static void forEachCell(Pattern pattern, CellVisitor visitor) {
		List<List<String>> layers = pattern.pattern;
		for (int y = 0; y < layers.size(); y++) {
			List<String> rows = layers.get(y);
			if (rows == null) continue;

			for (int z = 0; z < rows.size(); z++) {
				String row = rows.get(z);
				if (row == null) continue;

				for (int x = 0; x < row.length(); x++) {
					visitor.visit(x, y, z, row.charAt(x));
				}
			}
		}
	}

	private record Cell(int x, int y, int z) {
	}

	private interface CellVisitor {
		void visit(int x, int y, int z, char key);
	}
}

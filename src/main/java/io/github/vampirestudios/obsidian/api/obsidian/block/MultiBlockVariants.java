package io.github.vampirestudios.obsidian.api.obsidian.block;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code part} block state property: which cell of a multi-block structure a block is.
 *
 * <p>A block that declares {@code multi_block} of more than one cell places the whole structure at once, as
 * one block repeated across the cells, each carrying its own {@code part} value. Cells are named
 * {@code "<x>_<y>_<z>"} in the block's own space — {@code 0_0_0} is the cell the player placed, X and Z grow
 * with the block's facing and Y grows upwards — so a two-wide, three-tall statue owns {@code 0_0_0} through
 * {@code 1_2_0}.
 */
public final class MultiBlockVariants {

	public static final String PROPERTY = "part";

	/** The cell that gets placed where the player clicked; every other cell is derived from it. */
	public static final String ORIGIN = name(0, 0, 0);

	private MultiBlockVariants() {
	}

	public static String name(int x, int y, int z) {
		return x + "_" + y + "_" + z;
	}

	/** The {@code x, y, z} of a cell name, or null when it is not one. */
	public static int @Nullable [] parse(String part) {
		String[] split = part.split("_");
		if (split.length != 3) return null;
		try {
			return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])};
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** The multi-block size this block declares, or null when it is a single block. */
	public static Block.@Nullable MultiBlockInformation information(Block block) {
		if (block == null || !supports(block)) return null;

		Block.MultiBlockInformation information = block.multi_block_information;
		if (information == null || information.cellCount() <= 1) return null;
		return information;
	}

	/** Every cell name of this block's structure, in {@code x, y, z} order. Empty when it is a single block. */
	public static List<String> declared(Block block) {
		Block.MultiBlockInformation information = information(block);
		return information == null ? List.of() : cells(information);
	}

	/**
	 * Whether the declared cells become a block state property. A structure of one cell is a plain block,
	 * and a property needs at least two values — see {@link PlacementVariants#needsProperty(List)}.
	 */
	public static boolean needsProperty(List<String> cells) {
		return cells.size() >= 2;
	}

	/** Every cell name of a structure of this size, in {@code x, y, z} order. */
	public static List<String> cells(Block.MultiBlockInformation information) {
		List<String> cells = new ArrayList<>(information.cellCount());
		for (int x = 0; x < information.width(); x++) {
			for (int y = 0; y < information.height(); y++) {
				for (int z = 0; z < information.depth(); z++) {
					cells.add(name(x, y, z));
				}
			}
		}
		return List.copyOf(cells);
	}

	/**
	 * Only the block implementations that build the structure read the property. Everything else — stairs,
	 * doors, plants, the block entity backed dyeable blocks — has placement logic of its own that a
	 * multi-block would have to fight, and a 6-way facing has no sensible cell rotation.
	 */
	private static boolean supports(Block block) {
		Block.BlockType type = block.getBlockType();
		if (type == Block.BlockType.HORIZONTAL_DIRECTIONAL) return true;
		if (type != Block.BlockType.BLOCK && type != Block.BlockType.WOOD) return false;
		return block.additional_information == null || !block.additional_information.dyable;
	}
}

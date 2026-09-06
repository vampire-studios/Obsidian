package io.github.vampirestudios.obsidian.api.obsidian.block;

import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code placement} block state property: which surface a block was placed against.
 *
 * <p>A block declares the variants it supports by giving them geometry ({@code information.placement_shapes})
 * or a model ({@code rendering.placement_models}); the union of the two is the property's value set, so the
 * shape lookup at runtime and the generated blockstate always agree on which values exist.
 *
 * <p>This is the presentation half of placement — which surfaces look different. {@link PlacementRules} is
 * the restriction half, and runs first: a surface it disallows never reaches the fallback below.
 */
public final class PlacementVariants {

	public static final String PROPERTY = "placement";

	public static final String FLOOR = "floor";
	public static final String WALL = "wall";
	public static final String CEILING = "ceiling";

	/** Declaration order, which is also the order values appear in the block state — the first is the default. */
	private static final List<String> ORDER = List.of(FLOOR, WALL, CEILING);

	private PlacementVariants() {
	}

	/**
	 * The variants this block declares, in canonical order. Empty when the block does not use placement, or
	 * when it is a kind of block that cannot: only the plain block implementation reads the property, and
	 * powerable, toggleable and dyeable blocks already drive their own blockstate.
	 */
	public static List<String> declared(Block block) {
		if (block == null || !supports(block)) return List.of();

		BlockInformation.PlacementShapes shapes = block.information != null ? block.information.placementShapes : null;
		var models = block.rendering != null ? block.rendering.placementModels : null;
		if (shapes == null && models == null) return List.of();

		List<String> declared = new ArrayList<>(ORDER.size());
		for (String placement : ORDER) {
			boolean hasShape = shapes != null && shapes.get(placement) != null;
			boolean hasModel = models != null && models.get(placement) != null;
			if (hasShape || hasModel) declared.add(placement);
		}
		return List.copyOf(declared);
	}

	/**
	 * The block implementations that read the property: the plain block, the horizontal-facing block and the
	 * directional block, along with everything built on them (powerable, toggleable, sittable, and the
	 * horizontal dyeable block). A dyeable plain block is not one of them — it is a block entity block of its
	 * own — and neither are the block types with placement logic of their own, such as stairs and doors.
	 */
	private static boolean supports(Block block) {
		Block.BlockType type = block.getBlockType();
		if (type == Block.BlockType.HORIZONTAL_DIRECTIONAL || type == Block.BlockType.DIRECTIONAL) return true;
		if (type != Block.BlockType.BLOCK && type != Block.BlockType.WOOD) return false;
		return block.additional_information == null || !block.additional_information.dyable;
	}

	/**
	 * Whether the declared variants become a block state property.
	 *
	 * <p>One variant is not a choice: every placement is that variant, so there is nothing for a state to
	 * record. The game refuses a property with fewer than two values outright, so a block declaring a
	 * single placement would fail to construct — and, because its registry holder is made before its
	 * states are, take the whole game down at registry freeze rather than just failing itself.
	 *
	 * <p>The blockstate generator asks the same question, so what is written and what the block actually
	 * has cannot drift apart.
	 */
	public static boolean needsProperty(List<String> variants) {
		return variants.size() >= 2;
	}

	/**
	 * The variant a block gets when placed against {@code clickedFace}: the top face puts it on the floor, the
	 * bottom face hangs it from the ceiling, and the sides mount it on a wall. Variants the block does not
	 * declare fall back to the nearest one it does, so a floor-only block still places from any face.
	 */
	public static @Nullable String forClickedFace(Direction clickedFace, List<String> declared) {
		if (declared.isEmpty()) return null;

		String wanted = switch (clickedFace) {
			case UP -> FLOOR;
			case DOWN -> CEILING;
			default -> WALL;
		};
		if (declared.contains(wanted)) return wanted;

		for (String fallback : ORDER) {
			if (declared.contains(fallback)) return fallback;
		}
		return null;
	}
}

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TextureAndModelInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.BlockInformation;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/** Shape helpers shared by the block implementations, which each extend a different vanilla block. */
public final class BlockShapeUtils {

	private BlockShapeUtils() {
	}

	/**
	 * Rotates a shape authored for the default (north) facing to match a block's horizontal facing.
	 * Shorthand shapes are declared once in model space, so without this an asymmetric block keeps the
	 * same collision box in all four orientations. Coordinates outside 0-1 are mirrored about the block
	 * centre like any other, so oversized shapes rotate correctly too.
	 */
	public static VoxelShape orientToFacing(VoxelShape shape, @Nullable Direction facing) {
		if (facing == null || facing == Direction.NORTH || !facing.getAxis().isHorizontal()) return shape;

		VoxelShape[] rotated = {Shapes.empty()};
		shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> rotated[0] = Shapes.or(rotated[0], switch (facing) {
			case SOUTH -> Shapes.box(1 - maxX, minY, 1 - maxZ, 1 - minX, maxY, 1 - minZ);
			case WEST -> Shapes.box(minZ, minY, 1 - maxX, maxZ, maxY, 1 - minX);
			case EAST -> Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX);
			default -> Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
		}));
		return rotated[0];
	}

	/**
	 * Resolves a block's shape, in priority order: the specific bounding box (per-facing forms first, then
	 * its model-space fallbacks), then the {@code shapes} multi-box shorthand, then the {@code shape}
	 * single-box shorthand. Model-space forms are rotated to {@code facing}; per-facing forms are not,
	 * since they are already authored for their direction. Returns null when nothing is declared.
	 */
	public static @Nullable VoxelShape resolve(BlockInformation.@Nullable BoundingBox specific,
											   float @Nullable [] shorthand,
											   float @Nullable [][] shorthands,
											   @Nullable Direction facing) {
		if (specific != null && specific.collisionType != null) {
			switch (specific.collisionType) {
				case FULL_BLOCK -> {
					return Shapes.block();
				}
				case BOTTOM_SLAB -> {
					return Block.box(0, 0, 0, 16, 8, 16);
				}
				case TOP_SLAB -> {
					return Block.box(0, 8, 0, 16, 16, 16);
				}
				case NONE -> {
					return Shapes.empty();
				}
				case CUSTOM -> {
					float[][] facingShapes = directionalShapes(specific, facing);
					if (facingShapes != null) return boxes(facingShapes);

					float[] facingShape = directionalShape(specific, facing);
					if (facingShape != null) return box(facingShape);
				}
			}
		}

		if (shorthands != null) return orientToFacing(boxes(shorthands), facing);
		if (shorthand != null) return orientToFacing(box(shorthand), facing);
		return null;
	}

	/**
	 * Resolves the shape of one variant — a placement, or a cell of a multi-block — falling back per-field to
	 * the block-wide declaration: a variant that only gives geometry keeps the block's collision type, and a
	 * variant that only gives a collision type applies it to the block's geometry. Returns null when the
	 * variant declares nothing, leaving the caller on the block-wide shape.
	 */
	public static @Nullable VoxelShape resolveVariant(BlockInformation information, BlockInformation.@Nullable ShapeSet variant,
													  boolean outline, @Nullable Direction facing) {
		if (information == null || variant == null) return null;

		BlockInformation.BoundingBox specific = outline
				? (variant.outlineShape != null ? variant.outlineShape : information.outlineShape)
				: (variant.collisionShape != null ? variant.collisionShape : information.collisionShape);

		return resolve(specific,
				variant.shape != null ? variant.shape : information.shape,
				variant.shapes != null ? variant.shapes : information.shapes,
				facing);
	}

	/** The shape to occlude with when nothing was declared — never assume solidity for a custom model. */
	public static VoxelShape occlusionFallback(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		return rendersAsPlainCube(block) ? Shapes.block() : Shapes.empty();
	}

	/** Only vertical facings have overrides; horizontal ones are produced by rotating the model-space shape. */
	private static float @Nullable [][] directionalShapes(BlockInformation.BoundingBox specific, @Nullable Direction facing) {
		if (facing == Direction.UP) return specific.up_shapes;
		if (facing == Direction.DOWN) return specific.down_shapes;
		return null;
	}

	private static float @Nullable [] directionalShape(BlockInformation.BoundingBox specific, @Nullable Direction facing) {
		if (facing == Direction.UP) return specific.up_shape;
		if (facing == Direction.DOWN) return specific.down_shape;
		return null;
	}

	/** One box in model space, 0–16 per axis. */
	public static VoxelShape box(float[] b) {
		return Block.box(b[0], b[1], b[2], b[3], b[4], b[5]);
	}

	/** Several boxes in model space combined into one shape. */
	public static VoxelShape boxes(float[][] boxes) {
		VoxelShape result = Shapes.empty();
		for (float[] b : boxes) result = Shapes.or(result, box(b));
		return result;
	}

	/**
	 * The shape a growing block has at {@code age}, from a per-age list of box sets. A list shorter than
	 * the block's age range keeps its last entry for every age past it, so declaring one shape per visible
	 * stage is enough and a block that grows further does not fall back to a full cube.
	 *
	 * @return null when nothing was declared, leaving the caller on its own default
	 */
	public static @Nullable VoxelShape shapeForAge(float @Nullable [][][] shapesByAge, int age) {
		if (shapesByAge == null || shapesByAge.length == 0) return null;
		float[][] declared = shapesByAge[Math.min(Math.max(age, 0), shapesByAge.length - 1)];
		return declared == null || declared.length == 0 ? null : boxes(declared);
	}

	/**
	 * Whether a block's model is a plain full cube. Used to decide whether a block with no declared shape
	 * may claim full-cube occlusion; assuming solidity for a custom model culls the faces of every
	 * neighbouring block and makes the world look transparent around it.
	 */
	public static boolean rendersAsPlainCube(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		if (block == null || block.rendering == null) return true;
		if (block.rendering.hasBlockModelString() || block.rendering.hasLegacyModelString()) return false;

		TextureAndModelInformation model = block.rendering.getBlockModel();
		if (model == null) model = block.rendering.getModel();
		if (model == null || model.parent == null) return true;

		Identifier parent = model.parent;
		return parent.getNamespace().equals("minecraft") && parent.getPath().startsWith("block/cube");
	}
}

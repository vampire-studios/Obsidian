package io.github.vampirestudios.obsidian.api.obsidian.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code behaviour.placement}: which surfaces a block is allowed to be placed against.
 *
 * <p>This is the restriction half of placement. Its counterpart {@link PlacementVariants} is the
 * presentation half — which surfaces get their own shape or model — and the two are independent. A block
 * can look the same on every surface but only be placeable on the ceiling, or have three distinct models
 * and go anywhere. When both are declared, a surface has to be allowed here to be reachable at all.
 *
 * <p>Declaring the section at all opts the block in; a block without one goes anywhere, as before.
 */
public final class PlacementRules {

	private static final Logger LOGGER = LogManager.getLogger();

	private final List<String> allowed;

	private PlacementRules(List<String> allowed) {
		this.allowed = allowed;
	}

	/**
	 * The rules a block declares, or null when it declares none and everything is allowed. A section that
	 * allows nothing at all is a mistake that would make the block unplaceable, so it is warned about and
	 * treated as no restriction.
	 */
	public static @Nullable PlacementRules of(Block block) {
		if (block == null || block.behaviour == null || block.behaviour.placement == null) return null;

		Block.Behaviour.Placement declared = block.behaviour.placement;
		List<String> allowed = new ArrayList<>(3);
		if (declared.floor) allowed.add(PlacementVariants.FLOOR);
		if (declared.wall) allowed.add(PlacementVariants.WALL);
		if (declared.ceiling) allowed.add(PlacementVariants.CEILING);

		if (allowed.isEmpty()) {
			LOGGER.warn("Block {} declares behaviour.placement with no surface enabled, which would make it "
					+ "unplaceable; ignoring it.", block.information != null ? block.information.id : "?");
			return null;
		}
		return new PlacementRules(List.copyOf(allowed));
	}

	public boolean allows(String surface) {
		return allowed.contains(surface);
	}

	/** The surface a click on {@code clickedFace} would place against, allowed or not. */
	public static String surfaceFor(Direction clickedFace) {
		return switch (clickedFace) {
			case UP -> PlacementVariants.FLOOR;
			case DOWN -> PlacementVariants.CEILING;
			default -> PlacementVariants.WALL;
		};
	}

	/**
	 * The only surface this block can sit on, or null when it allows more than one.
	 *
	 * <p>A block that allows exactly one surface is the case where support can be checked without the block
	 * recording which surface it was placed against — a ceiling-only block is always holding onto the block
	 * above it. With two or more allowed, that has to come from the {@code placement} state property instead.
	 */
	public @Nullable String onlySurface() {
		return allowed.size() == 1 ? allowed.getFirst() : null;
	}

	/**
	 * Whether a block sitting on {@code surface} still has something to hold onto.
	 *
	 * <p>{@code facing} is the direction a wall-mounted block faces, which is away from its wall. Without one
	 * a wall block's support cannot be located, so it is left alone rather than guessed at.
	 */
	public static boolean supported(String surface, LevelReader level, BlockPos pos, @Nullable Direction facing) {
		Direction towardsSupport = switch (surface) {
			case PlacementVariants.FLOOR -> Direction.DOWN;
			case PlacementVariants.CEILING -> Direction.UP;
			case PlacementVariants.WALL -> facing == null ? null : facing.getOpposite();
			default -> null;
		};
		if (towardsSupport == null) return true;

		BlockPos supportPos = pos.relative(towardsSupport);
		return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, towardsSupport.getOpposite());
	}
}

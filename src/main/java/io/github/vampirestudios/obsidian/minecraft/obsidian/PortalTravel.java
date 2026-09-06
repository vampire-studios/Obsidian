package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.world.Portal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/**
 * Where a portal leads, and building the portal on the far side so the traveller can come back.
 *
 * <p>The mapping is deterministic: coordinates are scaled by the definition's {@code scale} going out
 * and by its inverse coming back, so returning through the far portal lands near where you left. When
 * nothing is standing at the mapped position, a frame is built there rather than searching the world
 * for one — a search is what makes vanilla's nether linking expensive and surprising.
 */
public final class PortalTravel {

	/** How far up and down the mapped position is searched for somewhere to stand. */
	private static final int VERTICAL_SEARCH = 16;

	/** Blocks of clearance a built frame needs above its floor. */
	private static final int PORTAL_HEADROOM = 6;

	private PortalTravel() {
	}

	public static @Nullable TeleportTransition destinationFor(ServerLevel from, Entity entity, BlockPos pos, Portal portal) {
		if (portal.dimension == null) return null;

		ResourceKey<Level> here = from.dimension();
		ResourceKey<Level> outbound = dimensionKey(portal.dimension);
		ResourceKey<Level> inbound = dimensionKey(portal.returnDimension);

		// Standing in the target dimension means this portal is the way back.
		boolean returning = here.equals(outbound);
		ResourceKey<Level> targetKey = returning ? inbound : outbound;

		ServerLevel target = from.getServer().getLevel(targetKey);
		if (target == null) {
			Obsidian.LOGGER.warn("Portal {} leads to dimension {}, which does not exist.",
					portal.id, targetKey.identifier());
			return null;
		}

		double scale = portal.scale <= 0.0 ? 1.0 : portal.scale;
		double factor = returning ? 1.0 / scale : scale;
		BlockPos mapped = new BlockPos(
				(int) Math.round(pos.getX() * factor),
				pos.getY(),
				(int) Math.round(pos.getZ() * factor));

		BlockPos landing = findOrBuild(target, mapped, portal);
		return new TeleportTransition(target, Vec3.atBottomCenterOf(landing), Vec3.ZERO,
				entity.getYRot(), entity.getXRot(), Set.of(), TeleportTransition.PLAY_PORTAL_SOUND);
	}

	private static ResourceKey<Level> dimensionKey(Identifier id) {
		return ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, id);
	}

    /**
     * The position to arrive at: an existing portal of this kind near the mapped coordinates, or a
     * freshly built one when there is none.
     */
	private static BlockPos findOrBuild(ServerLevel target, BlockPos mapped, Portal portal) {
		Block portalBlock = BuiltInRegistries.BLOCK.getValue(portal.block);

		BlockPos existing = findNearby(target, mapped, portalBlock);
		if (existing != null) return existing;

		BlockPos ground = groundAt(target, mapped);
		build(target, ground, portal, portalBlock);
		return ground.above();
	}

	/** Looks for a portal block of this kind in a small column around the mapped position. */
	private static @Nullable BlockPos findNearby(ServerLevel level, BlockPos mapped, @Nullable Block portalBlock) {
		if (portalBlock == null) return null;

		for (int dy = 0; dy <= VERTICAL_SEARCH; dy++) {
			for (int sign : new int[]{1, -1}) {
				BlockPos at = mapped.above(dy * sign);
				if (!level.isInWorldBounds(at)) continue;
				if (level.getBlockState(at).is(portalBlock)) return at;
			}
		}
		return null;
	}

	/** A solid spot to build on, from the world's surface at the mapped column. */
	private static BlockPos groundAt(ServerLevel level, BlockPos mapped) {
		int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mapped.getX(), mapped.getZ());
		// One below the surface, so the frame's floor sits in the ground rather than on top of it.
		int y = Math.clamp(surface - 1, level.getMinY() + 1, level.getMaxY() - PORTAL_HEADROOM);
		return new BlockPos(mapped.getX(), y, mapped.getZ());
	}

	/**
	 * Builds a minimum-size frame standing on {@code ground} and fills it, so the traveller arrives
	 * inside a working portal rather than beside a hole.
	 */
	private static void build(ServerLevel level, BlockPos ground, Portal portal, @Nullable Block portalBlock) {
		Block frame = BuiltInRegistries.BLOCK.getValue(portal.frame);
		if (frame == null || portalBlock == null) return;

		int width = Math.clamp(2, portal.minWidth(), portal.maxWidth());
		int height = Math.clamp(3, portal.minHeight(), portal.maxHeight());

		BlockState frameState = frame.defaultBlockState();
		BlockState portalState = PortalLogic.fillState(portalBlock, Direction.Axis.X);

		// Frame: floor and ceiling run one wider than the opening, the sides one taller.
		for (int dx = -1; dx <= width; dx++) {
			level.setBlockAndUpdate(ground.offset(dx, 0, 0), frameState);
			level.setBlockAndUpdate(ground.offset(dx, height + 1, 0), frameState);
		}
		for (int dy = 1; dy <= height; dy++) {
			level.setBlockAndUpdate(ground.offset(-1, dy, 0), frameState);
			level.setBlockAndUpdate(ground.offset(width, dy, 0), frameState);
		}

		for (int dx = 0; dx < width; dx++) {
			for (int dy = 1; dy <= height; dy++) {
				level.setBlockAndUpdate(ground.offset(dx, dy, 0), portalState);
			}
		}
	}
}

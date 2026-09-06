package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.world.Portal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.TeleportTransition;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ties a {@code world/portal} definition to the block a pack chose to fill its frame with.
 *
 * <p>The portal is not a block class of its own. Vanilla's {@code setAsInsidePortal} takes the
 * {@link net.minecraft.world.level.block.Portal} interface rather than the block, so the handler can be
 * a small object per definition and the block stays an ordinary Obsidian block — model, shape, light,
 * sounds and collision all declared in {@code block/} like anything else.
 */
public final class PortalLogic {

	/**
	 * The vanilla axis property, if the pack's block declares it. A portal block does not have to:
	 * one with a symmetrical model looks the same either way round.
	 */
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

	private static final Map<Block, Handler> BY_BLOCK = new ConcurrentHashMap<>();

	private PortalLogic() {
	}

	/** Binds a definition to its block. Called once per portal, as the pack loads. */
	public static void bind(Block block, Portal portal) {
		BY_BLOCK.put(block, new Handler(portal));
	}

	public static @Nullable Portal portalOf(BlockState state) {
		Handler handler = BY_BLOCK.get(state.getBlock());
		return handler == null ? null : handler.portal;
	}

	public static boolean isPortal(BlockState state) {
		return BY_BLOCK.containsKey(state.getBlock());
	}

	/**
	 * Hands the entity to vanilla's portal process when it is standing in a portal block.
	 *
	 * <p>Called from every block's {@code entityInside}; a block that is not a portal costs one map
	 * lookup and nothing else.
	 */
	public static void tryEnter(BlockState state, BlockPos pos, Entity entity) {
		Handler handler = BY_BLOCK.get(state.getBlock());
		if (handler == null) return;
		if (!entity.canUsePortal(false)) return;

		entity.setAsInsidePortal(handler, pos);
	}

	/** The state to fill a frame with, carrying the axis when the block has somewhere to put it. */
	public static BlockState fillState(Block block, Direction.Axis axis) {
		BlockState state = block.defaultBlockState();
		return state.hasProperty(AXIS) ? state.setValue(AXIS, axis) : state;
	}

	/** One definition's answer to vanilla's portal questions. */
	private record Handler(Portal portal) implements net.minecraft.world.level.block.Portal {

		@Override
		public @Nullable TeleportTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
			return PortalTravel.destinationFor(level, entity, pos, portal);
		}

		@Override
		public int getPortalTransitionTime(ServerLevel level, Entity entity) {
			return Math.max(0, portal.delay);
		}

		@Override
		public Transition getLocalTransition() {
			return portal.confusion ? Transition.CONFUSION : Transition.NONE;
		}
	}
}

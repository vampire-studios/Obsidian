package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.ISeatProvider;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.registry.OBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.List;

/** Shared, cushion-style lifecycle for every Obsidian and imported-furniture seat. */
public final class SeatLogic {

	public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
	public static final Vector3f DEFAULT_OFFSET = new Vector3f(0.0F, 0.4F, 0.0F);

	/** Seat offsets beyond this distance are unsupported because anchor cleanup must stay bounded. */
	private static final double ANCHOR_SEARCH_RADIUS = 16.0;

	private SeatLogic() {
	}

	/** Explicit seats, or one default seat for the legacy {@code sittable} flag. */
	public static @Nullable List<Block.Behaviour.Seat> seatsOf(@Nullable Block block) {
		if (block == null) return null;
		if (block.behaviour != null && block.behaviour.seat != null && !block.behaviour.seat.isEmpty()) {
			return block.behaviour.seat;
		}
		return isLegacySittable(block) ? List.of(new Block.Behaviour.Seat()) : null;
	}

	public static boolean isLegacySittable(@Nullable Block block) {
		return block != null && block.additional_information != null && block.additional_information.sittable;
	}

	/** Seats the player on the free declaration nearest the click. */
	public static InteractionResult sit(Level level, BlockPos clickedPos, BlockState clickedState, Player player,
	                                    @Nullable BlockHitResult hit) {
		SeatContext context = contextAt(level, clickedPos, clickedState);
		if (context == null || context.seats().isEmpty()) return InteractionResult.PASS;
		if (player.isPassenger() || player.isShiftKeyDown()) return InteractionResult.PASS;
		if (level.isClientSide()) return InteractionResult.SUCCESS;

		SeatCandidate candidate = pickSeat(level, context, player, hit);
		if (candidate == null) return InteractionResult.PASS;

		SeatEntity entity = findSeat(level, context.anchor(), candidate.index(), candidate.position());
		boolean spawned = false;
		if (entity == null) {
			entity = Obsidian.SEAT.create(level, EntitySpawnReason.TRIGGERED);
			if (entity == null) return InteractionResult.PASS;
			entity.configure(context.anchor(), candidate.index(), candidate.seat());
			entity.setPos(candidate.position());
			entity.setYRot(candidate.yaw());
			// Mounting an entity the level refused would leave the client riding something it never
			// received, which desynchronises its position and rotation until it relogs.
			boolean added = level.addFreshEntity(entity);
			if (!added) return InteractionResult.PASS;
			spawned = true;
		} else {
			entity.configure(context.anchor(), candidate.index(), candidate.seat());
			entity.setPos(candidate.position());
			entity.setYRot(candidate.yaw());
		}

		if (entity.isRemoved()) return InteractionResult.PASS;

		boolean riding = player.startRiding(entity);
		if (!riding) {
			if (spawned) entity.discard();
			return InteractionResult.PASS;
		}

		updateOccupied(level, context.anchor(), null);
		return InteractionResult.SUCCESS;
	}

	/** Removes every active occupant belonging to an anchor. */
	public static void clearSeats(Level level, BlockPos pos) {
		if (level.isClientSide()) return;
		for (SeatEntity seat : seatsNearAnchor(level, pos)) {
			if (!pos.equals(seat.getAnchor())) continue;
			seat.ejectPassengers();
			seat.discard();
		}
		setOccupied(level, pos, false);
	}

	/** Called by a seat entity to follow state rotations and reject stale anchors. */
	public static boolean refreshSeat(SeatEntity entity) {
		BlockPos anchor = entity.getAnchor();
		if (anchor == null) return false;

		SeatContext context = contextAt(entity.level(), anchor, entity.level().getBlockState(anchor));
		if (context == null || !anchor.equals(context.anchor())) return false;
		if (entity.getSeatIndex() < 0 || entity.getSeatIndex() >= context.seats().size()) return false;

		Block.Behaviour.Seat seat = context.seats().get(entity.getSeatIndex());
		Vec3 position = seatPosition(anchor, context.facing(), seat);
		entity.configure(anchor, entity.getSeatIndex(), seat);
		entity.setYRot(yawOf(context.facing(), seat));
		if (entity.position().distanceToSqr(position) > 1.0E-6) entity.setPos(position);

		LivingEntity passenger = entity.getFirstPassenger() instanceof LivingEntity living ? living : null;
		return passenger == null || isSpaceClear(entity.level(), anchor, position, entity.getYRot(), seat, passenger);
	}

	public static void updateOccupied(Level level, BlockPos anchor, @Nullable SeatEntity excluded) {
		boolean occupied = false;
		for (SeatEntity seat : seatsNearAnchor(level, anchor)) {
			if (seat == excluded || !anchor.equals(seat.getAnchor())) continue;
			if (!seat.getPassengers().isEmpty()) {
				occupied = true;
				break;
			}
		}
		setOccupied(level, anchor, occupied);
	}

	public static void setOccupied(Level level, BlockPos pos, boolean occupied) {
		BlockState state = level.getBlockState(pos);
		if (!state.hasProperty(OCCUPIED) || state.getValue(OCCUPIED) == occupied) return;
		level.setBlock(pos, state.setValue(OCCUPIED, occupied), net.minecraft.world.level.block.Block.UPDATE_ALL);
	}

	/** Finds a safe explicit or adjacent dismount position. */
	public static @Nullable Vec3 dismountLocation(SeatEntity entity, LivingEntity passenger) {
		BlockPos anchor = entity.getAnchor();
		if (anchor == null) return null;

		SeatContext context = contextAt(entity.level(), anchor, entity.level().getBlockState(anchor));
		Block.Behaviour.Seat seat = context != null && entity.getSeatIndex() >= 0
				&& entity.getSeatIndex() < context.seats().size() ? context.seats().get(entity.getSeatIndex()) : null;
		Direction facing = context != null ? context.facing() : Direction.fromYRot(entity.getYRot());

		if (seat != null && seat.dismountOffset != null) {
			Vec3 offset = rotateToFacing(seat.dismountOffset, facing);
			Vec3 explicit = new Vec3(anchor.getX() + 0.5 + offset.x, anchor.getY() + offset.y,
					anchor.getZ() + 0.5 + offset.z);
			Vec3 safe = safeDismount(entity.level(), passenger, explicit);
			if (safe != null) return safe;
		}

		for (int[] offset : DismountHelper.offsetsForDirection(Direction.fromYRot(entity.getYRot()))) {
			BlockPos candidate = anchor.offset(offset[0], 0, offset[1]);
			for (int y = 0; y <= 1; y++) {
				Vec3 safe = safeDismount(entity.level(), passenger, Vec3.atBottomCenterOf(candidate.above(y)));
				if (safe != null) return safe;
			}
		}
		return null;
	}

	private static @Nullable Vec3 safeDismount(Level level, LivingEntity passenger, Vec3 position) {
		for (Pose pose : passenger.getDismountPoses()) {
			if (DismountHelper.canDismountTo(level, position, passenger, pose)) return position;
		}
		return null;
	}

	public static @Nullable Direction facingOf(BlockState state) {
		if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		}
		if (state.hasProperty(BlockStateProperties.FACING)) {
			Direction facing = state.getValue(BlockStateProperties.FACING);
			return facing.getAxis().isHorizontal() ? facing : null;
		}
		return null;
	}

	public static Vec3 seatPosition(BlockPos pos, @Nullable Direction facing, Block.Behaviour.Seat seat) {
		Vector3f offset = seat.offset != null ? seat.offset : DEFAULT_OFFSET;
		Vec3 rotated = rotateToFacing(offset, facing);
		return new Vec3(pos.getX() + 0.5 + rotated.x, pos.getY() + rotated.y,
				pos.getZ() + 0.5 + rotated.z);
	}

	private static Vec3 rotateToFacing(Vector3f offset, @Nullable Direction facing) {
		if (facing == null) return new Vec3(offset.x(), offset.y(), offset.z());
		double radians = Math.toRadians(facing.toYRot() - Direction.NORTH.toYRot());
		double sin = Math.sin(radians);
		double cos = Math.cos(radians);
		return new Vec3(offset.x() * cos - offset.z() * sin, offset.y(),
				offset.x() * sin + offset.z() * cos);
	}

	private static float yawOf(@Nullable Direction facing, Block.Behaviour.Seat seat) {
		return (facing == null ? 0.0F : facing.toYRot()) + seat.direction;
	}

	private static @Nullable SeatCandidate pickSeat(Level level, SeatContext context, Player player,
	                                                @Nullable BlockHitResult hit) {
		Vec3 clicked = hit != null ? hit.getLocation() : Vec3.atCenterOf(context.anchor());
		SeatCandidate best = null;
		double bestDistance = Double.MAX_VALUE;

		for (int index = 0; index < context.seats().size(); index++) {
			Block.Behaviour.Seat seat = context.seats().get(index);
			Vec3 position = seatPosition(context.anchor(), context.facing(), seat);
			SeatEntity existing = findSeat(level, context.anchor(), index, position);
			if (existing != null && !existing.getPassengers().isEmpty()) continue;
			float yaw = yawOf(context.facing(), seat);
			if (!isSpaceClear(level, context.anchor(), position, yaw, seat, player)) continue;

			double distance = position.distanceToSqr(clicked);
			if (distance < bestDistance) {
				bestDistance = distance;
				best = new SeatCandidate(index, seat, position, yaw);
			}
		}
		return best;
	}

	/** Cushion-style collision validation, excluding the furniture surface beneath a sitting rider. */
	private static boolean isSpaceClear(Level level, BlockPos anchor, Vec3 position, float yaw, Block.Behaviour.Seat seat,
	                                    LivingEntity passenger) {
		if (!seat.checkSpace) return true;

		AABB checked;
		if (seat.pose == Block.Behaviour.Seat.SeatPose.LYING) {
			Direction direction = Direction.fromYRot(yaw);
			double widthX = direction.getAxis() == Direction.Axis.X ? 1.8 : 0.8;
			double widthZ = direction.getAxis() == Direction.Axis.Z ? 1.8 : 0.8;
			checked = AABB.ofSize(position.add(0.0, 0.35, 0.0), widthX, 0.7, widthZ).deflate(0.02);
		} else {
			AABB body = passenger.getDimensions(Pose.STANDING).makeBoundingBox(position).deflate(0.02);
			checked = new AABB(body.minX, Math.max(body.minY, position.y + 0.45), body.minZ,
					body.maxX, body.maxY, body.maxZ);
		}

		BlockState anchorState = level.getBlockState(anchor);
		ISeatProvider provider = anchorState.getBlock() instanceof ISeatProvider seatProvider ? seatProvider : null;
		for (BlockPos blockPos : BlockPos.betweenClosed(checked)) {
			BlockState state = level.getBlockState(blockPos);
			// The declared furniture supplies the surface; only surrounding blocks are headroom hazards.
			if (provider != null && provider.isSeatPart(level, anchor, blockPos, state)) continue;
			if (state.is(OBlockTags.ABOVE_BYPASSES_SEAT_CHECK)) continue;
			VoxelShape shape = state.getCollisionShape(level, blockPos);
			if (shape.isEmpty()) continue;
			for (AABB box : shape.toAabbs()) {
				if (box.move(blockPos).intersects(checked)) return false;
			}
		}
		return true;
	}

	private static @Nullable SeatContext contextAt(Level level, BlockPos pos, BlockState state) {
		if (!(state.getBlock() instanceof ISeatProvider provider)) return null;
		BlockPos anchor = provider.getSeatAnchor(level, pos, state).immutable();
		BlockState anchorState = level.getBlockState(anchor);
		if (!(anchorState.getBlock() instanceof ISeatProvider anchorProvider)) return null;
		if (!anchorProvider.isSeatAnchorValid(level, anchor, anchorState)) return null;
		List<Block.Behaviour.Seat> seats = anchorProvider.getSeats(anchorState);
		return new SeatContext(anchor, anchorState, facingOf(anchorState), seats != null ? seats : List.of());
	}

	private static @Nullable SeatEntity findSeat(Level level, BlockPos anchor, int index, Vec3 expectedPosition) {
		AABB search = AABB.ofSize(Vec3.atCenterOf(anchor), ANCHOR_SEARCH_RADIUS * 2.0,
				ANCHOR_SEARCH_RADIUS * 2.0, ANCHOR_SEARCH_RADIUS * 2.0);
		for (SeatEntity seat : level.getEntities(Obsidian.SEAT, search,
				seat -> !seat.isRemoved() && anchor.equals(seat.getAnchor()) && seat.getSeatIndex() == index)) {
			return seat;
		}

		// Old in-flight seat entities predate stable keys. Reclaim only an empty one at the exact target.
		for (SeatEntity seat : level.getEntities(Obsidian.SEAT, AABB.ofSize(expectedPosition, 0.5, 0.5, 0.5),
				seat -> !seat.isRemoved() && seat.getSeatIndex() < 0 && seat.getPassengers().isEmpty())) {
			return seat;
		}
		return null;
	}

	private static List<SeatEntity> seatsNearAnchor(Level level, BlockPos anchor) {
		return level.getEntities(Obsidian.SEAT,
				AABB.ofSize(Vec3.atCenterOf(anchor), ANCHOR_SEARCH_RADIUS * 2.0,
						ANCHOR_SEARCH_RADIUS * 2.0, ANCHOR_SEARCH_RADIUS * 2.0), seat -> true);
	}

	private record SeatContext(BlockPos anchor, BlockState state, @Nullable Direction facing,
	                           List<Block.Behaviour.Seat> seats) {
	}

	private record SeatCandidate(int index, Block.Behaviour.Seat seat, Vec3 position, float yaw) {
	}
}

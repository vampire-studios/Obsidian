package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * An invisible, active-only passenger attachment owned by one declared block seat.
 *
 * <p>Like vanilla's cushion it has one passenger, a stable anchor and survival checks. Unlike a
 * cushion it is not furniture by itself, so it disappears as soon as its passenger gets up.
 */
public class SeatEntity extends Entity {

	private static final EntityDataAccessor<Integer> DATA_POSE =
			SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> DATA_LOCK_ROTATION =
			SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.BOOLEAN);

	/** How far a rotation-locked rider may still turn away from the seat direction, as vanilla vehicles allow. */
	private static final float MAX_RIDER_YAW = 105.0F;

	private @Nullable BlockPos anchor;
	private int seatIndex = -1;
	private boolean skipNight;
	private boolean resetPhantoms;

	public SeatEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	public @Nullable BlockPos getAnchor() {
		return this.anchor;
	}

	public int getSeatIndex() {
		return this.seatIndex;
	}

	public Block.Behaviour.Seat.SeatPose getSeatPose() {
		int ordinal = this.entityData.get(DATA_POSE);
		Block.Behaviour.Seat.SeatPose[] poses = Block.Behaviour.Seat.SeatPose.values();
		return ordinal >= 0 && ordinal < poses.length ? poses[ordinal] : Block.Behaviour.Seat.SeatPose.SITTING;
	}

	public boolean isLying() {
		return this.getSeatPose() == Block.Behaviour.Seat.SeatPose.LYING;
	}

	public boolean locksRotation() {
		return this.isLying() || this.entityData.get(DATA_LOCK_ROTATION);
	}

	public void configure(BlockPos anchor, int seatIndex, Block.Behaviour.Seat seat) {
		this.anchor = anchor.immutable();
		this.seatIndex = seatIndex;
		Block.Behaviour.Seat.SeatPose pose = seat.pose != null
				? seat.pose : Block.Behaviour.Seat.SeatPose.SITTING;
		this.entityData.set(DATA_POSE, pose.ordinal());
		this.entityData.set(DATA_LOCK_ROTATION, seat.lockRotation);
		this.skipNight = pose == Block.Behaviour.Seat.SeatPose.LYING && seat.skipNight;
		this.resetPhantoms = pose == Block.Behaviour.Seat.SeatPose.LYING && seat.resetPhantoms;
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
		return false;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DATA_POSE, Block.Behaviour.Seat.SeatPose.SITTING.ordinal());
		builder.define(DATA_LOCK_ROTATION, false);
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level().isClientSide()) {
			if (this.getPassengers().isEmpty()) {
				this.discard();
				return;
			}
			if (this.tickCount % 20 == 0 && !SeatLogic.refreshSeat(this)) {
				this.ejectPassengers();
				this.discard();
				return;
			}
		}

		if (this.getFirstPassenger() instanceof LivingEntity living) {
			if (!this.level().isClientSide() && this.skipNight && living instanceof ServerPlayer player
					&& player.getSleepingPos().isEmpty()) {
				this.ejectPassengers();
				this.discard();
				return;
			}
			this.applyPassengerPresentation(living);
		}
	}

	@Override
	protected void addPassenger(Entity passenger) {
		super.addPassenger(passenger);
		if (passenger instanceof ServerPlayer player) this.startRest(player);
		if (passenger instanceof LivingEntity living) {
			this.applyPassengerPresentation(living);
		}
		this.clampPassengerRotation(passenger);
	}

	private void startRest(ServerPlayer player) {
		if (this.resetPhantoms) player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
		if (!this.skipNight || this.anchor == null) return;
		// Reset vanilla's private sleep timer before marking this non-bed anchor as sleeping.
		player.stopSleepInBed(true, false);
		player.setSleepingPos(this.anchor);
		player.level().updateSleepingPlayerList();
	}

	private void applyPassengerPresentation(LivingEntity living) {
		if (this.isLying()) living.setPose(Pose.SLEEPING);
	}

	/**
	 * Pulls a rider back towards the seat direction the way vanilla vehicles do.
	 *
	 * <p>Assigning the rotation outright fights the rider: a client keeps sending its own yaw and
	 * the seat keeps overwriting it, and because the previous-tick rotation is left untouched the
	 * renderer interpolates across the difference every frame. Nudging by the wrapped delta and
	 * moving {@code yRotO} by the same amount keeps both sides on one value with nothing to
	 * interpolate.
	 */
	private void clampPassengerRotation(Entity passenger) {
		if (!this.locksRotation()) return;

		passenger.setYBodyRot(this.getYRot());
		float difference = Mth.wrapDegrees(passenger.getYRot() - this.getYRot());
		float clamped = Mth.clamp(difference, -MAX_RIDER_YAW, MAX_RIDER_YAW);
		float correction = clamped - difference;
		passenger.yRotO += correction;
		passenger.setYRot(passenger.getYRot() + correction);
		passenger.setYHeadRot(passenger.getYRot());
	}

	@Override
	protected void positionRider(Entity passenger, MoveFunction callback) {
		super.positionRider(passenger, callback);
		this.clampPassengerRotation(passenger);
	}

	@Override
	public void onPassengerTurned(Entity passenger) {
		this.clampPassengerRotation(passenger);
	}

	@Override
	public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
		Vec3 safe = SeatLogic.dismountLocation(this, passenger);
		return safe != null ? safe : super.getDismountLocationForPassenger(passenger);
	}

	@Override
	protected void removePassenger(Entity passenger) {
		boolean wasLying = this.isLying();
		boolean wasSkippingNight = this.skipNight;
		BlockPos oldAnchor = this.anchor;
		super.removePassenger(passenger);

		// A client rebuilding its passenger list drops and re-adds the rider, so nothing here may
		// tear the seat down: only the server decides that a seat is finished. Discarding on the
		// client would delete the vehicle out from under the player still riding it.
		if (this.level().isClientSide()) return;

		if (wasSkippingNight && passenger instanceof ServerPlayer player
				&& oldAnchor != null && player.getSleepingPos().filter(oldAnchor::equals).isPresent()) {
			player.stopSleepInBed(true, true);
		}
		if (wasLying && passenger instanceof LivingEntity living) living.setPose(Pose.STANDING);
		if (!this.isRemoved()) this.discard();
	}

	@Override
	public void remove(RemovalReason reason) {
		BlockPos oldAnchor = this.anchor;
		super.remove(reason);
		if (oldAnchor != null && !this.level().isClientSide()) {
			SeatLogic.updateOccupied(this.level(), oldAnchor, this);
		}
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		int x = input.getIntOr("AnchorX", Integer.MIN_VALUE);
		this.anchor = x == Integer.MIN_VALUE ? null
				: new BlockPos(x, input.getIntOr("AnchorY", 0), input.getIntOr("AnchorZ", 0));
		this.seatIndex = input.getIntOr("SeatIndex", -1);
		this.entityData.set(DATA_POSE, input.getIntOr("SeatPose", 0));
		this.entityData.set(DATA_LOCK_ROTATION, input.getBooleanOr("LockRotation", false));
		this.skipNight = input.getBooleanOr("SkipNight", false);
		this.resetPhantoms = input.getBooleanOr("ResetPhantoms", false);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		if (this.anchor != null) {
			output.putInt("AnchorX", this.anchor.getX());
			output.putInt("AnchorY", this.anchor.getY());
			output.putInt("AnchorZ", this.anchor.getZ());
		}
		output.putInt("SeatIndex", this.seatIndex);
		output.putInt("SeatPose", this.entityData.get(DATA_POSE));
		output.putBoolean("LockRotation", this.entityData.get(DATA_LOCK_ROTATION));
		output.putBoolean("SkipNight", this.skipNight);
		output.putBoolean("ResetPhantoms", this.resetPhantoms);
	}

}

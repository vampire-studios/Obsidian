package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SeatEntity extends Entity {
	protected static float heightOffset = -0.45F;

	private static final ImmutableMap<Pose, ImmutableList<Integer>> DISMOUNT_FREE_Y_SPACES_NEEDED = ImmutableMap.of(
			Pose.STANDING, ImmutableList.of(0, 1, -1), Pose.CROUCHING, ImmutableList.of(0, 1, -1), Pose.SWIMMING, ImmutableList.of(0, 1)
	);

	public SeatEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
		return false;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {

	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {

	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
		return new ClientboundAddEntityPacket(this, entity, this.getId());
	}

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);
		this.remove(RemovalReason.KILLED);
	}
}
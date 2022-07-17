package io.github.vampirestudios.obsidian;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

import java.util.Optional;

public class EntityPositionSource implements PositionSource {
	public static final Codec<EntityPositionSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("source_entity_id").forGetter(entityPositionSource -> entityPositionSource.sourceEntityId),
					Codec.FLOAT.fieldOf("y_offset").forGetter(entityPositionSource -> entityPositionSource.yOffset)
				)
				.apply(instance, EntityPositionSource::new)
	);
	final int sourceEntityId;
	private Optional<Entity> sourceEntity = Optional.empty();
	final float yOffset;

	public EntityPositionSource(Entity entity, float f) {
		this(entity.getId(), f);
	}

	EntityPositionSource(int i, float f) {
		this.sourceEntityId = i;
		this.yOffset = f;
	}

	@Override
	public Optional<Vec3d> getPos(World level) {
		if (this.sourceEntity.isEmpty()) {
			this.sourceEntity = Optional.ofNullable(level.getEntityById(this.sourceEntityId));
		}

		return this.sourceEntity.map(entity -> entity.getPos().add(0.0, this.yOffset, 0.0));
	}

	@Override
	public PositionSourceType<?> getType() {
		return PositionSourceType.ENTITY;
	}

	public static class Type implements PositionSourceType<EntityPositionSource> {
		public EntityPositionSource readFromBuf(PacketByteBuf friendlyByteBuf) {
			return new EntityPositionSource(friendlyByteBuf.readVarInt(), friendlyByteBuf.readFloat());
		}

		public void writeToBuf(PacketByteBuf friendlyByteBuf, EntityPositionSource entityPositionSource) {
			friendlyByteBuf.writeVarInt(entityPositionSource.sourceEntityId);
			friendlyByteBuf.writeFloat(entityPositionSource.yOffset);
		}

		@Override
		public Codec<EntityPositionSource> getCodec() {
			return EntityPositionSource.CODEC;
		}
	}
}

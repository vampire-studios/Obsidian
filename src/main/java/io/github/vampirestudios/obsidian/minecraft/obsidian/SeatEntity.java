package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;

public class SeatEntity extends Entity {
    public static final HashMap<Vec3d,BlockPos> OCCUPIED = new HashMap<>();

    public SeatEntity(World world) {
        super(Obsidian.SEAT, world);
        noClip = true;
    }

    public SeatEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {}

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        if(passenger instanceof PlayerEntity) {
            BlockPos pos = OCCUPIED.remove(getPos());
            if(pos != null) {
                remove(RemovalReason.DISCARDED);
                return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            }
        }
        remove(RemovalReason.DISCARDED);
        return super.updatePassengerForDismount(passenger);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        OCCUPIED.remove(getPos());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound var1) {}

    @Override
    protected void writeCustomDataToNbt(NbtCompound var1) {}

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

}
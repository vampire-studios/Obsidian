package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import java.util.HashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SeatEntity extends Entity {
    public static final HashMap<Vec3,BlockPos> OCCUPIED = new HashMap<>();

    public SeatEntity(Level world) {
        super(Obsidian.SEAT, world);
        noPhysics = true;
    }

    public SeatEntity(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        if(passenger instanceof Player) {
            BlockPos pos = OCCUPIED.remove(position());
            if(pos != null) {
                remove(RemovalReason.DISCARDED);
                return new Vec3(pos.getX(), pos.getY(), pos.getZ());
            }
        }
        remove(RemovalReason.DISCARDED);
        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        OCCUPIED.remove(position());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag var1) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag var1) {}

}
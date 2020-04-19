package io.github.vampirestudios.obsidian.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class EntityImpl extends Entity {

    public EntityImpl(EntityType<?> entityType_1, World world_1) {
        super(entityType_1, world_1);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromTag(CompoundTag var1) {

    }

    @Override
    protected void writeCustomDataToTag(CompoundTag var1) {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }

}
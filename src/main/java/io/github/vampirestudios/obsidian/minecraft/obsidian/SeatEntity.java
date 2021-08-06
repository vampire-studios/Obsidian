package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.SittableBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class SeatEntity extends Entity {

    public SeatEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {}

    @Override
    public void tick() {
        super.tick();

        List<Entity> passengers = getPassengerList();
        boolean dead = passengers.isEmpty();

        BlockPos pos = getBlockPos();
        BlockState state = world.getBlockState(pos);

        if(!dead) {
            if(!(state.getBlock() instanceof SittableBlock)) {
                PistonBlockEntity piston = null;
                boolean didOffset = false;

                BlockEntity tile = world.getBlockEntity(pos);
                if(tile instanceof PistonBlockEntity && ((PistonBlockEntity) tile).getPushedBlock().getBlock() instanceof SittableBlock)
                    piston = (PistonBlockEntity) tile;
                else for(Direction d : Direction.values()) {
                    BlockPos offPos = pos.offset(d, 1);
                    tile = world.getBlockEntity(offPos);

                    if(tile instanceof PistonBlockEntity && ((PistonBlockEntity) tile).getPushedBlock().getBlock() instanceof SittableBlock) {
                        piston = (PistonBlockEntity) tile;
                        break;
                    }
                }

                if(piston != null) {
                    Direction dir = piston.getMovementDirection();
                    move(MovementType.PISTON, new Vec3d( dir.getOffsetX() * 0.33,  dir.getOffsetY() * 0.33,  dir.getOffsetZ() * 0.33));

                    didOffset = true;
                }

                dead = !didOffset;
            }

            if(dead && !world.isClient) {
                kill();

                if(state.getBlock() instanceof SittableBlock)
                    world.setBlockState(pos, state.with(SittableBlock.OCCUPIED, false));
            }
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound var1) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound var1) {

    }

    @Override
    public double getMountedHeightOffset() {
        return 0;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

}
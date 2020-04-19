package io.github.vampirestudios.obsidian.utils;

import net.fabricmc.fabric.api.dimension.v1.EntityPlacer;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public enum PlayerPlacementHandlers {
    SURFACE_WORLD((teleported, destination, portalDir, horizontalOffset, verticalOffset) -> {
        BlockPos blockPos = getSurfacePos(destination, teleported);
        return new BlockPattern.TeleportTarget(new Vec3d(new Vector3f(blockPos.getX(), blockPos.getY(), blockPos.getZ())), Vec3d.ZERO, 0);
    }),
    CAVE_WORLD((teleported, destination, portalDir, horizontalOffset, verticalOffset) -> {
        BlockPos blockPos = getSurfacePos(destination, teleported);
        return new BlockPattern.TeleportTarget(new Vec3d(new Vector3f(blockPos.getX(), blockPos.getY(), blockPos.getZ())), Vec3d.ZERO, 0);
    }),
    OVERWORLD((teleported, destination, portalDir, horizontalOffset, verticalOffset) -> {
        BlockPos blockPos = getSurfacePos(destination, teleported);
        return new BlockPattern.TeleportTarget(new Vec3d(new Vector3f(blockPos.getX(), blockPos.getY(), blockPos.getZ())), Vec3d.ZERO, 0);
    }),
    FLOATING_WORLD((teleported, destination, portalDir, horizontalOffset, verticalOffset) -> {
        BlockPos blockPos = getSurfacePos(destination, teleported);
        if (blockPos.getY() == 255 || blockPos.getY() == 256) {
            blockPos = new BlockPos(blockPos.getX(), teleported.getY(), blockPos.getZ());
            return new BlockPattern.TeleportTarget(new Vec3d(new Vector3f(blockPos.up(2).getX(), blockPos.up(2).getY(), blockPos.up(2).getZ())), Vec3d.ZERO, 0);
        } else {
            return new BlockPattern.TeleportTarget(new Vec3d(new Vector3f(blockPos.getX(), blockPos.getY(), blockPos.getZ())), Vec3d.ZERO, 0);
        }
    });

    private final EntityPlacer entityPlacer;

    PlayerPlacementHandlers(EntityPlacer entityPlacer) {
        this.entityPlacer = entityPlacer;
    }

    private static BlockPos getSurfacePos(ServerWorld serverWorld, Entity entity) {
        BlockPos portalPos;
        for (int i = 255; i > 0; i--) {
            BlockPos pos = new BlockPos(entity.getBlockPos().getX(), i, entity.getBlockPos().getZ());
            if (!(serverWorld.getBlockState(pos.down(1)).getBlock() instanceof AirBlock) && (serverWorld.getBlockState(pos.up()).getBlock() instanceof AirBlock) && (serverWorld.getBlockState(pos).getBlock() instanceof AirBlock)) {
                return pos.up();
            }
        }
        portalPos = new BlockPos(entity.getBlockPos().getX(), 255 + 1, entity.getBlockPos().getZ());
        serverWorld.setBlockState(portalPos.up(), Blocks.AIR.getDefaultState());
        serverWorld.setBlockState(portalPos, Blocks.AIR.getDefaultState());
        return portalPos;
    }

    private static BlockPos getPortalPos(Entity entity) {
        return new BlockPos(entity.getPos().x, 255, entity.getPos().z);
    }

    public EntityPlacer getEntityPlacer() {
        return entityPlacer;
    }
}

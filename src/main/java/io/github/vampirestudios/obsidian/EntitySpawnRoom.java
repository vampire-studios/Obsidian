package io.github.vampirestudios.obsidian;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class EntitySpawnRoom extends StructurePiece {

    private final BackroomsLevel level;

    public EntitySpawnRoom(int depth, RandomSource random, BoundingBox boundingBox, BackroomsLevel level) {
        super(OStructurePieceTypes.BACKROOMS_ROOM, depth, boundingBox);
        this.level = level;
    }

    @Override
    public void postProcess(WorldGenLevel world, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
        // Customize the room as usual
        this.generateBox(world, boundingBox, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ(), Blocks.STONE.defaultBlockState(), Blocks.CAVE_AIR.defaultBlockState(), false);

        // Add entities based on the level
        if (level == BackroomsLevel.LEVEL_2) {
            EntityType.ZOMBIE.spawn((ServerLevel) world, null, null, pos, EntitySpawnReason.STRUCTURE, true, false);
        } else if (level == BackroomsLevel.LEVEL_3) {
            EntityType.ENDERMAN.spawn((ServerLevel) world, null, null, pos, EntitySpawnReason.STRUCTURE, true, false);
        } else if (level == BackroomsLevel.LEVEL_4) {
            EntityType.WITCH.spawn((ServerLevel) world, null, null, pos, EntitySpawnReason.STRUCTURE, true, false);
        }
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
    }
}

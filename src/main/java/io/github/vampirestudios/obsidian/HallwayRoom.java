package io.github.vampirestudios.obsidian;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class HallwayRoom extends StructurePiece {

    public HallwayRoom(int depth, RandomSource random, BoundingBox boundingBox) {
        super(StructurePieceType.MINE_SHAFT_CORRIDOR, depth, boundingBox);
    }

    @Override
    public void postProcess(WorldGenLevel world, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
        // Generate a long narrow hallway
        this.generateBox(world, boundingBox, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ(), Blocks.STONE.defaultBlockState(), Blocks.CAVE_AIR.defaultBlockState(), false);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
    }
}
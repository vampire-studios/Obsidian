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

public class SecretRoomPiece extends StructurePiece {

    public SecretRoomPiece(int depth, RandomSource random, BoundingBox boundingBox) {
        super(StructurePieceType.MINE_SHAFT_ROOM, depth, boundingBox);
    }

    public static BoundingBox findSecretRoomBounds(RandomSource random, int x, int y, int z) {
        int width = random.nextInt(3) + 3;
        int height = random.nextInt(3) + 3;
        return new BoundingBox(x, y, z, x + width, y + height, z + width);
    }

    @Override
    public void postProcess(WorldGenLevel world, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
        // Generate a simple hidden room
        this.generateBox(world, boundingBox, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ(), Blocks.STONE.defaultBlockState(), Blocks.CAVE_AIR.defaultBlockState(), false);
        
        // Optionally add loot or a rare item inside
        if (random.nextFloat() < 0.25) {
            this.generateBox(world, boundingBox, this.boundingBox.minX() + 1, this.boundingBox.minY() + 1, this.boundingBox.minZ() + 1, this.boundingBox.maxX() - 1, this.boundingBox.maxY() - 1, this.boundingBox.maxZ() - 1, Blocks.CHEST.defaultBlockState(), Blocks.CHEST.defaultBlockState(), false);
        }
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {

    }
}

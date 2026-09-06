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

public class TrapPiece extends StructurePiece {
	public TrapPiece(int depth, RandomSource random, BoundingBox boundingBox) {
		super(StructurePieceType.MINE_SHAFT_ROOM, depth, boundingBox);
	}

	public static BoundingBox findTrapBounds(RandomSource random, int x, int y, int z) {
		int width = random.nextInt(3) + 3;
		int height = random.nextInt(3) + 3;
		return new BoundingBox(x, y, z, x + width, y + height, z + width);
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {

	}

	@Override
	public void postProcess(WorldGenLevel world, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
		// Generate a simple pitfall trap
		this.generateBox(world, boundingBox, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.minY() + 1, this.boundingBox.maxZ(), Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
		this.generateBox(world, boundingBox, this.boundingBox.minX(), this.boundingBox.minY() - 4, this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.minY(), this.boundingBox.maxZ(), Blocks.STONE.defaultBlockState(), Blocks.STONE.defaultBlockState(), false);

		// Optionally add some TNT at the bottom
		if (random.nextFloat() < 0.25) {
			this.generateBox(world, boundingBox, this.boundingBox.minX(), this.boundingBox.minY() - 3, this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.minY() - 2, this.boundingBox.maxZ(), Blocks.TNT.defaultBlockState(), Blocks.TNT.defaultBlockState(), false);
		}
	}
}

package io.github.vampirestudios.obsidian;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.List;
import java.util.Locale;

public class BackroomsPieces {
	static class BackroomsRoom extends StructurePiece {
		private final List<BoundingBox> childEntranceBoxes;
		private final List<StructurePiece> children;
		private final BackroomsLevel level;

		public BackroomsRoom(CompoundTag nbt) {
			super(OStructurePieceTypes.BACKROOMS_ROOM, nbt);
			this.childEntranceBoxes = Lists.newArrayList();
			this.children = Lists.newArrayList();
			this.level = BackroomsLevel.valueOf(nbt.getStringOr("level", "level_0").toUpperCase(Locale.ROOT));
		}

		public BackroomsRoom(int depth, RandomSource random, int x, int z, BackroomsLevel level) {
			super(OStructurePieceTypes.BACKROOMS_ROOM, depth, new BoundingBox(x, 50, z, x + 7 + random.nextInt(6), 54 + random.nextInt(6), z + 7 + random.nextInt(6)));
			this.childEntranceBoxes = Lists.newArrayList();
			this.children = Lists.newArrayList();
			this.level = level;
		}

		@Override
		protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
			nbt.putString("level", this.level.name().toLowerCase(Locale.ROOT));
		}

		@Override
		public void addChildren(StructurePiece start, StructurePiecesBuilder pieces, RandomSource random) {
			int depth = this.getGenDepth();
			int height = this.boundingBox.getYSpan() - 3 - 1;
			if (height <= 0) {
				height = 1;
			}

			int widthOffset = 0;
			while (widthOffset < this.boundingBox.getXSpan()) {
				widthOffset += random.nextInt(this.boundingBox.getXSpan());
				if (widthOffset + 3 > this.boundingBox.getXSpan()) break;

				StructurePiece corridorPiece = BackroomsPieces.generateAndAddPiece(
						start, pieces, random, this.boundingBox.minX() + widthOffset,
						this.boundingBox.minY() + random.nextInt(height) + 1, this.boundingBox.minZ() - 1,
						Direction.NORTH, depth, this.level
				);
				if (corridorPiece != null) {
					this.children.add(corridorPiece);
					BoundingBox corridorBox = corridorPiece.getBoundingBox();
					this.childEntranceBoxes.add(new BoundingBox(
							corridorBox.minX(), corridorBox.minY(), this.boundingBox.minZ(),
							corridorBox.maxX(), corridorBox.maxY(), this.boundingBox.minZ() + 1
					));
					corridorPiece.addChildren(start, pieces, random);
				}

				widthOffset += 4;
			}
		}

		@Override
		public void postProcess(WorldGenLevel world, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
			for (StructurePiece child : this.children) {
				child.postProcess(world, structureManager, chunkGenerator, random, boundingBox, chunkPos, pos);
			}

			// Define block states for different levels
			BlockState bottomWallBlock = Blocks.STRIPPED_BIRCH_LOG.defaultBlockState();
			BlockState topWallBlock = Blocks.SMOOTH_SANDSTONE.defaultBlockState();
			BlockState floorBlock = Blocks.OAK_PLANKS.defaultBlockState();
			BlockState ceilingBlock = Blocks.BEEHIVE.defaultBlockState();
			BlockState lightBlock = Blocks.OCHRE_FROGLIGHT.defaultBlockState();
			BlockState doorBlock = Blocks.OAK_DOOR.defaultBlockState();

			// Adjust block types based on the level
			switch (this.level) {
				case LEVEL_0 -> {
					bottomWallBlock = Blocks.BIRCH_PLANKS.defaultBlockState();
					topWallBlock = Blocks.SMOOTH_SANDSTONE.defaultBlockState();
					floorBlock = Blocks.OAK_PLANKS.defaultBlockState();
					ceilingBlock = Blocks.BEEHIVE.defaultBlockState();
					lightBlock = Blocks.OCHRE_FROGLIGHT.defaultBlockState();
				}
				case LEVEL_1 -> {
					bottomWallBlock = Blocks.STONE_BRICKS.defaultBlockState();
					topWallBlock = Blocks.STONE_BRICKS.defaultBlockState();
					floorBlock = Blocks.STONE.defaultBlockState();
					ceilingBlock = Blocks.STONE.defaultBlockState();
					lightBlock = Blocks.COPPER_BULB.weathering().unaffected().defaultBlockState().setValue(CopperBulbBlock.LIT, true);
				}
				case LEVEL_2 -> {
					bottomWallBlock = Blocks.NETHER_BRICKS.defaultBlockState();
					topWallBlock = Blocks.NETHER_BRICKS.defaultBlockState();
					floorBlock = Blocks.NETHERRACK.defaultBlockState();
					ceilingBlock = Blocks.NETHER_BRICKS.defaultBlockState();
					lightBlock = Blocks.PEARLESCENT_FROGLIGHT.defaultBlockState();
				}
				case LEVEL_3 -> {
					bottomWallBlock = Blocks.IRON_BLOCK.defaultBlockState();
					topWallBlock = Blocks.IRON_BLOCK.defaultBlockState();
					floorBlock = Blocks.IRON_BARS.defaultBlockState();
					ceilingBlock = Blocks.IRON_BLOCK.defaultBlockState();
				}
				case LEVEL_4 -> {
					bottomWallBlock = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
					topWallBlock = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
					floorBlock = Blocks.DIRT.defaultBlockState();
					ceilingBlock = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
				}
				case POOL_ROOMS -> {
					bottomWallBlock = Blocks.QUARTZ_BLOCK.defaultBlockState();
					topWallBlock = Blocks.QUARTZ_BLOCK.defaultBlockState();
					floorBlock = Blocks.WATER.defaultBlockState();
					ceilingBlock = Blocks.SEA_LANTERN.defaultBlockState();
				}
				case TERROR_HOTEL -> {
					bottomWallBlock = Blocks.DARK_OAK_PLANKS.defaultBlockState();
					topWallBlock = Blocks.DARK_OAK_PLANKS.defaultBlockState();
					floorBlock = Blocks.CARPET.red().defaultBlockState();
					ceilingBlock = Blocks.DARK_OAK_PLANKS.defaultBlockState();
				}
				case SUBURBS -> {
					bottomWallBlock = Blocks.BRICKS.defaultBlockState();
					topWallBlock = Blocks.BRICKS.defaultBlockState();
					floorBlock = Blocks.OAK_PLANKS.defaultBlockState();
					ceilingBlock = Blocks.BRICKS.defaultBlockState();
				}
				case PARADISE -> {
					bottomWallBlock = Blocks.GRASS_BLOCK.defaultBlockState();
					topWallBlock = Blocks.GRASS_BLOCK.defaultBlockState();
					floorBlock = Blocks.SAND.defaultBlockState();
					ceilingBlock = Blocks.GRASS_BLOCK.defaultBlockState();
				}
			}

			// Create walls with the specified block palette
			for (int x = this.boundingBox.minX(); x <= this.boundingBox.maxX(); x++) {
				for (int z = this.boundingBox.minZ(); z <= this.boundingBox.maxZ(); z++) {
					if (x == this.boundingBox.minX() || x == this.boundingBox.maxX() || z == this.boundingBox.minZ() || z == this.boundingBox.maxZ()) {
						// Bottom part of the walls (Stripped Birch Logs)
						this.placeBlock(world, bottomWallBlock.trySetValue(RotatedPillarBlock.AXIS, Direction.Axis.Y), x, this.boundingBox.minY(), z, boundingBox);

						// Top part of the walls (Smooth Sandstone)
						for (int y = this.boundingBox.minY() + 1; y <= this.boundingBox.maxY(); y++) {
							this.placeBlock(world, topWallBlock, x, y, z, boundingBox);
						}
					}
				}
			}

			// Generate the main structure: walls, floor, ceiling
			// Generate floor
			for (int x = this.boundingBox.minX(); x <= this.boundingBox.maxX(); x++) {
				for (int z = this.boundingBox.minZ(); z <= this.boundingBox.maxZ(); z++) {
					this.placeBlock(world, floorBlock, x, this.boundingBox.minY(), z, boundingBox);
				}
			}

			// Create ceiling with Beehives and Ochre Froglights
			for (int x = this.boundingBox.minX() + 1; x < this.boundingBox.maxX(); x++) {
				for (int z = this.boundingBox.minZ() + 1; z < this.boundingBox.maxZ(); z++) {
					// Randomly place Ochre Froglights for lighting, otherwise use Beehives
					if (random.nextFloat() < 0.2) {
						this.placeBlock(world, lightBlock, x, this.boundingBox.maxY(), z, boundingBox);
					} else {
						this.placeBlock(world, ceilingBlock, x, this.boundingBox.maxY(), z, boundingBox);
					}
				}
			}

			for (int x = this.boundingBox.minX(); x <= this.boundingBox.maxX(); x++) {
				for (int y = this.boundingBox.minY() + 1; y <= this.boundingBox.maxY() - 1; y++) {
					for (int z = this.boundingBox.minZ(); z <= this.boundingBox.maxZ(); z++) {
						if (world.getBlockState(new BlockPos(x, y, z)) != floorBlock &&
								world.getBlockState(new BlockPos(x, y, z)) != bottomWallBlock &&
								world.getBlockState(new BlockPos(x, y, z)) != topWallBlock &&
								world.getBlockState(new BlockPos(x, y, z)) != lightBlock &&
								world.getBlockState(new BlockPos(x, y, z)) != ceilingBlock) {
							this.placeBlock(world, Blocks.AIR.defaultBlockState(), x, y, z, boundingBox);
						}
					}
				}
			}
		}

		@Override
		public void move(int x, int y, int z) {
			super.move(x, y, z);
			for (StructurePiece child : this.children) {
				child.move(x, y, z);
			}
		}
	}

	static StructurePiece generateAndAddPiece(StructurePiece start, StructurePiecesBuilder pieces, RandomSource random, int x, int y, int z, Direction orientation, int depth, BackroomsLevel level) {
		StructurePiece newPiece = null;

		switch (level) {
			case LEVEL_0 -> {
				if (random.nextFloat() < 0.3) {
					newPiece = new HallwayRoom(depth, random, new BoundingBox(x, y, z, x + 5, y + 4, z + 20));
				} else if (random.nextFloat() < 0.5) {
					newPiece = new LargeRoom(depth, random, new BoundingBox(x, y, z, x + 15, y + 10, z + 15));
				} else {
					newPiece = new SmallRoom(depth, random, new BoundingBox(x, y, z, x + 5, y + 4, z + 5));
				}
			}
			case LEVEL_1 -> {
				if (random.nextFloat() < 0.4) {
					newPiece = new LargeRoom(depth, random, new BoundingBox(x, y, z, x + 20, y + 10, z + 20));
				} else {
					newPiece = new SmallRoom(depth, random, new BoundingBox(x, y, z, x + 5, y + 4, z + 5));
				}
			}
			case LEVEL_2 -> {
				// LEVEL_2 could be more dangerous or challenging, with more traps
				if (random.nextFloat() < 0.5) {
					BoundingBox trapBounds = TrapPiece.findTrapBounds(random, x, y, z);
					if (pieces.findCollisionPiece(trapBounds) == null) {
						TrapPiece trapPiece = new TrapPiece(depth, random, trapBounds);
						pieces.addPiece(trapPiece);
						return trapPiece;
					}
				} else {
					newPiece = new SmallRoom(depth, random, new BoundingBox(x, y, z, x + 5, y + 4, z + 5));
				}
			}
			case LEVEL_3 -> {
				// LEVEL_3 could be a mix of large rooms and narrow hallways, representing a factory-like environment
				if (random.nextFloat() < 0.3) {
					newPiece = new HallwayRoom(depth, random, new BoundingBox(x, y, z, x + 10, y + 5, z + 50));
				} else if (random.nextFloat() < 0.6) {
					newPiece = new LargeRoom(depth, random, new BoundingBox(x, y, z, x + 25, y + 15, z + 25));
				} else {
					newPiece = new SmallRoom(depth, random, new BoundingBox(x, y, z, x + 7, y + 5, z + 7));
				}
			}
			case LEVEL_4 -> {
				// LEVEL_4 could be claustrophobic with small rooms and dead ends
				if (random.nextFloat() < 0.2) {
					newPiece = new HallwayRoom(depth, random, new BoundingBox(x, y, z, x + 3, y + 3, z + 15));
				} else {
					newPiece = new SmallRoom(depth, random, new BoundingBox(x, y, z, x + 4, y + 4, z + 4));
				}
			}
		}

		if (newPiece != null && pieces.findCollisionPiece(newPiece.getBoundingBox()) == null) {
			pieces.addPiece(newPiece);
			return newPiece;
		}

		return null;
	}
}
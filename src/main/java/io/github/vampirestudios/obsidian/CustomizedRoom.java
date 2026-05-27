package io.github.vampirestudios.obsidian;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class CustomizedRoom extends StructurePiece {

    private final BackroomsLevel level;

    public CustomizedRoom(int depth, RandomSource random, BoundingBox boundingBox, BackroomsLevel level) {
        super(OStructurePieceTypes.BACKROOMS_ROOM, depth, boundingBox);
        this.level = level;
    }

    @Override
    public void postProcess(WorldGenLevel world, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
        BlockState wallBlock;
        BlockState floorBlock;
        BlockState ceilingBlock;

        switch (level) {
            case LEVEL_0 -> {
                wallBlock = Blocks.DYED_TERRACOTTA.yellow().defaultBlockState();
                floorBlock = Blocks.WOOL.gray().defaultBlockState();
                ceilingBlock = Blocks.SEA_LANTERN.defaultBlockState();
            }
            case LEVEL_1 -> {
                wallBlock = Blocks.STONE_BRICKS.defaultBlockState();
                floorBlock = Blocks.STONE.defaultBlockState();
                ceilingBlock = Blocks.STONE.defaultBlockState();
            }
            case LEVEL_2 -> {
                wallBlock = Blocks.NETHER_BRICKS.defaultBlockState();
                floorBlock = Blocks.NETHERRACK.defaultBlockState();
                ceilingBlock = Blocks.NETHER_BRICKS.defaultBlockState();
            }
            case LEVEL_3 -> {
                wallBlock = Blocks.IRON_BLOCK.defaultBlockState();
                floorBlock = Blocks.IRON_BARS.defaultBlockState();
                ceilingBlock = Blocks.IRON_BLOCK.defaultBlockState();
            }
            case LEVEL_4 -> {
                wallBlock = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
                floorBlock = Blocks.DIRT.defaultBlockState();
                ceilingBlock = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
            }
            default -> {
                wallBlock = Blocks.STONE.defaultBlockState();
                floorBlock = Blocks.STONE.defaultBlockState();
                ceilingBlock = Blocks.STONE.defaultBlockState();
            }
        }

        this.generateBox(world, boundingBox, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ(), wallBlock, Blocks.CAVE_AIR.defaultBlockState(), false);
        this.generateBox(world, boundingBox, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.minY(), this.boundingBox.maxZ(), floorBlock, floorBlock, false);
        this.generateBox(world, boundingBox, this.boundingBox.minX(), this.boundingBox.maxY(), this.boundingBox.minZ(), this.boundingBox.maxX(), this.boundingBox.maxY(), this.boundingBox.maxZ(), ceilingBlock, ceilingBlock, false);

        // Add entities or effects based on the level
        if (level == BackroomsLevel.LEVEL_2 && random.nextFloat() < 0.3) {
            EntityTypes.BLAZE.spawn((ServerLevel) world, null, null, pos, EntitySpawnReason.STRUCTURE, true, false);
        } else if (level == BackroomsLevel.LEVEL_3 && random.nextFloat() < 0.3) {
            EntityTypes.CREEPER.spawn((ServerLevel) world, null, null, pos, EntitySpawnReason.STRUCTURE, true, false);
        } else if (level == BackroomsLevel.LEVEL_4 && random.nextFloat() < 0.3) {
            EntityTypes.ENDERMAN.spawn((ServerLevel) world, null, null, pos, EntitySpawnReason.STRUCTURE, true, false);
        }

        // Add ambient sounds
        if (level == BackroomsLevel.LEVEL_0) {
            world.playSound(null, pos, SoundEvents.AMBIENT_CAVE.value(), SoundSource.AMBIENT, 1.0F, 1.0F);
        } else if (level == BackroomsLevel.LEVEL_1) {
            world.playSound(null, pos, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE, SoundSource.AMBIENT, 1.0F, 1.0F);
        } else if (level == BackroomsLevel.LEVEL_2) {
            world.playSound(null, pos, SoundEvents.LAVA_AMBIENT, SoundSource.AMBIENT, 1.0F, 1.0F);
        } else if (level == BackroomsLevel.LEVEL_3) {
            world.playSound(null, pos, SoundEvents.BEEHIVE_WORK, SoundSource.AMBIENT, 1.0F, 1.0F);
        } else if (level == BackroomsLevel.LEVEL_4) {
            world.playSound(null, pos, SoundEvents.WITHER_SPAWN, SoundSource.AMBIENT, 1.0F, 1.0F);
        }
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
    }
}

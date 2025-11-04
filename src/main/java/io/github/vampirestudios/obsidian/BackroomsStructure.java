package io.github.vampirestudios.obsidian;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class BackroomsStructure extends Structure {
    public static final MapCodec<BackroomsStructure> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            settingsCodec(instance)
    ).apply(instance, BackroomsStructure::new));

    public BackroomsStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), 50, chunkPos.getMinBlockZ());
        StructurePiecesBuilder piecesBuilder = new StructurePiecesBuilder();
        int yOffset = this.generatePieces(piecesBuilder, context);
        return Optional.of(new GenerationStub(blockPos.offset(0, yOffset, 0), Either.right(piecesBuilder)));
    }

    private int generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        WorldgenRandom random = context.random();
        BackroomsPieces.BackroomsRoom initialRoom = new BackroomsPieces.BackroomsRoom(0, random, chunkPos.getBlockX(2), chunkPos.getBlockZ(2), BackroomsLevel.LEVEL_0);
        builder.addPiece(initialRoom);
        initialRoom.addChildren(initialRoom, builder, random);

        // Use the level enum to adjust the structure generation
        int seaLevel = context.chunkGenerator().getSeaLevel();
        BlockPos centerPos = builder.getBoundingBox().getCenter();
        int topY = context.chunkGenerator().getBaseHeight(centerPos.getX(), centerPos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int verticalOffset = Mth.randomBetweenInclusive(random, seaLevel, topY);
        builder.offsetPiecesVertically(verticalOffset - centerPos.getY());

        return verticalOffset;
    }

    @Override
    public StructureType<?> type() {
        return OStructureTypes.BACKROOMS; // Use your custom StructureType
    }
}
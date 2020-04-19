package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.hidden_gems.DimensionalBiomeSourceConfig;
import io.github.vampirestudios.hidden_gems.HiddenGems;
import io.github.vampirestudios.vampirelib.utils.Color;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import javax.annotation.Nullable;
import java.util.Set;

public class DimensionImpl extends Dimension {

    public io.github.vampirestudios.obsidian.api.world.Dimension dimension;
    private final DimensionType dimensionType;
    private final Set<Biome> biomeSet;

    public DimensionImpl(io.github.vampirestudios.obsidian.api.world.Dimension dimension, World world, DimensionType type, Set<Biome> biomeSet) {
        super(world, type, 0.0F);
        this.dimensionType = type;
        this.dimension = dimension;
        this.biomeSet = biomeSet;
    }

    @Override
    public boolean isNether() {
        return dimension.netherLike;
    }

    @Override
    public boolean doesWaterVaporize() {
        return dimension.waterVaporize;
    }

    @Override
    public ChunkGenerator<?> createChunkGenerator() {
        return dimension.getChunkGenerator(world, HiddenGems.DIMENSIONAL_BIOMES.
                applyConfig(new DimensionalBiomeSourceConfig(this.world.getLevelProperties()).setBiomes(biomeSet)));
    }

    @Nullable
    @Override
    public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity) {
        for (int startX = chunkPos.getStartX(); startX <= chunkPos.getEndX(); ++startX) {
            for (int startZ = chunkPos.getStartZ(); startZ <= chunkPos.getEndZ(); ++startZ) {
                BlockPos topSpawningBlockPosition = this.getTopSpawningBlockPosition(startX, startZ, checkMobSpawnValidity);
                if (topSpawningBlockPosition != null) {
                    return topSpawningBlockPosition;
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, 0, z);
        Biome biome = this.world.getBiomeAccess().getBiome(mutable);
        BlockState topMaterial = biome.getSurfaceConfig().getTopMaterial();
        if (checkMobSpawnValidity && !topMaterial.getBlock().isIn(BlockTags.VALID_SPAWN)) {
            return null;
        } else {
            Chunk chunk = this.world.getChunk(x >> 4, z >> 4);
            int heightmap = chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 15, z & 15);
            if (heightmap < 0) {
                return null;
            } else if (chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 15, z & 15) > chunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, x & 15, z & 15)) {
                return null;
            } else {
                for (int i = heightmap + 1; i >= 0; --i) {
                    mutable.set(x, i, z);
                    BlockState blockState = this.world.getBlockState(mutable);
                    if (!blockState.getFluidState().isEmpty()) {
                        break;
                    }

                    if (blockState.equals(topMaterial)) {
                        return mutable.up().toImmutable();
                    }
                }

                return null;
            }
        }
    }

    @Override
    public float getSkyAngle(long timeOfDay, float tickDelta) {
        if (dimension.hasSky) {
            double fractionalPart = MathHelper.fractionalPart((double) timeOfDay / 24000.0D - 0.25D);
            double v1 = 0.5D - Math.cos(fractionalPart * 3.141592653589793D) / 2.0D;
            return (float) (fractionalPart * 2.0D + v1) / 3.0F;
        } else {
            return 0.0F;
        }
    }

    @Override
    public boolean hasVisibleSky() {
        return dimension.hasSky;
    }

    @Override
    public Vec3d modifyFogColor(Vec3d fogColor, float tickDelta) {
        int fogColor2 = dimension.colors.fogColor;
        int[] rgbColor = Color.intToRgb(fogColor2);
        return new Vec3d(rgbColor[0] / 255.0, rgbColor[1] / 255.0, rgbColor[2] / 255.0);
    }

    @Override
    public boolean canPlayersSleep() {
        return dimension.canSleep;
    }

    @Override
    public boolean isFogThick(int x, int z) {
        return dimension.renderFog;
    }

    @Override
    public DimensionType getType() {
        return dimensionType;
    }

}
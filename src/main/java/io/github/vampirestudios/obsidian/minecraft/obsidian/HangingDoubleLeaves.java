package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class HangingDoubleLeaves extends Block {
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    protected static final VoxelShape WISTERIA_VINE_TOP = Block.createCuboidShape(1, 0, 1, 15, 16, 15);
    protected static final VoxelShape WISTERIA_VINE_BOTTOM = Block.createCuboidShape(4, 0, 4, 12, 16, 12);

    public HangingDoubleLeaves(AbstractBlock.Settings properties) {
        super(properties);
        setDefaultState(getStateManager().getDefaultState().with(HALF, DoubleBlockHalf.UPPER));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) return WISTERIA_VINE_TOP;
        if (state.get(HALF) == DoubleBlockHalf.LOWER) return WISTERIA_VINE_BOTTOM;
        else return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (state == getDefaultState().with(HALF, DoubleBlockHalf.UPPER)) {
            if (world.getBlockState(pos.up()) == Blocks.AIR.getDefaultState()) {
                world.removeBlock(pos, false);
            }
        } else if (state == getDefaultState().with(HALF, DoubleBlockHalf.LOWER)) {
            if (world.getBlockState(pos.up()) == Blocks.AIR.getDefaultState()) {
                world.removeBlock(pos, false);
            }
        }
    }

    public int getOpacity(BlockState state, BlockView worldIn, BlockPos pos) {
        return 1;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (worldIn.hasRain(pos.up())) {
            if (rand.nextInt(15) == 1) {
                BlockPos blockpos = pos.down();
                BlockState blockstate = worldIn.getBlockState(blockpos);
                if (!blockstate.isOpaque() || !blockstate.isSideSolidFullSquare(worldIn, blockpos, Direction.UP)) {
                    double d0 = (float) pos.getX() + rand.nextFloat();
                    double d1 = (double) pos.getY() - 0.05D;
                    double d2 = (float) pos.getZ() + rand.nextFloat();
                    worldIn.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

	/*public boolean causesSuffocation(BlockState state, BlockView worldIn, BlockPos pos) {
		return false;
	}

	public boolean canEntitySpawn(BlockState state, BlockView worldIn, BlockPos pos, EntityType<?> type) {
		return type == EntityType.OCELOT || type == EntityType.PARROT;
	}*/

    protected boolean isStateValid(World worldIn, BlockPos pos) {
        BlockState blockState = worldIn.getBlockState(pos.up());
        return blockState == getDefaultState().with(HALF, DoubleBlockHalf.UPPER) || blockState.isIn(BlockTags.LEAVES) || blockState.isIn(BlockTags.LOGS);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        if (isStateValid(world, pos)) {
            if (world.getBlockState(pos.up()) == getDefaultState().with(HALF, DoubleBlockHalf.UPPER)) {
                return getDefaultState().with(HALF, DoubleBlockHalf.LOWER);
            } else if (world.getBlockState(pos.up()) == getDefaultState().with(HALF, DoubleBlockHalf.LOWER)) {
                return null;
            } else return getDefaultState().with(HALF, DoubleBlockHalf.UPPER);
        }
        return null;
    }

}
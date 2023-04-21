package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HangingDoubleLeaves extends Block {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    protected static final VoxelShape WISTERIA_VINE_TOP = Block.box(1, 0, 1, 15, 16, 15);
    protected static final VoxelShape WISTERIA_VINE_BOTTOM = Block.box(4, 0, 4, 12, 16, 12);

    public HangingDoubleLeaves(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(HALF, DoubleBlockHalf.UPPER));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) return WISTERIA_VINE_TOP;
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) return WISTERIA_VINE_BOTTOM;
        else return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (state == defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER)) {
            if (world.getBlockState(pos.above()) == Blocks.AIR.defaultBlockState()) {
                world.removeBlock(pos, false);
            }
        } else if (state == defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER)) {
            if (world.getBlockState(pos.above()) == Blocks.AIR.defaultBlockState()) {
                world.removeBlock(pos, false);
            }
        }
    }

    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        if (worldIn.isRainingAt(pos.above())) {
            if (rand.nextInt(15) == 1) {
                BlockPos blockpos = pos.below();
                BlockState blockstate = worldIn.getBlockState(blockpos);
                if (!blockstate.canOcclude() || !blockstate.isFaceSturdy(worldIn, blockpos, Direction.UP)) {
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

    protected boolean isStateValid(Level worldIn, BlockPos pos) {
        BlockState blockState = worldIn.getBlockState(pos.above());
        return blockState == defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER) || blockState.is(BlockTags.LEAVES) || blockState.is(BlockTags.LOGS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (isStateValid(world, pos)) {
            if (world.getBlockState(pos.above()) == defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER)) {
                return defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER);
            } else if (world.getBlockState(pos.above()) == defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER)) {
                return null;
            } else return defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER);
        }
        return null;
    }

}
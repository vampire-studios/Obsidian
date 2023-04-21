package io.github.vampirestudios.obsidian.api.obsidian.block;

import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SaplingBaseBlock extends BushBlock implements BonemealableBlock {
	public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
	protected static final VoxelShape SHAPE = net.minecraft.world.level.block.Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
	private final Block block;

    public SaplingBaseBlock(Block block) {
        super(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
		this.block = block;
		this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (world.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) {
			this.generateNew(world, pos, state);
		}
	}

	public void generateNew(ServerLevel world, BlockPos pos, BlockState state) {
		if (state.getValue(STAGE) == 0) {
			world.setBlock(pos, state.cycle(STAGE), 4);
		} else {
			if (block.placable_feature != null) {
				world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				if (world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).containsKey(block.placable_feature)) {
					ConfiguredFeature<?, ?> feature = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).get(block.placable_feature);
					assert feature != null;
					feature.place(world, world.getChunkSource().getGenerator(), world.getRandom(), pos);
				}
			}
		}
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}


	@Override
	public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
		return world.random.nextFloat() < 0.45;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
		this.generateNew(world, pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		builder.add(STAGE);
	}

}
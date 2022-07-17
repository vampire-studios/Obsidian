package io.github.vampirestudios.obsidian.api.obsidian.block;

import io.github.vampirestudios.vampirelib.api.itemGroupSorting.VanillaTargetedItemGroupFiller;
import net.minecraft.block.BlockState;
import net.minecraft.block.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class SaplingBaseBlock extends PlantBlock implements Fertilizable {
	public static final IntProperty STAGE = Properties.STAGE;
	protected static final VoxelShape SHAPE = net.minecraft.block.Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    private final VanillaTargetedItemGroupFiller FILLER;
	private final Block block;

    public SaplingBaseBlock(Block block) {
        super(AbstractBlock.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS));
		this.block = block;
		this.setDefaultState(this.stateManager.getDefaultState().with(STAGE, 0));
        FILLER = new VanillaTargetedItemGroupFiller(Blocks.DARK_OAK_SAPLING);
    }

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
        FILLER.fillItem(this.asItem(), group, list);
    }

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		if (world.getLightLevel(pos.up()) >= 9 && random.nextInt(7) == 0) {
			this.generateNew(world, pos, state);
		}
	}

	public void generateNew(ServerWorld world, BlockPos pos, BlockState state) {
		if (state.get(STAGE) == 0) {
			world.setBlockState(pos, state.cycle(STAGE), 4);
		} else {
			if (block.placable_feature != null) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				if (world.getRegistryManager().get(Registry.CONFIGURED_FEATURE_KEY).containsId(block.placable_feature)) {
					ConfiguredFeature<?, ?> feature = world.getRegistryManager().get(Registry.CONFIGURED_FEATURE_KEY).get(block.placable_feature);
					assert feature != null;
					feature.generate(world, world.getChunkManager().getChunkGenerator(), world.getRandom(), pos);
				}
			}
		}
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canGrow(World world, RandomGenerator random, BlockPos pos, BlockState state) {
		return world.random.nextFloat() < 0.45;
	}

	@Override
	public void grow(ServerWorld world, RandomGenerator random, BlockPos pos, BlockState state) {
		this.generateNew(world, pos, state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
		builder.add(STAGE);
	}

}
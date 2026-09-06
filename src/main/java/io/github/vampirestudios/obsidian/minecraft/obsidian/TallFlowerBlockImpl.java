package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class TallFlowerBlockImpl extends DoublePlantBlock implements BonemealableBlock {

	/**
	 * The age property for the plant currently being constructed, or null when it does not grow.
	 * {@link #createBlockStateDefinition} runs from the block constructor, before this subclass can assign
	 * a field, so the property has to be handed over out-of-band.
	 */
	private static final ThreadLocal<IntegerProperty> CONSTRUCTING_AGE = new ThreadLocal<>();

	private final Block block;
	/** Null for a plant that does not grow, which is every one that declares no {@code growable}. */
	private final IntegerProperty age;

	public TallFlowerBlockImpl(Block block, BlockBehaviour.Properties settings) {
		super(prepare(block, settings.dynamicShape()));
		this.block = block;
		this.age = CONSTRUCTING_AGE.get();
		CONSTRUCTING_AGE.remove();

		if (age != null) {
			this.registerDefaultState(this.defaultBlockState().setValue(age, block.growable.min_age));
		}
	}

	/** Builds the age property and stashes it for {@link #createBlockStateDefinition}. */
	private static BlockBehaviour.Properties prepare(Block block, BlockBehaviour.Properties settings) {
		CONSTRUCTING_AGE.set(block.growable == null ? null
				: IntegerProperty.create("age", block.growable.min_age,
						Math.max(block.growable.min_age + 1, block.growable.max_age)));
		return settings;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		IntegerProperty constructing = CONSTRUCTING_AGE.get();
		if (constructing != null) builder.add(constructing);
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		return block.information.getBlockSettings() != null ? !block.information.getBlockSettings().translucent ? 0.2F : 1.0F : super.getShadeBrightness(state, world, pos);
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
		return block.information.getBlockSettings() != null ? !block.information.getBlockSettings().translucent : super.isCollisionShapeFullBlock(state, world, pos);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state) {
		return block.information.getBlockSettings() != null ? block.information.getBlockSettings().translucent : super.propagatesSkylightDown(state);
	}

	public IntegerProperty getAgeProperty() {
		return age;
	}

	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return false;
	}

	public int getMaxAge() {
		return block.growable.max_age;
	}

	protected int getAge(BlockState state) {
		return state.getValue(this.getAgeProperty());
	}

	public BlockState withAge(int age) {
		return this.defaultBlockState().setValue(this.getAgeProperty(), age);
	}

	public boolean isMature(BlockState state) {
		return state.getValue(this.getAgeProperty()) >= this.getMaxAge();
	}

	public boolean isRandomlyTicking(BlockState state) {
		if (block.growable != null) {
			return !this.isMature(state);
		}
		return false;
	}

	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (age == null) return;
		if (world.getRawBrightness(pos, 0) >= 9) {
			int i = this.getAge(state);
			if (i < this.getMaxAge()) {
				float f = 1.0F;
				if (random.nextInt((int) (25.0F / f) + 1) == 0) {
					world.setBlock(pos, this.withAge(i + 1), 2);
				}
			}
		}

	}

	public void applyGrowth(Level world, BlockPos pos, BlockState state) {
		int i = this.getAge(state) + this.getGrowthAmount(world);
		int j = this.getMaxAge();
		if (i > j) {
			i = j;
		}
		world.setBlock(pos, this.withAge(i), 2);
	}

	protected int getGrowthAmount(Level world) {
		return Mth.nextInt(world.getRandom(), 2, 5);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
		popResource(level, pos, new ItemStack(this));
	}
}
